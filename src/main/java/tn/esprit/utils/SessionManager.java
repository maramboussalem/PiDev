package tn.esprit.utils;

import tn.esprit.entities.Utilisateur;

public class SessionManager {
    private static SessionManager instance;
    private Utilisateur utilisateurConnecte;

    private SessionManager() {
        // private constructor to prevent instantiation
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
    }

    public Utilisateur getUtilisateur() {
        return utilisateurConnecte;
    }

    public void logout() {
        utilisateurConnecte = null;
    }
}
