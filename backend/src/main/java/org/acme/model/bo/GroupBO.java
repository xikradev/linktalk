package org.acme.model.bo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.acme.controller.GroupController;
import org.acme.model.dao.GroupAdminDAO;
import org.acme.model.dao.GroupDAO;
import org.acme.model.dao.ImageDAO;
import org.acme.model.dao.UserDAO;
import org.acme.model.dto.GroupRequestDTO;
import org.acme.model.dto.GroupResponseDTO;
import org.acme.model.dto.UserContactDTO;
import org.acme.model.entity.Conversation;
import org.acme.model.entity.Group;
import org.acme.model.entity.Image;
import org.acme.model.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class GroupBO {

    @Inject
    GroupDAO groupDAO;

    @Inject
    UserDAO userDAO;

    @Inject
    ImageDAO imageDAO;

    @Inject
    GroupAdminDAO groupAdminDAO;

    @Inject
    AuditLogBO auditLogBO;

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
    @Transactional
    public void deleteGroupById(Long groupId) {
        Group group = groupDAO.findById(groupId);
        if(group != null){
            List<Image> images = imageDAO.getImagesByGroup(groupId);
            for(Image image : images){
                imageDAO.delete(image);
            }
            groupDAO.delete(group);
        }else {
            throw new NotFoundException("Grupo com ID " + groupId + " não encontrado.");
        }
    }
    @Transactional
    public void addUserToGroup(Long groupId, List<Long> usersId, String emailToken) {
        Group group = groupDAO.findById(groupId);

        for( Long userId : usersId){
            User user = userDAO.findById(userId);
            if(user == null){
                auditLogBO.logToDatabase("ADD_USER_TO_GROUP_REQUEST_FAILED_NOT_FOUND_USER", emailToken, LocalDateTime.now(), GroupBO.class);
                throw new NotFoundException("Usuário com ID " + userId + " não encontrada.");
            }
            if(group == null){
                auditLogBO.logToDatabase("ADD_USER_TO_GROUP_REQUEST_FAILED_NOT_FOUND_GROUP", emailToken, LocalDateTime.now(), GroupBO.class);
                throw new NotFoundException("Grupo com ID " + groupId + " não encontrado.");
            }
            groupDAO.addUserToGroup(group, user);
        }
    }
    @Transactional
    public void removeUserFromGroup(Long groupId, List<Long> userIds,String emailToken) {
        Group group = groupDAO.findById(groupId);

        for (Long userId : userIds){
            User user = userDAO.findById(userId);
            if(user == null){
                auditLogBO.logToDatabase("ADD_USER_TO_GROUP_REQUEST_FAILED_NOT_FOUND_USER", emailToken, LocalDateTime.now(), GroupBO.class);
                throw new NotFoundException("Usuário com ID " + userId + " não encontrada.");
            }
            if(group == null){
                auditLogBO.logToDatabase("ADD_USER_TO_GROUP_REQUEST_FAILED_NOT_FOUND_GROUP", emailToken, LocalDateTime.now(), GroupBO.class);
                throw new NotFoundException("Grupo com ID " + groupId + " não encontrado.");
            }
            groupDAO.removeUserFromGroup(group,user);
        }
    }

    public List<UserContactDTO> getGroupMembers(Long groupId) {
        Group group = groupDAO.findById(groupId);
        if(group == null){
            throw new NotFoundException("Grupo com ID " + groupId + " não encontrado.");
        }
        List<User> members = groupDAO.getGroupMembers(group);
        List<UserContactDTO> contacts = new ArrayList<>();
        for(User user: members){
            UserContactDTO userContactDTO = new UserContactDTO();
            userContactDTO.setEmail(user.getEmail());
            userContactDTO.setId(user.getId());
            userContactDTO.setFullName(user.getFullName());
            contacts.add(userContactDTO);
        }

        return contacts;
    }
    @Transactional
    public void updateGroupName(Long groupId, String emailToken, GroupRequestDTO groupRequestDTO) {
        Group group = groupDAO.findById(groupId);
        if(group == null){
            throw new NotFoundException("Grupo não encontrado com id: "+ groupId);
        }
        group.setName(groupRequestDTO.getName());
        groupDAO.update(group);
    }
}
