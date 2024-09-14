package org.acme.model.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.acme.model.entity.AuditLog;
@ApplicationScoped
public class AuditLogDAO {
    @Inject
    EntityManager entityManager;

    @Transactional
    public void save(AuditLog log) {
        entityManager.persist(log);
    }
}
