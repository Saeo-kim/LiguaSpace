package com.example.linguaspace.korean;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KoreanEntryRepository
        extends JpaRepository<KoreanEntry, Long> {

    List<KoreanEntry> findAllByOrderByCreatedAtDesc();
}