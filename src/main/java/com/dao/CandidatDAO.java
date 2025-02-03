package com.dao;

import com.models.Candidat;
import com.models.Parcours;
import com.util.HibernateUtil;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class CandidatDAO {

    public void ajouterCandidat(Candidat candidat) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            for (Parcours parcours : candidat.getParcoursChoisis()) {
                session.saveOrUpdate(parcours);
            }

            session.saveOrUpdate(candidat);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void modifierCandidat(Candidat candidat) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            session.saveOrUpdate(candidat);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void supprimerCandidat(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Candidat candidat = session.get(Candidat.class, id);
            if (candidat != null) {
                session.delete(candidat);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<Candidat> obtenirTousLesCandidats() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Candidat", Candidat.class).list();
        }
    }

    public Candidat obtenirCandidatParId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Candidat.class, id);
        }
    }
}