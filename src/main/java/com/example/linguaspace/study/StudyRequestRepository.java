
package com.example.linguaspace.study;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudyRequestRepository
        extends JpaRepository<StudyRequest, Long> {

    List<StudyRequest> findAllByOrderByCreatedAtDesc();
}
