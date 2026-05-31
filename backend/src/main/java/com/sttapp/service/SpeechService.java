package com.sttapp.service;

import com.sttapp.dto.TranscriptionResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for handling Speech-to-Text business logic.
 */
public interface SpeechService {

    /**
     * Uploads an audio file, sends it to Google Cloud Speech-to-Text API,
     * saves the transcript to the database, and returns the result.
     *
     * @param file the uploaded audio file (MultipartFile)
     * @param userEmail the email of the currently authenticated user
     * @return TranscriptionResponse containing the text result
     */
    TranscriptionResponse processAudio(MultipartFile file, String userEmail);

    /**
     * Retrieves the entire transcription history for the logged-in user.
     *
     * @param userEmail the email of the currently authenticated user
     * @return a list of their past transcriptions
     */
    List<TranscriptionResponse> getUserTranscriptionHistory(String userEmail);

    /**
     * Retrieves a single, specific transcription record.
     * 
     * @param id the ID of the transcription
     * @param userEmail the email of the user (to verify they own it)
     * @return TranscriptionResponse
     */
    TranscriptionResponse getTranscriptionById(Long id, String userEmail);

    /**
     * Deletes a specific transcription from the database.
     *
     * @param id the ID of the transcription
     * @param userEmail the email of the user (to verify they own it)
     */
    void deleteTranscription(Long id, String userEmail);
}
