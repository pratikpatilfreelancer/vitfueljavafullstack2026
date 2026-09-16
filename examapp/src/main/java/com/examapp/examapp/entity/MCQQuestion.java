package com.examapp.examapp.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("MCQ")
public class MCQQuestion extends Question {

    @ElementCollection
    private List<String> options;

    private int correctIndex;

    @Override
    public boolean evaluate(String answer) {
        int selectedIndex = Integer.parseInt(answer);
        return selectedIndex == correctIndex;
    }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public int getCorrectIndex() { return correctIndex; }
    public void setCorrectIndex(int correctIndex) { this.correctIndex = correctIndex; }
}