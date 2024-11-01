package com.example.demo.service;

public class ProgressResponse {
    private String status;
    private boolean completed;

    public ProgressResponse(String status, boolean completed) {
        this.status = status;
        this.completed = completed;
    }

    public String getStatus() {
        return status;
    }

    public boolean isCompleted() {
        return completed;
    }
}