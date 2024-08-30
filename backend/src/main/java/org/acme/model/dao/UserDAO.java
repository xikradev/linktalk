package org.acme.model.dao;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
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

    public User findById(Long id){
        TypedQuery<User> query =entityManager.createQuery("SELECT u FROM User u WHERE u.id = :id",User.class);
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


    public void persist(User user){
        entityManager.persist(user);
    }
}
