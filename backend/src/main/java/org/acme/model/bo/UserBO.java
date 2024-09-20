package org.acme.model.bo;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.acme.controller.UserController;
import org.acme.exception.InvalidLoginException;
import org.acme.model.dao.UserDAO;
import org.acme.model.dto.*;
import org.acme.model.entity.Group;
import org.acme.model.entity.User;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@ApplicationScoped
public class UserBO {

    @Inject
    UserDAO userDAO;
    @Inject
    AuditLogBO auditLogBO;

    @Transactional
    public void register(UserRegisterDTO userRegisterDTO) {
        User userFounded = userDAO.findByEmail(userRegisterDTO.getEmail());
        if(userFounded != null){
            auditLogBO.logToDatabase("REGISTER_USER_FAILED_EXISTED_EMAIL", userRegisterDTO.getEmail(), LocalDateTime.now(), UserBO.class);
            throw new BadRequestException("Já existe uma conta com esse email");
        }
        User user = new User();
        user.setFullName(userRegisterDTO.getFullName());
        user.setEmail(userRegisterDTO.getEmail());
        user.setPassword(BCrypt.hashpw(userRegisterDTO.getPassword(), BCrypt.gensalt()));
        userDAO.persist(user);
    }

    public UserLoginResponseDTO login(UserLoginRequestDTO userLoginRequestDTO) {
        User user = userDAO.findByEmail(userLoginRequestDTO.getEmail());
        if (user == null || !BCrypt.checkpw(userLoginRequestDTO.getPassword(), user.getPassword())) {
            auditLogBO.logToDatabase("LOGIN_FAILED_WRONG_EMAIL_OR_PASSWORD", userLoginRequestDTO.getEmail(), LocalDateTime.now(), UserBO.class);
            throw new InvalidLoginException("email ou senha inválidos");
        }

        String token = Jwt.issuer("linktalk")
                .subject("linktalk")
                .groups(new HashSet<>(Arrays.asList("admin", "writer")))

                .claim("userId", user.getId().longValue())
                .claim("email", user.getEmail())
                .claim("userName", user.getFullName())
                .expiresAt(System.currentTimeMillis() + 3600)
                .sign();

        UserLoginResponseDTO response = new UserLoginResponseDTO();
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setId(user.getId());
        response.setToken(token);
        return response;
    }

    @Transactional
    public String findUserEmailById(Long id) {
        return userDAO.findUserEmailById(id);
    }

    @Transactional
    public User findUserById(Long id) {
        return userDAO.findById(id);
    }

    public List<UserContactDTO> contactsByUserId(Long id) {
        return userDAO.contactsByUserId(id);
    }

    @Transactional
    public UserLoginResponseDTO getUserByEmail(String email, String emailToken) {
        User foundedUser = userDAO.findByEmail(email);
        if (foundedUser != null) {
            UserLoginResponseDTO response = new UserLoginResponseDTO();
            response.setFullName(foundedUser.getFullName());
            response.setEmail(foundedUser.getEmail());
            response.setId(foundedUser.getId());
            return response;
        } else {
            auditLogBO.logToDatabase("GET_USER_BY_EMAIL_REQUEST_FAILED_NOT_FOUNDED_EMAIL", emailToken, LocalDateTime.now(),UserBO.class);
            throw new NotFoundException("Usuario com Email: " + email + " não encontrado.");
        }
    }

    public List<GroupResponseDTO> getUserGroups(Long userId) {
        List<Group> groups = userDAO.getUserGroups(userId);
        List<GroupResponseDTO> groupResponseDTOS = new ArrayList<>();
        for(Group group : groups){
            GroupResponseDTO groupResponseDTO = new GroupResponseDTO();

            groupResponseDTO.setId(group.getId());
            groupResponseDTO.setName(group.getName());
            groupResponseDTO.setAdmin(userDAO.isAdmin(userId, group.getId()));

            groupResponseDTOS.add(groupResponseDTO);
        }
        return groupResponseDTOS;
    }
    @Transactional
    public boolean verifyEmail(String email) {
        User user = userDAO.findByEmail(email);
        return user != null;
    }
}
