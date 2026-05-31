package com.sttapp.repository;

import com.sttapp.entity.Transcription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Transcription entity.
 * Provides database access operations for speech transcriptions.
 */
@Repository
public interface TranscriptionRepository extends JpaRepository<Transcription, Long> {

    /**
     * Finds all transcriptions belonging to a specific user.
     * The results are automatically ordered by the creation date in descending order,
     * so the most recent transcriptions appear first.
     *
     * @param userId the ID of the user who owns the transcriptions
     * @return a list of transcriptions belonging to the user
     */
    List<Transcription> findByUserIdOrderByCreatedAtDesc(Long userId);
}
