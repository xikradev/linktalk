package org.acme.model.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.acme.model.entity.Conversation;
import org.acme.model.entity.User;

@ApplicationScoped
public class ConversationDAO {

    @Inject
    EntityManager em;

    @Transactional
    public Conversation createConversation(User user1, User user2){
        Conversation conversation = new Conversation();
        conversation.setUser1(user1);
        conversation.setUser2(user2);
        em.persist(conversation);
        return conversation;
    }

    public Conversation findByUsers(User user1, User user2){
        return em.createQuery("SELECT c FROM Conversation c WHERE " +
                "(c.user1 = :user1 AND c.user2 = :user2) OR " +
                "(c.user1 = :user2 AND c.user2 = :user1)", Conversation.class)
                .setParameter("user1", user1)
                .setParameter("user2", user2)
                .getSingleResult();
    }

    public Conversation findById(Long id){
        TypedQuery<Conversation> query =em.createQuery("SELECT c FROM Conversation c WHERE c.id = :id",Conversation.class);
        query.setParameter("id", id);
        return query.getResultStream().findFirst().orElse(null);
    }
}
