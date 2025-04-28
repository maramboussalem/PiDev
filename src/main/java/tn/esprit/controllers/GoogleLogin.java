package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.GoogleSignInService;
import tn.esprit.services.ServiceUtilisateur;

import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;

public class GoogleLogin {

    @FXML
    private WebView webView;

    @FXML
    public void initialize() {
        try {
            startGoogleAuth();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startGoogleAuth() throws Exception {
        String clientId = "";
        String redirectUri = "";
        String scope = "profile email";

        String authUrl = "https://accounts.google.com/o/oauth2/auth" +
                "?scope=" + URLEncoder.encode(scope, "UTF-8") +
                "&access_type=offline" +
                "&include_granted_scopes=true" +
                "&response_type=code" +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8") +
                "&client_id=" + URLEncoder.encode(clientId, "UTF-8");

        WebEngine webEngine = webView.getEngine();
        webEngine.load(authUrl);

        webEngine.locationProperty().addListener((obs, oldLoc, newLoc) -> {
            if (newLoc.startsWith("http://localhost")) {
                String code = extractCodeFromUrl(newLoc);
                System.out.println("Code Google : " + code);

                GoogleSignInService signInService = new GoogleSignInService();
                Utilisateur userFromGoogle = signInService.getUserInfoFromCode(code);

                if (userFromGoogle != null) {
                    // Vérifier si l'utilisateur existe déjà
                    Utilisateur finalUser = saveOrGetUser(userFromGoogle);

                    // Rediriger vers Home avec l'utilisateur
                    redirectToHomePage(finalUser);
                }
            }
        });
    }

    private Utilisateur saveOrGetUser(Utilisateur user) {
        ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
        try {
            Utilisateur existingUser = serviceUtilisateur.getUserByEmail(user.getEmail());
            if (existingUser != null) {
                System.out.println("Utilisateur déjà existant. Connexion...");
                return existingUser;
            } else {
                System.out.println("Nouvel utilisateur. Création...");
                serviceUtilisateur.ajouter(user);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private String extractCodeFromUrl(String url) {
        try {
            URL u = new URL(url);
            String query = u.getQuery();
            for (String param : query.split("&")) {
                if (param.startsWith("code=")) {
                    return param.substring(5);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveUser(Utilisateur user) {
        ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
        try {
            serviceUtilisateur.ajouter(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void redirectToHomePage(Utilisateur utilisateur) {
        try {
            Stage stage = (Stage) webView.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));
            Parent root = loader.load();

            // Passer l'utilisateur à Home
            Home controller = loader.getController();
            controller.setUtilisateurConnecte(utilisateur);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
