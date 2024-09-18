package org.acme.model.bo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.acme.model.dao.*;
import org.acme.model.dto.MessageResponseDTO;
import org.acme.model.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    GroupDAO groupDAO;
    @Inject
    MessageDAO messageDAO;
    @Inject
    ImageDAO imageDAO;

    @Inject
    AuditLogDAO auditLogDAO;

    @Inject
    UserDAO userDAO;

    public List<MessageResponseDTO> getConversationMessages(Long conversationId) {
        Conversation conversation = conversationDAO.findById(conversationId);
        List<Object[]> results = messageDAO.getMessagesByConversation(conversation);
        List<MessageResponseDTO> messageResponseDTOS = new ArrayList<>();
        for (Object[] result : results) {
            Message message = (Message) result[0]; // O primeiro elemento é a entidade Message
            String imageUrl = (String) result[1];  // O segundo elemento é a URL da imagem ou null

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
            messageResponseDTO.setImgUrl(imageUrl);
            messageResponseDTOS.add(messageResponseDTO);
        }
        return messageResponseDTOS;
    }

    public List<MessageResponseDTO> getGroupMessages(Long groupId) {
        Group group = groupDAO.findById(groupId);
        List<Object[]> results = messageDAO.getMessagesByGroup(group);
        List<MessageResponseDTO> messageResponseDTOS = new ArrayList<>();
        for (Object[] result : results) {
            Message message = (Message) result[0]; // O primeiro elemento é a entidade Message
            String imageUrl = (String) result[1];  // O segundo elemento é a URL da imagem ou null

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
            messageResponseDTO.setImgUrl(imageUrl);
            messageResponseDTOS.add(messageResponseDTO);
        }
        return messageResponseDTOS;
    }
    @Transactional
    public void deleteMessageById(Long messageId) {
        Message message = messageDAO.findById(messageId);
        Image image = imageDAO.findByMessageId(message);
        if(image != null){
            imageDAO.delete(image);
        }
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
    public Message sendMessageToGroup(Long groupId, Long senderId, String senderEmail, Long currentTimeMillis,
                               String content) {
        Group group = groupDAO.findById(groupId);
        User sender = userDAO.findById(senderId);

        Message message = new Message();
        message.setGroup(group);
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
