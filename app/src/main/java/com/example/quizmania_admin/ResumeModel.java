package com.example.quizmania_admin;

public class ResumeModel {
    String fullName;
    String resume;

    public ResumeModel() {
    }

    public ResumeModel(String resume, String fullName) {
        this.resume = resume;
        this.fullName = fullName;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
