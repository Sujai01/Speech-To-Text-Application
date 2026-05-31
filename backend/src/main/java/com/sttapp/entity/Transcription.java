package com.sttapp.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "transcriptions")
public class Transcription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "audio_file", nullable = false)
    private String audioFile;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String transcript;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Transcription() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getAudioFile() { return audioFile; }
    public void setAudioFile(String audioFile) { this.audioFile = audioFile; }
    public String getTranscript() { return transcript; }
    public void setTranscript(String transcript) { this.transcript = transcript; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static TranscriptionBuilder builder() {
        return new TranscriptionBuilder();
    }

    public static class TranscriptionBuilder {
        private User user;
        private String audioFile;
        private String transcript;

        public TranscriptionBuilder user(User user) {
            this.user = user;
            return this;
        }

        public TranscriptionBuilder audioFile(String audioFile) {
            this.audioFile = audioFile;
            return this;
        }

        public TranscriptionBuilder transcript(String transcript) {
            this.transcript = transcript;
            return this;
        }

        public Transcription build() {
            Transcription t = new Transcription();
            t.setUser(this.user);
            t.setAudioFile(this.audioFile);
            t.setTranscript(this.transcript);
            return t;
        }
    }
}
