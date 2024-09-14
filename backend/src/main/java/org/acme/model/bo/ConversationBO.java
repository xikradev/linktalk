package org.acme.model.bo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.acme.audit.Auditable;
import org.acme.model.dao.ConversationDAO;
import org.acme.model.dao.MessageDAO;
import org.acme.model.dao.UserDAO;
import org.acme.model.entity.Conversation;
import org.acme.model.entity.User;

@ApplicationScoped
public class ConversationBO {

    @Inject
    ConversationDAO conversationDAO;
    @Inject
    UserDAO userDAO;
    @Transactional
    @Auditable
    public Conversation startConversation(Long user1Id, Long user2Id) {
        User user1 = userDAO.findById(user1Id);
        User user2 = userDAO.findById(user2Id);
        if(user1 != null && user2!= null){
            return conversationDAO.createConversation(user1, user2);
        }else{
            throw new NotFoundException("Usuários com esses Id's não foram encontrados");
        }
    }
}
