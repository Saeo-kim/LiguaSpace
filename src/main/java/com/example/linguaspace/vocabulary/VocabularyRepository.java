package com.example.linguaspace.vocabulary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface VocabularyRepository extends JpaRepository<VocabularyEntry, Long> {

    List<VocabularyEntry> findByLanguageAndNormalizedWordIn(String language, Collection<String> normalizedWords);

    // Empty strings mean "no filter". Null parameters would make PostgreSQL fail to infer their type.
    @Query("""
            select v from VocabularyEntry v
            where (:language = '' or v.language = :language)
              and (:pattern = ''
                   or v.normalizedWord like :pattern escape '!'
                   or lower(v.meaning) like :pattern escape '!'
                   or lower(v.example) like :pattern escape '!'
                   or lower(v.translation) like :pattern escape '!'
                   or lower(v.notes) like :pattern escape '!'
                   or lower(v.category) like :pattern escape '!')
            """)
    Page<VocabularyEntry> search(
            @Param("language") String language,
            @Param("pattern") String pattern,
            Pageable pageable
    );
}
