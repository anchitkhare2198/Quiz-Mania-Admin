package com.example.quizmania_admin;

public class VideoModel {
    private String fullName;
    private String videoName;
    private String videoUrl;

    public VideoModel() {
    }

    public VideoModel(String fullName, String videoName, String videoUrl) {
        this.fullName = fullName;
        this.videoName = videoName;
        this.videoUrl = videoUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
