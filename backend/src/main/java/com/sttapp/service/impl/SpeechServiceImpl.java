package com.sttapp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sttapp.dto.TranscriptionResponse;
import com.sttapp.entity.Transcription;
import com.sttapp.entity.User;
import com.sttapp.repository.TranscriptionRepository;
import com.sttapp.repository.UserRepository;
import com.sttapp.service.SpeechService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the SpeechService.
 * Handles the logic for converting audio to text and managing history.
 */
@Service
public class SpeechServiceImpl implements SpeechService {

    @Value("${deepgram.api.key}")
    private String deepgramApiKey;

    private final TranscriptionRepository transcriptionRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public SpeechServiceImpl(TranscriptionRepository transcriptionRepository, UserRepository userRepository, ObjectMapper objectMapper) {
        this.transcriptionRepository = transcriptionRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public TranscriptionResponse processAudio(MultipartFile file, String userEmail) {
        User user = getUser(userEmail);
        String fileName = file.getOriginalFilename();
        String generatedTranscript = "";

        try {
            // Determine Content-Type
            String contentType = file.getContentType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = "application/octet-stream";
            }

            // 1. Build Deepgram API HTTP Request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.deepgram.com/v1/listen?smart_format=true&model=nova-2&language=en"))
                    .header("Authorization", "Token " + deepgramApiKey)
                    .header("Content-Type", contentType)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                    .build();

            // 2. Execute Request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Deepgram API returned an error: " + response.body());
            }

            // 3. Parse JSON Response
            JsonNode rootNode = objectMapper.readTree(response.body());
            JsonNode channels = rootNode.path("results").path("channels");
            
            if (channels.isArray() && channels.size() > 0) {
                JsonNode alternatives = channels.get(0).path("alternatives");
                if (alternatives.isArray() && alternatives.size() > 0) {
                    generatedTranscript = alternatives.get(0).path("transcript").asText();
                }
            }

            if (generatedTranscript.isEmpty()) {
                generatedTranscript = "[No speech detected]";
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to process audio with Deepgram STT: " + e.getMessage(), e);
        }

        // 4. Save the result to the PostgreSQL database
        Transcription transcription = Transcription.builder()
                .user(user)
                .audioFile(fileName)
                .transcript(generatedTranscript)
                .build();

        transcription = transcriptionRepository.save(transcription);

        return mapToResponse(transcription);
    }

    @Override
    public List<TranscriptionResponse> getUserTranscriptionHistory(String userEmail) {
        User user = getUser(userEmail);
        
        return transcriptionRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TranscriptionResponse getTranscriptionById(Long id, String userEmail) {
        Transcription transcription = getOwnedTranscription(id, userEmail);
        return mapToResponse(transcription);
    }

    @Override
    public void deleteTranscription(Long id, String userEmail) {
        Transcription transcription = getOwnedTranscription(id, userEmail);
        transcriptionRepository.delete(transcription);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Transcription getOwnedTranscription(Long id, String email) {
        Transcription transcription = transcriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transcription not found"));

        if (!transcription.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Access Denied: You do not own this transcription.");
        }
        return transcription;
    }

    private TranscriptionResponse mapToResponse(Transcription t) {
        return TranscriptionResponse.builder()
                .id(t.getId())
                .audioFile(t.getAudioFile())
                .transcript(t.getTranscript())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
