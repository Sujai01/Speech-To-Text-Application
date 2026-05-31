package com.sttapp.service.impl;

import com.sttapp.dto.TranscriptionResponse;
import com.sttapp.entity.Transcription;
import com.sttapp.entity.User;
import com.sttapp.repository.TranscriptionRepository;
import com.sttapp.repository.UserRepository;
import com.sttapp.service.SpeechService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the SpeechService.
 * Handles the logic for converting audio to text and managing history.
 */
@Service
@RequiredArgsConstructor
public class SpeechServiceImpl implements SpeechService {

    private final TranscriptionRepository transcriptionRepository;
    private final UserRepository userRepository;

    @Override
    public TranscriptionResponse processAudio(MultipartFile file, String userEmail) {
        User user = getUser(userEmail);

        // 1. Extract file info (In production, you'd upload this to an S3 bucket or Google Cloud Storage first)
        String fileName = file.getOriginalFilename();

        // 2. TODO: Call actual Google Cloud Speech-to-Text API
        // Because GCP requires a JSON key file to work, we are mocking the text return for now.
        // Once credentials are added to application.properties, the Cloud SDK code goes here.
        String generatedTranscript = "This is a simulated transcript for the audio file: " + fileName 
                + ". In a production environment with valid GCP credentials, this will be actual text.";

        // 3. Save the result to the PostgreSQL database
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
        
        // Fetch from DB, map Entity to DTO, and return as a List
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

    // --- Private Helper Methods ---

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Security Check: Ensures a user can only access their own records
    private Transcription getOwnedTranscription(Long id, String email) {
        Transcription transcription = transcriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transcription not found"));

        if (!transcription.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Access Denied: You do not own this transcription.");
        }
        return transcription;
    }

    // Cleanly converts our Database Entity into the Frontend DTO
    private TranscriptionResponse mapToResponse(Transcription t) {
        return TranscriptionResponse.builder()
                .id(t.getId())
                .audioFile(t.getAudioFile())
                .transcript(t.getTranscript())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
