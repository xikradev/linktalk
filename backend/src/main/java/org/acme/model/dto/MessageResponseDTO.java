package org.acme.model.dto;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.acme.model.entity.Conversation;
import org.acme.model.entity.User;

public class MessageResponseDTO {

    private Long id;
    private String senderEmail;
    private String content;
    private String timeSented;
    private String imgUrl;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
