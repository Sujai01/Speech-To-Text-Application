package com.sttapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for outgoing transcription data.
 * Used when sending speech-to-text results or history to the client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranscriptionResponse {

    private Long id;

    // The name/url of the audio file that was processed
    private String audioFile;

    // The final text result of the speech-to-text conversion
    private String transcript;

    // When the transcription occurred
    private LocalDateTime createdAt;
}
