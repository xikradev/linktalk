package org.acme.model.bo;
import io.smallrye.jwt.build.Jwt;
import io.vertx.ext.auth.impl.jose.JWT;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotAuthorizedException;
import org.acme.model.dao.UserDAO;
import org.acme.model.dto.UserLoginDTO;
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

   public String login(UserLoginDTO userLoginDTO){
       User user = userDAO.findByEmail(userLoginDTO.getEmail());
       if(user == null || !BCrypt.checkpw(userLoginDTO.getPassword(),user.getPassword())){
           throw new NotAuthorizedException("email ou senha inválidos");
       }

       return Jwt.issuer("linktalk")
               .subject("linktalk")
               .groups(new HashSet<>(Arrays.asList("admin", "writer")))
               .expiresAt(System.currentTimeMillis() + 3600)
               .sign();
   }
}
