package br.edu.ifba.inf008.core.infrastructure.managers;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateManager {
  private static final SessionFactory sessionFactory =
      new Configuration().configure().buildSessionFactory();

  public static Session getSession() {
    return sessionFactory.openSession();
  }
}