package com.example.exam.service;

import com.example.exam.enums.ExamType;
import com.example.exam.model.Exam;
import com.example.exam.model.Result;
import com.example.exam.model.SubmissionStatistics;
import com.example.exam.repository.ResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ResultService {
    private final ResultRepository resultRepository;
    private final SubmissionService submissionService;
    private final UserExamRecordService userExamRecordService;

    @Transactional(readOnly = true)
    Optional<Result> findByExamId(final int examId, String examinee) {
        return resultRepository.findByExamIdAndExaminee(examId, examinee);
    }

    private Result buildResult(Exam exam, String examinee, SubmissionStatistics statistics) {
        Result result = new Result();
        result.setExamId(exam.getId());
        result.setExamType(exam.getExamType());
        result.setExaminee(examinee);
        result.setMarksObtained(calculateMarks(statistics));
        return result;
    }

    public void save(Result result) {
        resultRepository.save(result);
    }

    public void save(List<Result> results) {
        resultRepository.saveAll(results);
    }

    private double calculateMarks(SubmissionStatistics statistics) {
        return statistics.getCorrect() - (statistics.getWrong() * 0.5);
    }

    public void updateMark(Exam exam, String examinee) {
        SubmissionStatistics statistics = submissionService.getStatistics(exam, examinee);

        Result result = buildResult(exam, examinee, statistics);
        save(result);
    }

    private void handlePracticeExam(Exam exam) {
        if (userExamRecordService.hasUserExitedFromTheExam(exam.getId())) {
            log.info("User exited from the exam and mark is already updated");
        } else {
            updateMark(exam, exam.getExaminee());
        }
    }

    public void updateMark(Exam exam) {
        if (ExamType.isPractice(exam.getExamType())) {
            handlePracticeExam(exam);
            return;
        }

        Map<String, SubmissionStatistics> statisticsMap = submissionService.getExamStatistics(exam);
        List<Result> results = statisticsMap.entrySet()
                .stream()
                .map(entry -> buildResult(exam, entry.getKey(), entry.getValue()))
                .toList();
        save(results);
    }
}