package com.examapp.examapp.service;

import com.examapp.examapp.entity.Exam;
import com.examapp.examapp.repository.ExamRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamService {

    private final ExamRepository examRepository;

    public ExamService(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    public Exam createExam(Exam exam) {
        return examRepository.save(exam);
    }

    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    public Exam getExamById(Long id) {
        try {
            return examRepository.findById(id).get();
        } catch (Exception e) {
            System.out.println("Exam not found with id: " + id);
            throw new RuntimeException("Exam not found with id: " + id);
        }
    }

    public Exam updateExam(Long id, Exam updatedExam) {
        Exam exam = getExamById(id);
        exam.setTitle(updatedExam.getTitle());
        exam.setTotalMarks(updatedExam.getTotalMarks());
        return examRepository.save(exam);
    }

    public void deleteExam(Long id) {
        examRepository.deleteById(id);
    }
}