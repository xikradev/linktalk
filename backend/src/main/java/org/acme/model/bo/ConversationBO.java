package org.acme.model.bo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.acme.controller.ConversationController;
import org.acme.model.dao.ConversationDAO;
import org.acme.model.dao.ImageDAO;
import org.acme.model.dao.MessageDAO;
import org.acme.model.dao.UserDAO;
import org.acme.model.entity.Conversation;
import org.acme.model.entity.Image;
import org.acme.model.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class ConversationBO {

    @Inject
    ConversationDAO conversationDAO;
    @Inject
    UserDAO userDAO;
    @Inject
    ImageDAO imageDAO;

    @Inject
    AuditLogBO auditLogBO;
    @Transactional
    public void deleteConversationById(Long conversationId) {
        Conversation conversation = conversationDAO.findById(conversationId);
        if(conversation != null){
            List<Image> images = imageDAO.getImagesByConversation(conversationId);
            for(Image image : images){
                imageDAO.delete(image);
            }
            conversationDAO.delete(conversation);
        }else {
            throw new NotFoundException("Conversa com ID " + conversationId + " não encontrada.");
        }
    }

    @Transactional
    public Conversation startConversation(Long user1Id, Long user2Id, String emailToken) {
        User user1 = userDAO.findById(user1Id);
        User user2 = userDAO.findById(user2Id);
        if(user1 != null && user2!= null){
            return conversationDAO.createConversation(user1, user2);
        }else{
            auditLogBO.logToDatabase("START_CONVERSATION_REQUEST", emailToken, LocalDateTime.now(), ConversationController.class);
            throw new NotFoundException("Usuários com esses Id's não foram encontrados");
        }
    }
}
