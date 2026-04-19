package com.lifeAIFrontend.LifeAIFrontend.model;

public class ChatResponse {

    private String reply;
    private boolean success;
    private String error;

    public ChatResponse() {}

    public ChatResponse(String reply) {
        this.reply = reply;
        this.success = true;
    }

    public ChatResponse(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}