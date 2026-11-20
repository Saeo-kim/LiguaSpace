package com.example.linguaspace.english;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnglishWordRepository extends JpaRepository<EnglishWord, Long> {

    List<EnglishWord> findAllByOrderByCreatedAtDesc();
}
