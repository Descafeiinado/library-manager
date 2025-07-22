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

public class HibernateManager {

    private static final List<Class<?>> entityClasses = new ArrayList<>();
    private static SessionFactory sessionFactory;

    public static void buildSessionFactory() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();

        MetadataSources metadataSources = new MetadataSources(registry);

        MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder();

        AggregatedClassLoader aggregatedClassLoader = new AggregatedClassLoader(
                ICore.getInstance().getPluginController().getPluginClassLoaders(),
                TcclLookupPrecedence.BEFORE
        );

        System.out.println();

        metadataBuilder.applyTempClassLoader(aggregatedClassLoader);

        for (Class<?> entity : entityClasses) {
            metadataSources.addAnnotatedClass(entity);

            System.out.println("Registered entity class: " + entity.getName());
        }

        sessionFactory = metadataBuilder.build().buildSessionFactory();
    }

    public static void registerEntityClass(Class<?> entityClass) {
        if (!entityClasses.contains(entityClass)) {
            entityClasses.add(entityClass);
        }
    }

    public static void shutdownSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    public static Session getSession() {
        if (sessionFactory == null) {
            throw new IllegalStateException(
                    "SessionFactory has not been built. Call buildSessionFactory() first.");
        }

        return sessionFactory.openSession();
    }

    private static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}