package br.edu.ifba.inf008.core.infrastructure.repositories.impl;

import br.edu.ifba.inf008.core.domain.models.PageRequest;
import br.edu.ifba.inf008.core.domain.models.PageableResponse;
import br.edu.ifba.inf008.core.infrastructure.managers.HibernateManager;
import br.edu.ifba.inf008.core.infrastructure.repositories.Repository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.Transaction;

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
    public Optional<T> findOne(String fieldName, Object value) {
        try (Session session = HibernateManager.getSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);

            Root<T> root = cq.from(entityClass);

            cq.select(root).where(cb.equal(root.get(fieldName), value));

            return session.createQuery(cq).uniqueResultOptional();
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
                    .setMaxResults(pageRequest.limit()).list();

            long totalElements = (long) session.createQuery(
                    "select count(*) from " + entityClass.getName()).uniqueResult();

            return new PageableResponse<>(pageRequest.page(), pageRequest.limit(), totalElements,
                    content);
        }
    }

    @Override
    public PageableResponse<T> findAll(PageRequest pageRequest, String fieldName, Object value) {
        try (Session session = HibernateManager.getSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);

            Root<T> root = cq.from(entityClass);

            Predicate predicate = cb.equal(root.get(fieldName), value);

            if (value == null) {
                predicate = cb.isNull(root.get(fieldName));
            }

            cq.select(root).where(predicate);

            List<T> content = session.createQuery(cq)
                    .setFirstResult(pageRequest.page() * pageRequest.limit())
                    .setMaxResults(pageRequest.limit()).list();

            long totalElements = (long) session.createQuery(
                    "select count(*) from " + entityClass.getName() + " where " + fieldName
                            + " = :value").setParameter("value", value).uniqueResult();

            return new PageableResponse<>(pageRequest.page(), pageRequest.limit(), totalElements,
                    content);
        }
    }

    @Override
    public T save(T entity) {
        Transaction transaction = null;

        try (Session session = HibernateManager.getSession()) {
            transaction = session.beginTransaction();
            T newEntity = session.merge(entity);
            transaction.commit();

            return newEntity;
        } catch (Exception e) {
            try {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
            } catch (Exception ignored) {
            }

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
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
