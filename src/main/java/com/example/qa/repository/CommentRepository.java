package com.example.qa.repository;

import com.example.qa.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findAllByQuestionId(int questionId, Pageable pageable);

    void deleteAllByQuestionId(int questionId);
}
