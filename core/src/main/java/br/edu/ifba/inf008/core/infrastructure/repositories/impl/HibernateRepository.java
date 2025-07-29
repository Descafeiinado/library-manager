package br.edu.ifba.inf008.core.infrastructure.repositories.impl;

import br.edu.ifba.inf008.core.domain.models.PageRequest;
import br.edu.ifba.inf008.core.domain.models.PageableResponse;
import br.edu.ifba.inf008.core.infrastructure.managers.HibernateManager;
import br.edu.ifba.inf008.core.infrastructure.repositories.Repository;
import jakarta.persistence.Table;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Generic repository implementation using Hibernate for CRUD operations.
 *
 * @param <T>  the type of the entity
 * @param <ID> the type of the entity's identifier
 */
public class HibernateRepository<T, ID extends Serializable> implements Repository<T, ID> {

    private final Class<T> entityClass;

    public HibernateRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Finds an entity by its identifier.
     * @param id the identifier of the entity
     * @return an Optional containing the entity if found, or empty if not found
     */
    @Override
    public Optional<T> findById(ID id) {
        try (Session session = getSession()) {
            T entity = session.get(entityClass, id);

            return Optional.ofNullable(entity);
        }
    }

    /**    
     * Finds an entity by a specific field and its value.
     * @param fieldName the name of the field to search by
     * @param value the value of the field to search for
     * @return an Optional containing the entity if found, or empty if not found
     */
    @Override
    public Optional<T> findOne(String fieldName, Object value) {
        try (Session session = getSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);

            Root<T> root = cq.from(entityClass);

            cq.select(root).where(cb.equal(root.get(fieldName), value));

            return session.createQuery(cq).uniqueResultOptional();
        }
    }

    /**
     * Finds all entities of the specified type.
     * @return a list of all entities
     */
    @Override
    public List<T> findAll() {
        try (Session session = getSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);

            cq.select(root);

            return session.createQuery(cq).list();
        }
    }

    /**
     * Finds all entities with pagination support.
     * @param pageRequest the pagination request containing page number and size
     * @return a pageable response containing the entities
     */
    @Override
    public PageableResponse<T> findAll(PageRequest pageRequest) {
        try (Session session = getSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);

            Root<T> root = cq.from(entityClass);
            cq.select(root);

            List<T> content = session.createQuery(cq)
                    .setFirstResult(pageRequest.page() * pageRequest.limit())
                    .setMaxResults(pageRequest.limit()).list();

            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<T> countRoot = countQuery.from(entityClass);
            countQuery.select(cb.count(countRoot));

            long totalElements = session.createQuery(countQuery).uniqueResult();

            return new PageableResponse<>(pageRequest.page(), pageRequest.limit(), totalElements,
                    content);
        }
    }

    /**
     * Finds all entities with a specific field value and pagination support.
     * @param pageRequest the pagination request containing page number and size
     * @param fieldName the name of the field to search by
     * @param value the value of the field to search for
     * @return a pageable response containing the entities
     */
    @Override
    public PageableResponse<T> findAll(PageRequest pageRequest, String fieldName, Object value) {
        try (Session session = getSession()) {
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
            
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<T> countRoot = countQuery.from(entityClass);
            
            countQuery.select(cb.count(countRoot)).where(predicate);
            
            long totalElements = session.createQuery(countQuery).uniqueResult();

            return new PageableResponse<>(pageRequest.page(), pageRequest.limit(), totalElements,
                    content);
        }
    }

    /**
     * Saves an entity to the database.
     * @param entity the entity to save
     * @return the saved entity
     */
    @Override
    public T save(T entity) {
        Transaction transaction = null;

        try (Session session = getSession()) {
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

    /**
     * Deletes an entity from the database.
     * @param entity the entity to delete
     */
    @Override
    public void delete(T entity) {
        Transaction transaction = null;

        try (Session session = getSession()) {
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

    /**
     * Gets the current Hibernate session.
     * @return the current Hibernate session
     */
    public Session getSession() {
        return HibernateManager.getSession();
    }
    
    /**
     * Gets the table name for the entity class.
     * If the entity class does not have a @Table annotation, it returns the simple class name.
     * @return the table name
     */
    private String getTableName() {
        String fallback = entityClass.getSimpleName();

        try {
            Table tableAnnotation = entityClass.getAnnotation(Table.class);
            
            return tableAnnotation.name();
        } catch (Exception exception) {
            return fallback;
        }
    }
}
