package com.example.triviafest.model;

public class Question {
    private String question;
    private boolean answerTrue;

    public Question() {
    }

    public Question(String question, boolean answerTrue) {
        this.question = question;
        this.answerTrue = answerTrue;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean isAnswerTrue() {
        return answerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        this.answerTrue = answerTrue;
    }

    @Override
    public String toString() {
        return "Question{" +
                "question='" + question + '\'' +
                ", answerTrue=" + answerTrue +
                '}';
    }
}
