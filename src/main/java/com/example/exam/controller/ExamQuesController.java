package com.example.exam.controller;

import com.example.exam.entity.ExamQuestion;
import com.example.exam.model.ExamQuesRequest;
import com.example.exam.model.ExamQuesResponse;
import com.example.exam.model.ExamQuesResponseDto;
import com.example.exam.service.ExamQuesService;
import com.example.exam.service.ExamQuesValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/exams")
public class ExamQuesController {
    private final ExamQuesService examQuesService;
    private final ExamQuesValidationService validationService;

    @GetMapping("/{examId}/questions")
    public ExamQuesResponse getExamQuestions(
            @PathVariable final int examId,
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.ASC) final Pageable pageable) {

        return examQuesService.getExamQuestions(examId, pageable);
    }

    @GetMapping("/{examId}/questions/{topicId}")
    public List<ExamQuesResponseDto> getExamQuestions(
            @PathVariable final int examId,
            @PathVariable final int topicId) {

        return examQuesService.getExamQuestions(examId, topicId);
    }

    @GetMapping("/{examId}/questionCountMap")
    public Map<Integer, Long> getTopicIdToQuestionCountMap(@PathVariable final int examId) {
        return examQuesService.getTopicIdToQuestionCountMap(examId);
    }

    @PostMapping("/{examId}/questions")
    public ResponseEntity<?> addExamQuestion(@PathVariable final int examId,
            @Valid @RequestBody final List<ExamQuesRequest> requestList) {

        if (!validationService.canQuesBeAdded(examId, requestList.size())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Increase question limit in the exam section to add new questions");
        }

        try {
            List<ExamQuestion> examQuestionList = examQuesService.createExamQuestions(requestList);
            return ResponseEntity.status(HttpStatus.CREATED).body(examQuestionList);
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{examId}/questions/{id}")
    public ExamQuestion updateExamQuestion(
            @PathVariable int examId,
            @PathVariable final int id,
            @Valid @RequestBody final ExamQuesRequest request) {

        return examQuesService.updateExamQuestion(id, request);
    }

    @DeleteMapping("/{examId}/questions/{id}")
    public ResponseEntity<Void> deleteExamQuestion(@PathVariable final int examId, @PathVariable final int id) {
        examQuesService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
