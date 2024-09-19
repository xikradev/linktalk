package org.acme.model.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.acme.model.entity.Group;
import org.acme.model.entity.GroupAdmin;
import org.acme.model.entity.User;

@ApplicationScoped
public class GroupAdminDAO {

    @Inject
    EntityManager em;

    public GroupAdmin saveGroupAdmin (User user, Group group){
        GroupAdmin groupAdmin = new GroupAdmin();
        groupAdmin.setGroup(group);
        groupAdmin.setUser(user);
        em.persist(groupAdmin);

        return groupAdmin;
    }
}
