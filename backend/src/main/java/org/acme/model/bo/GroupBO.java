package org.acme.model.bo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.model.dao.GroupDAO;
import org.acme.model.dto.GroupRequestDTO;
import org.acme.model.dto.GroupResponseDTO;
import org.acme.model.entity.Group;

import java.util.List;

@ApplicationScoped
public class GroupBO {

    @Inject
    GroupDAO groupDAO;

    @Transactional
    public GroupResponseDTO createGroup(List<Long> userIds, GroupRequestDTO groupRequestDTO){
        Group groupSaved = groupDAO.saveGroup(userIds, groupRequestDTO);

        GroupResponseDTO response = new GroupResponseDTO();
        response.setId(groupSaved.getId());
        response.setName(groupSaved.getName());

        return response;
    }
}
