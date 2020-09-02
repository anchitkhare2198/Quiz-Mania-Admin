package com.example.quizmania_admin;

public class ReviewModel {
    private String fullName,review;

    public ReviewModel() {
    }

    public ReviewModel(String fullName, String review) {
        this.fullName = fullName;
        this.review = review;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
