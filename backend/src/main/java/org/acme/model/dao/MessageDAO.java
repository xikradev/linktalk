package org.acme.model.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.acme.model.entity.Conversation;
import org.acme.model.entity.Message;

import java.util.List;

@ApplicationScoped
public class MessageDAO {
    @Inject
    EntityManager em;

    @Transactional
    public void saveMessage(Message message){
        em.persist(message);
    }

    public List<Message> getMessagesByConversation(Conversation conversation){
        return em.createQuery("SELECT m FROM Message m WHERE m.conversation = :conversation ORDER BY m.timestamp", Message.class)
                .setParameter("conversation", conversation)
                .getResultList();
    }
}
