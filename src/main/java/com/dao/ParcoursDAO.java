package com.dao;

import com.models.Parcours;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;

public class ParcoursDAO {
    private SessionFactory sessionFactory;

    public ParcoursDAO() {
        // Initialisation de la SessionFactory
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    // Trouver tous les parcours
    public List<Parcours> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Parcours", Parcours.class).list();
        }
    }

    // Trouver un parcours par son ID
    public Parcours findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Parcours.class, id);
        }
    }

    // Sauvegarder un parcours
    public void save(Parcours parcours) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(parcours); // Sauvegarder le parcours
            session.getTransaction().commit(); // Valider la transaction
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback(); // Annuler la transaction en cas d'erreur
            }
            throw new RuntimeException("Erreur lors de l'enregistrement du parcours", e);
        } finally {
            session.close(); // Fermer la session
        }
    }

    // Mettre à jour un parcours
    public void update(Parcours parcours) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(parcours);
            session.getTransaction().commit();
        }
    }

    // Supprimer un parcours
    public void delete(Parcours parcours) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(parcours);
            session.getTransaction().commit();
        }
    }

    // Supprimer un parcours par son ID
    public void deleteById(int id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Parcours parcours = session.get(Parcours.class, id);
            if (parcours != null) {
                session.delete(parcours);
            }
            session.getTransaction().commit();
        }
    }
}