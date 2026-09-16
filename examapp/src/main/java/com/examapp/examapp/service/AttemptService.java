package com.examapp.examapp.service;

import com.examapp.examapp.entity.*;
import com.examapp.examapp.repository.AttemptRepository;
import com.examapp.examapp.repository.ExamRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class AttemptService {

    private final ExamRepository examRepository;
    private final AttemptRepository attemptRepository;

    public AttemptService(ExamRepository examRepository, AttemptRepository attemptRepository) {
        this.examRepository = examRepository;
        this.attemptRepository = attemptRepository;
    }

    public Attempt submitAttempt(Long examId, String candidateName, Map<Long, String> answers) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        List<Question> questions = exam.getQuestions();
        int score = 0;

        try {
            for (Question question : questions) {
                String userAnswer = answers.get(question.getId());
                if (userAnswer != null && question.evaluate(userAnswer)) {
                    score++;
                }
            }
        } catch (Exception e) {
            System.out.println("Error while evaluating answers: " + e.getMessage());
        }

        Attempt attempt = new Attempt();
        attempt.setExam(exam);
        attempt.setCandidateName(candidateName);
        attempt.setScore(score);
        attempt.setSubmittedOn(LocalDateTime.now());

        return attemptRepository.save(attempt);
    }
}