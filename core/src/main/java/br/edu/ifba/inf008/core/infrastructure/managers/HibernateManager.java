package br.edu.ifba.inf008.core.infrastructure.managers;

import br.edu.ifba.inf008.core.ICore;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.classloading.internal.AggregatedClassLoader;
import org.hibernate.boot.registry.classloading.internal.TcclLookupPrecedence;

/**
 * HibernateManager is responsible for managing the Hibernate SessionFactory and
 * providing methods to register entity classes, build the session factory, and
 * obtain sessions.
 */
public class HibernateManager {

    private static final List<Class<?>> entityClasses = new ArrayList<>();
    private static SessionFactory sessionFactory;

    /**
     * Builds the Hibernate SessionFactory using the registered entity classes.
     * This method should be called before obtaining any sessions.
     */
    public static void buildSessionFactory() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();

        MetadataSources metadataSources = new MetadataSources(registry);

        MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder();

        AggregatedClassLoader aggregatedClassLoader = new AggregatedClassLoader(
                ICore.getInstance().getPluginController().getPluginClassLoaders(),
                TcclLookupPrecedence.BEFORE
        );

        metadataBuilder.applyTempClassLoader(aggregatedClassLoader);

        for (Class<?> entity : entityClasses) {
            metadataSources.addAnnotatedClass(entity);

            System.out.println("Registered entity class: " + entity.getName());
        }

        sessionFactory = metadataBuilder.build().buildSessionFactory();
    }

    /**
     * Registers an entity class to be included in the Hibernate SessionFactory.
     * This method can be called multiple times to register multiple entity classes.
     *
     * @param entityClass the class of the entity to register
     */
    public static void registerEntityClass(Class<?> entityClass) {
        if (!entityClasses.contains(entityClass)) {
            entityClasses.add(entityClass);
        }
    }

    /**
     * Shuts down the Hibernate SessionFactory, releasing all resources.
     * This method should be called when the application is shutting down.
     */
    public static void shutdownSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    /**
     * Obtains a new Hibernate session from the SessionFactory.
     * This method should be used to create a new session for database operations.
     *
     * @return a new Hibernate Session
     */
    public static Session getSession() {
        if (sessionFactory == null) {
            throw new IllegalStateException(
                    "SessionFactory has not been built. Call buildSessionFactory() first.");
        }

        return sessionFactory.openSession();
    }

    /**
     * Gets the current Hibernate SessionFactory.
     * This method can be used to access the SessionFactory directly.
     *
     * @return the current SessionFactory
     */
    private static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}