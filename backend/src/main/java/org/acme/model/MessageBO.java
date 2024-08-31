package org.acme.model;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.model.dao.ConversationDAO;
import org.acme.model.dao.MessageDAO;
import org.acme.model.dto.MessageResponseDTO;
import org.acme.model.entity.Conversation;
import org.acme.model.entity.Message;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MessageBO {

    @Inject
    ConversationDAO conversationDAO;
    @Inject
    MessageDAO messageDAO;

    public List<MessageResponseDTO> getConversationMessages(Long conversationId) {
        Conversation conversation = conversationDAO.findById(conversationId);
        List<Message> messages = messageDAO.getMessagesByConversation(conversation);
        List<MessageResponseDTO> messageResponseDTOS = new ArrayList<>();
        for (Message message : messages) {
            MessageResponseDTO messageResponseDTO = new MessageResponseDTO();
            messageResponseDTO.setContent(message.getContent());
            messageResponseDTO.setSenderEmail(message.getSender().getEmail());
            LocalTime time = Instant.ofEpochMilli(message.getTimestamp())
                    .atZone(ZoneId.systemDefault())
                    .toLocalTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            String formattedTime = time.format(formatter);
            messageResponseDTO.setTimeSented(formattedTime);
            messageResponseDTOS.add(messageResponseDTO);
        }
        return messageResponseDTOS;
    }
}
