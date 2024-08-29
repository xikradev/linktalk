package org.acme.model.bo;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotAuthorizedException;
import org.acme.model.dao.UserDAO;
import org.acme.model.dto.UserLoginRequestDTO;
import org.acme.model.dto.UserLoginResponseDTO;
import org.acme.model.dto.UserRegisterDTO;
import org.acme.model.entity.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Arrays;
import java.util.HashSet;

@ApplicationScoped
public class UserBO {

   @Inject
    UserDAO userDAO;

   @Transactional
    public void register (UserRegisterDTO userRegisterDTO){
       User user = new User();
       user.setFullName(userRegisterDTO.getFullName());
       user.setEmail(userRegisterDTO.getEmail());
       user.setPassword(BCrypt.hashpw(userRegisterDTO.getPassword(), BCrypt.gensalt()));
       userDAO.persist(user);
   }

   public UserLoginResponseDTO login(UserLoginRequestDTO userLoginRequestDTO){
       User user = userDAO.findByEmail(userLoginRequestDTO.getEmail());
       if(user == null || !BCrypt.checkpw(userLoginRequestDTO.getPassword(),user.getPassword())){
           throw new NotAuthorizedException("email ou senha inválidos");
       }


       String token =Jwt.issuer("linktalk")
               .subject("linktalk")
               .groups(new HashSet<>(Arrays.asList("admin", "writer")))
               .expiresAt(System.currentTimeMillis() + 3600)
               .sign();

       UserLoginResponseDTO response = new UserLoginResponseDTO();
       response.setFullName(user.getFullName());
       response.setEmail(user.getEmail());
       response.setToken(token);
       return response;
   }
}
