package com.sttapp.dto;

import java.time.LocalDateTime;

public class TranscriptionResponse {
    private Long id;
    private String audioFile;
    private String transcript;
    private LocalDateTime createdAt;

    public TranscriptionResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAudioFile() { return audioFile; }
    public void setAudioFile(String audioFile) { this.audioFile = audioFile; }
    public String getTranscript() { return transcript; }
    public void setTranscript(String transcript) { this.transcript = transcript; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static TranscriptionResponseBuilder builder() {
        return new TranscriptionResponseBuilder();
    }

    public static class TranscriptionResponseBuilder {
        private Long id;
        private String audioFile;
        private String transcript;
        private LocalDateTime createdAt;

        public TranscriptionResponseBuilder id(Long id) { this.id = id; return this; }
        public TranscriptionResponseBuilder audioFile(String audioFile) { this.audioFile = audioFile; return this; }
        public TranscriptionResponseBuilder transcript(String transcript) { this.transcript = transcript; return this; }
        public TranscriptionResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public TranscriptionResponse build() {
            TranscriptionResponse t = new TranscriptionResponse();
            t.setId(this.id);
            t.setAudioFile(this.audioFile);
            t.setTranscript(this.transcript);
            t.setCreatedAt(this.createdAt);
            return t;
        }
    }
}
