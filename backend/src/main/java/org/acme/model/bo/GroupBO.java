package org.acme.model.bo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.acme.model.dao.GroupAdminDAO;
import org.acme.model.dao.GroupDAO;
import org.acme.model.dao.UserDAO;
import org.acme.model.dto.GroupRequestDTO;
import org.acme.model.dto.GroupResponseDTO;
import org.acme.model.entity.Group;
import org.acme.model.entity.User;

import java.util.List;

@ApplicationScoped
public class GroupBO {

    @Inject
    GroupDAO groupDAO;

    @Inject
    UserDAO userDAO;

    @Inject
    GroupAdminDAO groupAdminDAO;

    @Transactional
    public GroupResponseDTO createGroup(List<Long> userIds, GroupRequestDTO groupRequestDTO, String emailToken){
        User user = userDAO.findByEmail(emailToken);
        userIds.add(user.getId());
        Group groupSaved = groupDAO.saveGroup(userIds, groupRequestDTO);

        groupAdminDAO.saveGroupAdmin(user,groupSaved);
        GroupResponseDTO response = new GroupResponseDTO();
        response.setId(groupSaved.getId());
        response.setName(groupSaved.getName());

        return response;
    }
}
