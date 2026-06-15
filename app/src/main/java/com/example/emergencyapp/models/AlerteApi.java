package com.example.emergencyapp.models;

public class AlerteApi {

    private int id;
    private String title;
    private String body;
    private int userId;

    public AlerteApi() {}

    public AlerteApi(String title, String body, int userId) {
        this.title = title;
        this.body = body;
        this.userId = userId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}
