package org.acme.model.bo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.model.dao.ConversationDAO;
import org.acme.model.dao.MessageDAO;
import org.acme.model.dao.UserDAO;
import org.acme.model.entity.Conversation;
import org.acme.model.entity.Message;
import org.acme.model.entity.User;

import java.util.List;

@ApplicationScoped
public class ChatBO {
    @Inject
    ConversationDAO conversationDAO;

    @Inject
    MessageDAO messageDAO;

    @Inject
    UserDAO userDAO;

    public Conversation startConversation(Long user1Id, Long user2Id){
        User user1 = userDAO.findById(user1Id);
        User user2 = userDAO.findById(user2Id);
        return conversationDAO.createConversation(user1, user2);
    }
    @Transactional
    public void sendMessage(Long conversationId, Long senderId, String senderEmail,String content){
        Conversation conversation = conversationDAO.findById(conversationId);
        User sender = userDAO.findById(senderId);

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setSenderEmail(senderEmail);
        message.setContent(content);
        message.setTimestamp(System.currentTimeMillis());
        messageDAO.saveMessage(message);
    }

    public List<Message> getConversationMessages(Long conversationId) {
        Conversation conversation = conversationDAO.findById(conversationId);
        return messageDAO.getMessagesByConversation(conversation);
    }
}
