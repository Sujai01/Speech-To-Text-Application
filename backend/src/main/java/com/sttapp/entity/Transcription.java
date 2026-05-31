package com.sttapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a Speech-to-Text Transcription.
 * Maps to the "transcriptions" table in PostgreSQL.
 */
@Entity
@Table(name = "transcriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transcription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many-to-One relationship: Many transcriptions belong to one user.
    // FetchType.LAZY ensures we only load the user data when explicitly requested, saving memory.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Stores the path or URL where the uploaded audio file is saved
    @Column(name = "audio_file", nullable = false)
    private String audioFile;

    // We use columnDefinition = "TEXT" because transcripts can be very long 
    // and exceed standard VARCHAR(255) limits.
    @Column(columnDefinition = "TEXT", nullable = false)
    private String transcript;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
