package com.beans;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import com.dao.UserDAO;
import com.models.Role;
import com.models.User;
import org.mindrot.jbcrypt.BCrypt;

@ManagedBean(name = "userManagementBean")
@SessionScoped
public class UserManagementBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private User user = new User();
    private List<User> users;
    private UserDAO userDAO = new UserDAO(); // DAO pour accéder aux données

    @PostConstruct
    public void init() {
        users = userDAO.findAll(); // Charger les utilisateurs au démarrage
        user.setRole(Role.CANDIDAT); // Initialisation du rôle par défaut
    }

    // Ajouter un utilisateur
    public void addUser() {
        try {
            // Hacher le mot de passe avant de l'enregistrer
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(hashedPassword);

            userDAO.save(user);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Utilisateur ajouté avec succès.", null));
            users = userDAO.findAll(); // Recharger la liste des utilisateurs
            user = new User(); // Réinitialiser le formulaire
            user.setRole(Role.CANDIDAT); // Réinitialiser le rôle par défaut
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur lors de l'ajout de l'utilisateur.", null));
        }
    }

    // Changer le rôle de l'utilisateur
    public void changeRole(User user) {
        try {
            userDAO.update(user); // Mettre à jour l'utilisateur dans la base de données
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Rôle mis à jour avec succès.", null));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur lors de la mise à jour du rôle.", null));
        }
    }

    // Supprimer un utilisateur
    public void deleteUser(User user) {
        userDAO.delete(user);
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Utilisateur supprimé avec succès.", null));
        users = userDAO.findAll(); // Recharger la liste des utilisateurs
    }

    // Getters et Setters
    public List<User> getUsers() {
        return users;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    public List<Role> getRoles() {
        return Arrays.asList(Role.values());
    }
}