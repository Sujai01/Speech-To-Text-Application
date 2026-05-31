package com.sttapp.controller;

import com.sttapp.dto.TranscriptionResponse;
import com.sttapp.service.SpeechService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST Controller exposing endpoints for Speech-to-Text operations.
 */
@RestController
@RequestMapping("/api/speech")
public class SpeechController {

    private final SpeechService speechService;

    public SpeechController(SpeechService speechService) {
        this.speechService = speechService;
    }

    /**
     * Uploads an audio file for speech-to-text conversion.
     * POST /api/speech/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<TranscriptionResponse> uploadAudio(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        String userEmail = authentication.getName();
        TranscriptionResponse response = speechService.processAudio(file, userEmail);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the current user's transcription history.
     * GET /api/speech/history
     */
    @GetMapping("/history")
    public ResponseEntity<List<TranscriptionResponse>> getHistory(Authentication authentication) {
        String userEmail = authentication.getName();
        List<TranscriptionResponse> history = speechService.getUserTranscriptionHistory(userEmail);
        return ResponseEntity.ok(history);
    }

    /**
     * Retrieves a specific transcription by ID.
     * GET /api/speech/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TranscriptionResponse> getTranscription(
            @PathVariable Long id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        TranscriptionResponse response = speechService.getTranscriptionById(id, userEmail);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a specific transcription by ID.
     * DELETE /api/speech/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTranscription(
            @PathVariable Long id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        speechService.deleteTranscription(id, userEmail);
        return ResponseEntity.noContent().build();
    }
}
