package com.beans;

import com.dao.ParcoursDAO;
import com.models.Etablissement;
import com.models.Parcours;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.util.List;

@ManagedBean(name = "parcoursBean")
@SessionScoped
public class ParcoursBean {
    private Parcours parcours = new Parcours(); // Objet Parcours
    private List<Parcours> listeParcours; // Liste des parcours
    private ParcoursDAO parcoursDAO = new ParcoursDAO(); // DAO pour interagir avec la base de données
    private boolean enModeModification = false; // Mode édition (true/false)

    // Getters et Setters
    public Parcours getParcours() {
        return parcours;
    }

    public void setParcours(Parcours parcours) {
        this.parcours = parcours;
    }

    public List<Parcours> getListeParcours() {
        if (listeParcours == null) {
            listeParcours = parcoursDAO.findAll(); // Charger la liste si elle est vide
        }
        return listeParcours;
    }

    public boolean isEnModeModification() {
        return enModeModification;
    }
    
    @PostConstruct
    public void init() {
    	parcours = new Parcours(); 
    	listeParcours = getListeParcours(); 
    }

    // Sauvegarder un parcours (ajout ou modification)
    public void sauvegarderParcours() {
        try {
            if (enModeModification) {
                parcoursDAO.update(parcours); // Mettre à jour le parcours
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Parcours modifié."));
            } else {
                parcoursDAO.save(parcours); // Ajouter un nouveau parcours
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Parcours ajouté."));
            }
            reinitialiserFormulaire(); // Réinitialiser le formulaire
            rafraichirListeParcours(); // Rafraîchir la liste
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Erreur lors de l'enregistrement du parcours."));
            
        }
    }

    
    
 // Charger un parcours pour modification
    public void modifierParcour(Parcours parcours) {
        this.parcours = parcours; 
        this.enModeModification = true; 
    }

    // Supprimer un parcours
    public String supprimerParcours(int id) {
        try {
            parcoursDAO.deleteById(id); // Supprimer le parcours par son ID
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Parcours supprimé."));
            rafraichirListeParcours(); // Rafraîchir la liste
            return "parcours.xhtml?faces-redirect=true"; // Redirection vers la page des parcours
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Erreur lors de la suppression du parcours."));
            return null;
        }
    }

    // Réinitialiser le formulaire
    public void reinitialiserFormulaire() {
        parcours = new Parcours(); // Réinitialiser l'objet Parcours
        enModeModification = false; // Désactiver le mode édition
    }

    // Rafraîchir la liste des parcours
    public void rafraichirListeParcours() {
        listeParcours = parcoursDAO.findAll(); // Recharger la liste depuis la base de données
    }
}