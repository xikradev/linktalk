package org.acme.model.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.BadRequestException;
import org.acme.model.dto.GroupRequestDTO;
import org.acme.model.entity.Group;
import org.acme.model.entity.Message;
import org.acme.model.entity.User;

import java.util.List;

@ApplicationScoped
public class GroupDAO {
    @Inject
    EntityManager em;

    public Group saveGroup(List<Long> userIds, GroupRequestDTO groupRequestDTO){
        Group group = new Group();
        group.setName(groupRequestDTO.getName());

        // Busca os usuários pelos IDs fornecidos
        List<User> users = em.createQuery("SELECT u FROM User u WHERE u.id IN :userIds", User.class)
                .setParameter("userIds", userIds)
                .getResultList();
        if(userIds.size() != users.size()){
            throw new BadRequestException("Algum dos ids informados são inválidos");
        }

        // Adiciona os usuários ao grupo
        group.setMembers(users);

        // Persiste o grupo
        em.persist(group);

        return group;
    }

    public Group findById(Long id){
        return em.find(Group.class, id);
    }

    public void delete(Group group) {
        em.remove(em.contains(group) ? group : em.merge(group));
    }

    public void update(Group group){
        em.merge(group);
    }

    public void addUserToGroup(Group group, User user) {
        group.getMembers().add(user);
        em.merge(group);
    }

    public void removeUserFromGroup(Group group, User user) {
        if (group != null && user != null) {
            group.getMembers().remove(user);
            em.merge(group);
        }
    }

    public List<User> getGroupMembers(Group group) {
        return group.getMembers();
    }
}
