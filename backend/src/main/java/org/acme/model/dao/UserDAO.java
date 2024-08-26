package org.acme.model.dao;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.acme.model.entity.User;

@ApplicationScoped
public class UserDAO {

    @Inject
    EntityManager entityManager;

    public User findByEmail(String email){
        TypedQuery<User> query =entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email",User.class);
        query.setParameter("email", email);
        return query.getResultStream().findFirst().orElse(null);
    }

    public void persist(User user){
        entityManager.persist(user);
    }
}
