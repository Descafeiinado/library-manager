package br.edu.ifba.inf008.core.infrastructure.repositories.impl;

import br.edu.ifba.inf008.core.domain.models.PageRequest;
import br.edu.ifba.inf008.core.domain.models.PageableResponse;
import br.edu.ifba.inf008.core.infrastructure.managers.HibernateManager;
import br.edu.ifba.inf008.core.infrastructure.repositories.Repository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class HibernateRepository<T, ID extends Serializable> implements Repository<T, ID> {

    private final Class<T> entityClass;

    public HibernateRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public Optional<T> findById(ID id) {
        try (Session session = HibernateManager.getSession()) {
            T entity = session.get(entityClass, id);

            return Optional.ofNullable(entity);
        }
    }

    @Override
    public List<T> findAll() {
        try (Session session = HibernateManager.getSession()) {
            return session.createQuery("from " + entityClass.getName(), entityClass).list();
        }
    }

    @Override
    public PageableResponse<T> findAll(PageRequest pageRequest) {
        try (Session session = HibernateManager.getSession()) {
            List<T> content = session.createQuery("from " + entityClass.getName(), entityClass)
                    .setFirstResult(pageRequest.page() * pageRequest.limit())
                    .setMaxResults(pageRequest.limit())
                    .list();

            long totalElements = (long) session.createQuery("select count(*) from " + entityClass.getName()).uniqueResult();

            return new PageableResponse<>(pageRequest.page(), pageRequest.limit(), totalElements, content);
        }
    }

    @Override
    public void save(T entity) {
        Transaction transaction = null;

        try (Session session = HibernateManager.getSession()) {
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    @Override
    public void delete(T entity) {
        Transaction transaction = null;

        try (Session session = HibernateManager.getSession()) {
            transaction = session.beginTransaction();
            session.remove(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
}
