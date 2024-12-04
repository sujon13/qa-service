package com.example.exam.controller;

import com.example.exam.entity.ExamTaker;
import com.example.exam.model.ExamTakerRequest;
import com.example.exam.service.ExamTakerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/exam-takers")
public class ExamTakerController {
    private final ExamTakerService ExamTakerService;

    @PostMapping("")
    public ResponseEntity<ExamTaker> addExamTaker(@Valid @RequestBody final ExamTakerRequest request) {
        try {
            ExamTaker ExamTaker = ExamTakerService.addExamTaker(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ExamTaker);
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("")
    public List<ExamTaker> getAllExamTakers() {
        return ExamTakerService.findAll();
    }

    @GetMapping("/{id}")
    public ExamTaker getExamTakerById(@PathVariable final Integer id) {
        return ExamTakerService.getExamTaker(id);
    }

    @PutMapping("/{id}")
    public ExamTaker updateExamTaker(@PathVariable final int id, @Valid @RequestBody final ExamTakerRequest request) {
        return ExamTakerService.editExamTaker(id, request);
    }
}
