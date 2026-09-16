package com.examapp.examapp.service;

import com.examapp.examapp.entity.*;
import com.examapp.examapp.repository.ExamRepository;
import com.examapp.examapp.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;

    public QuestionService(QuestionRepository questionRepository, ExamRepository examRepository) {
        this.questionRepository = questionRepository;
        this.examRepository = examRepository;
    }

    public MCQQuestion addMCQQuestion(Long examId, String text, List<String> options, int correctIndex) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        MCQQuestion question = new MCQQuestion();
        question.setQuestionText(text);
        question.setOptions(options);
        question.setCorrectIndex(correctIndex);
        question.setExam(exam);

        return questionRepository.save(question);
    }

    public TrueFalseQuestion addTrueFalseQuestion(Long examId, String text, boolean correctBoolean) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        TrueFalseQuestion question = new TrueFalseQuestion();
        question.setQuestionText(text);
        question.setCorrectBoolean(correctBoolean);
        question.setExam(exam);

        return questionRepository.save(question);
    }

    public List<Question> getQuestionsByExamId(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        return exam.getQuestions();
    }

    public Question getQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
    }

    public MCQQuestion updateMCQQuestion(Long id, String text, List<String> options, int correctIndex) {
        MCQQuestion question = (MCQQuestion) getQuestionById(id);
        question.setQuestionText(text);
        question.setOptions(options);
        question.setCorrectIndex(correctIndex);
        return questionRepository.save(question);
    }

    public TrueFalseQuestion updateTrueFalseQuestion(Long id, String text, boolean correctBoolean) {
        TrueFalseQuestion question = (TrueFalseQuestion) getQuestionById(id);
        question.setQuestionText(text);
        question.setCorrectBoolean(correctBoolean);
        return questionRepository.save(question);
    }

    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }
}