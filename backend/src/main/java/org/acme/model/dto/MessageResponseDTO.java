package org.acme.model.dto;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.acme.model.entity.Conversation;
import org.acme.model.entity.User;

public class MessageResponseDTO {

    private Conversation conversation;
    private User sender;
    private String content;
    private Long timestamp;
}
