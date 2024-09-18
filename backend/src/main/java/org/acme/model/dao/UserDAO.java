package org.acme.model.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.NotFoundException;
import org.acme.model.dto.UserContactDTO;
import org.acme.model.entity.Group;
import org.acme.model.entity.User;

import java.util.List;

@ApplicationScoped
public class UserDAO {

    @Inject
    EntityManager entityManager;

    public User findByEmail(String email) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        return query.getResultStream().findFirst().orElse(null);
    }

    public User findById(Long id) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u WHERE u.id = :id", User.class);
        query.setParameter("id", id);
        return query.getResultStream().findFirst().orElse(null);
    }

    public String findUserEmailById(Long id) {
        try {
            TypedQuery<String> query = entityManager.createQuery(
                    "SELECT u.email FROM User u WHERE u.id = :id", String.class);
            query.setParameter("id", id);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<UserContactDTO> contactsByUserId(Long id) {
        String queryStr = "SELECT new org.acme.model.dto.UserContactDTO(u.id, u.fullName, u.email, c.id) " +
                "FROM Conversation c " +
                "JOIN User u ON (u.id = c.user1.id OR u.id = c.user2.id) " +
                "WHERE (c.user1.id = :userId OR c.user2.id = :userId) " +
                "AND u.id != :userId";
        try {
            TypedQuery<UserContactDTO> query = entityManager.createQuery(queryStr, UserContactDTO.class);
            query.setParameter("userId", id);

            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }

    }

    public void persist(User user) {
        entityManager.persist(user);
    }

    public List<Group> getUserGroups(Long userId){
        User user = entityManager.find(User.class, userId);
        if(user == null){
            throw new NotFoundException("Não foi possível encontrar um usuário com esse id: "+userId);
        }
        return user.getGroups();
    }

    public boolean isAdmin(Long userId, Long groupId){
        return entityManager.createQuery(
                        "SELECT COUNT(ga) FROM GroupAdmin ga WHERE ga.group.id = :groupId AND ga.user.id = :userId", Long.class)
                .setParameter("groupId", groupId)
                .setParameter("userId", userId)
                .getSingleResult() > 0;
    }
}
