package com.example.linguaspace.english;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnglishExpressionRepository extends JpaRepository<EnglishExpression, Long> {

    List<EnglishExpression> findAllByOrderByCreatedAtDesc();
}
