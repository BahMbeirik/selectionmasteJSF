package com.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import com.models.Etablissement;
import com.util.HibernateUtil;
import java.util.List;

public class EtablissementDAO {

    private SessionFactory sessionFactory;

    public EtablissementDAO() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    public List<Etablissement> obtenirTousLesEtablissements() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Etablissement", Etablissement.class).list();
        }
    }

    public void ajouterEtablissement(Etablissement etablissement) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.persist(etablissement);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void modifierEtablissement(Etablissement etablissement) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.merge(etablissement);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void supprimerEtablissement(int id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Etablissement etablissement = session.get(Etablissement.class, id);
            if (etablissement != null) {
                session.delete(etablissement);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public Etablissement obtenirEtablissementParId(Long id) {
        Session session = HibernateUtil.getSession();
        try {
            return session.get(Etablissement.class, id);
        } finally {
            HibernateUtil.closeSession();
        }
    }
}