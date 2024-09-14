package org.acme.audit;

import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.acme.controller.UserSession;
import org.acme.model.dao.AuditLogDAO;
import org.acme.model.entity.AuditLog;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import org.slf4j.Logger;

@Auditable
@Interceptor
public class AuditInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditInterceptor.class);
    @Inject
    AuditLogDAO auditLogDAO;

    @Inject
    UserSession userSession; // Classe que contém informações do usuário logado

    @AroundInvoke
    public Object logAction(InvocationContext context) throws Exception {
        String action = context.getMethod().getName();
        String username = userSession.getCurrentUser();

        AuditLog log = new AuditLog(action, username, LocalDateTime.now());
        auditLogDAO.save(log);

        LOGGER.info("User: {}, Action: {}, Timestamp: {}", username, action, LocalDateTime.now());

        return context.proceed();
    }
}
