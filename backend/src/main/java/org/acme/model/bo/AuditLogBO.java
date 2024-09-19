package org.acme.model.bo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.model.dao.AuditLogDAO;
import org.acme.model.entity.AuditLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@ApplicationScoped
public class AuditLogBO {

    @Inject
    AuditLogDAO auditLogDAO;

    public void logToDatabase(String action, String username, LocalDateTime timestamp, Class<?> clazz){
        Logger LOGGER = LoggerFactory.getLogger(clazz);
        AuditLog auditLog = new AuditLog(action,username,timestamp);
        auditLogDAO.save(auditLog);
        LOGGER.info("Action: {}, Executed by: {}, at: {}", action, username, timestamp);
    }


}
