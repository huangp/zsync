package org.zanata.sync.dao;

import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zanata.sync.model.SystemSettings;


/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 * @author Patrick Huang <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
@Stateless
public class SystemSettingsDAO {
    private static final Logger log =
            LoggerFactory.getLogger(SystemSettingsDAO.class);

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<SystemSettings> getSystemSettings(String key) {
        List<SystemSettings> result = entityManager
                .createQuery("from SystemSettings where key = :key",
                        SystemSettings.class)
                .setParameter("key", key)
                .getResultList();
        if (result.size() == 1) {
            return Optional.of(result.get(0));
        }
        return Optional.empty();
    }


    @TransactionAttribute
    public void persist(SystemSettings systemSettings) {
        entityManager.persist(systemSettings);

        log.info("System settings saved." + systemSettings.toString());
    }
}
