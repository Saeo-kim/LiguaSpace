package com.example.linguaspace.english;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnglishMemoRepository extends JpaRepository<EnglishMemo, Long> {

    List<EnglishMemo> findAllByOrderByCreatedAtDesc();
}
