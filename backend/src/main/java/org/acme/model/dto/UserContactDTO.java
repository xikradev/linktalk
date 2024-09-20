package org.acme.model.dto;

public class UserContactDTO {
    private Long id;
    private String fullName;
    private String email;
    private Long conversationId;

    public UserContactDTO() {
    }

    public UserContactDTO(Long id, String fullName, String email, Long conversationId) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.conversationId = conversationId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }
}
