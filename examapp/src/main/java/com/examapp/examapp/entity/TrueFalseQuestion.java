package com.examapp.examapp.entity;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("TRUEFALSE")
public class TrueFalseQuestion extends Question {

    private boolean correctBoolean;

    @Override
    public boolean evaluate(String answer) {
        boolean selected = Boolean.parseBoolean(answer);
        return selected == correctBoolean;
    }

    public boolean isCorrectBoolean() { return correctBoolean; }
    public void setCorrectBoolean(boolean correctBoolean) { this.correctBoolean = correctBoolean; }
}