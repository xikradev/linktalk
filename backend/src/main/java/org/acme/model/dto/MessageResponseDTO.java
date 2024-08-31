package org.acme.model.dto;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.acme.model.entity.Conversation;
import org.acme.model.entity.User;

public class MessageResponseDTO {

    private String senderEmail;
    private String content;
    private String timeSented;

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimeSented() {
        return timeSented;
    }

    public void setTimeSented(String timeSented) {
        this.timeSented = timeSented;
    }
}
