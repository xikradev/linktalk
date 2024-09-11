package org.acme.model.bo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.acme.model.dao.ConversationDAO;
import org.acme.model.dao.MessageDAO;
import org.acme.model.dao.UserDAO;
import org.acme.model.dto.MessageResponseDTO;
import org.acme.model.entity.Conversation;
import org.acme.model.entity.Message;
import org.acme.model.entity.User;

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

    @Inject
    UserDAO userDAO;

    public List<MessageResponseDTO> getConversationMessages(Long conversationId) {
        Conversation conversation = conversationDAO.findById(conversationId);
        List<Message> messages = messageDAO.getMessagesByConversation(conversation);
        List<MessageResponseDTO> messageResponseDTOS = new ArrayList<>();
        for (Message message : messages) {
            MessageResponseDTO messageResponseDTO = new MessageResponseDTO();
            messageResponseDTO.setId(message.getId());
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
    @Transactional
    public void deleteMessageById(Long messageId) {
        Message message = messageDAO.findById(messageId);
        if (message != null) {
            messageDAO.delete(message);
        } else {
            throw new NotFoundException("Mensagem com ID " + messageId + " não encontrada.");
        }
    }

    @Transactional
    public Message sendMessage(Long conversationId, Long senderId, String senderEmail, Long currentTimeMillis,
                               String content) {
        Conversation conversation = conversationDAO.findById(conversationId);
        User sender = userDAO.findById(senderId);

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setSenderEmail(senderEmail);
        message.setContent(content);
        message.setTimestamp(currentTimeMillis);
        messageDAO.saveMessage(message);

        return message;
    }

    @Transactional
    public void updateMessageContent(Long messageId, String newContent) {
        Message message = messageDAO.findById(messageId);
        if (message != null) {
            message.setContent(newContent); // Atualizando o conteúdo da mensagem
            messageDAO.update(message);     // Salvando a mensagem com o novo conteúdo
        } else {
            throw new NotFoundException("Mensagem com ID " + messageId + " não encontrada.");
        }
    }
}
