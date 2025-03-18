package com.beans;

import java.io.Serializable;
import java.io.IOException;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.dao.UserDAO;
import com.models.User;
import org.mindrot.jbcrypt.BCrypt;

@ManagedBean(name = "authBean")
@SessionScoped
public class AuthBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private User loggedInUser;

    private UserDAO userDAO = new UserDAO(); // Assume this is properly configured

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    // Login method
    public String login() {
        User user = userDAO.findByUsername(username); 
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            loggedInUser = user;
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Connexion réussie !"));

            // Redirect based on role
            switch (user.getRole()) {
                case ADMIN:
                    return "admin/home?faces-redirect=true";
                case JURY:
                    return "jury/home?faces-redirect=true";
                case AGENT:
                    return "agent/home?faces-redirect=true";
                case CANDIDAT:
                    return "candidat/home?faces-redirect=true";
                default:
                    return "login?faces-redirect=true";
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nom d'utilisateur ou mot de passe incorrect.", null));
            return "login"; // Return to login page
        }
    }
    // Déconnexion
    public String logout() {
        if (loggedInUser != null) {
            System.out.println("Déconnexion de l'utilisateur : " + loggedInUser.getUsername());
        }
        // Invalidate session
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        loggedInUser = null;
        return "/pages/login.xhtml?faces-redirect=true"; // Redirect to login page
    }

    
}
