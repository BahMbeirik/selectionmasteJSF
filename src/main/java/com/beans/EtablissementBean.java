package com.beans;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.models.Etablissement;
import com.util.HibernateUtil;

import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "etablissementBean")
@SessionScoped
public class EtablissementBean implements Serializable {
    private Etablissement etablissement = new Etablissement();
    private List<Etablissement> etablissements;
    private boolean enModeModification = false;

    // Getters et Setters
    public Etablissement getEtablissement() {
        return etablissement;
    }

    public void setEtablissement(Etablissement etablissement) {
        this.etablissement = etablissement;
    }

    public List<Etablissement> getEtablissements() {
        return etablissements;
    }

    public boolean isEnModeModification() {
        return enModeModification;
    }

    @PostConstruct
    public void init() {
        etablissement = new Etablissement(); 
        etablissements = chargerEtablissements(); 
    }


    public void ajouterEtablissement() {
        if (etablissement.getNom() == null || etablissement.getNom().trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Le nom est obligatoire."));
            return;
        }

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            
            if (enModeModification) {
                // Modification
                Etablissement etablissementExistant = session.get(Etablissement.class, etablissement.getId());
                if (etablissementExistant != null) {
                    etablissementExistant.setNom(etablissement.getNom());
                    session.update(etablissementExistant);
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Établissement modifié avec succès."));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Erreur", "Établissement introuvable."));
                }
            } else {
                // Ajout
                session.save(etablissement);
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Établissement ajouté avec succès."));
            }

            transaction.commit();
            reinitialiserFormulaire();
            etablissements = chargerEtablissements();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Une erreur est survenue."));
        } finally {
            session.close();
        }
    }

    public void modifierEtablissement(Etablissement etablissement) {
        this.etablissement = etablissement; 
        this.enModeModification = true; 
    }


    public void supprimerEtablissement(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Etablissement etablissement = session.get(Etablissement.class, id);
            if (etablissement != null) {
                session.delete(etablissement);
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Établissement supprimé avec succès."));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Avertissement", "L'établissement n'existe pas."));
            }
            transaction.commit();
            etablissements = chargerEtablissements();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Une erreur est survenue."));
        } finally {
            session.close();
        }
    }

    public void reinitialiserFormulaire() {
        etablissement = new Etablissement();
        enModeModification = false;
    }

    public List<Etablissement> chargerEtablissements() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Etablissement> result;
        try {
            result = session.createQuery("from Etablissement", Etablissement.class).list();
        } finally {
            session.close();
        }
        return result;
    }
}
