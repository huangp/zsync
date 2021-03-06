/*
 * Copyright 2015, Red Hat, Inc. and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.zanata.sync.dao;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.zanata.sync.model.SyncWorkConfig;


/**
 * @author Patrick Huang <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
@Stateless
public class SyncWorkConfigDAO implements Repository<SyncWorkConfig, Long> {

    @Inject
    private SyncWorkConfigSerializer serializer;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<SyncWorkConfig> load(Long id) {
        SyncWorkConfig syncWorkConfig =
                entityManager.find(SyncWorkConfig.class, id);
        return Optional.ofNullable(syncWorkConfig);
    }

    @Override
    @TransactionAttribute
    public void persist(SyncWorkConfig config) {
        entityManager.persist(config);
    }

    @Override
    @TransactionAttribute
    public boolean delete(Long id) {
        SyncWorkConfig syncWorkConfig =
                entityManager.find(SyncWorkConfig.class, id);
        if (syncWorkConfig != null) {
            entityManager.remove(syncWorkConfig);
            return true;
        }
        return false;
    }

    @Override
    public List<SyncWorkConfig> getHistory(Long id) {
        //TODO implement
        throw new UnsupportedOperationException("Implement me!");
        //return null;
    }

    @Override
    public List<SyncWorkConfig> getAll() {
        return entityManager
                .createQuery("from SyncWorkConfig order by createdDate",
                        SyncWorkConfig.class).getResultList();
    }

    @Override
    public List<SyncWorkConfig> findByCriteria(
            BiFunction<CriteriaBuilder, Root<SyncWorkConfig>, Predicate[]> criteriaBuilderToPredicates) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SyncWorkConfig> query = cb
                .createQuery(SyncWorkConfig.class);
        Root<SyncWorkConfig> root = query.from(SyncWorkConfig.class);
        CriteriaQuery<SyncWorkConfig> where =
                query.select(root).where(criteriaBuilderToPredicates.apply(cb, root));
        return entityManager.createQuery(where).getResultList();
    }
}
