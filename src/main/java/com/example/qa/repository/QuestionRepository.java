package com.example.qa.repository;

import com.example.qa.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findAllByParentId(int parentId);
    Page<Question> findAllByTopicIdInAndVisibleTrue(List<Integer> topicIds, Pageable pageable);
}
