package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.GitHubSignInService;
import tn.esprit.services.ServiceUtilisateur;

import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;

public class GitHubLogin {

    @FXML
    private WebView webView;

    @FXML
    public void initialize() {
        try {
            startGitHubAuth();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startGitHubAuth() throws Exception {
        String clientId = "";
        String redirectUri = "";
        String scope = "read:user user:email";

        String authUrl = "https://github.com/login/oauth/authorize" +
                "?client_id=" + URLEncoder.encode(clientId, "UTF-8") +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8") +
                "&scope=" + URLEncoder.encode(scope, "UTF-8");

        WebEngine webEngine = webView.getEngine();
        webEngine.load(authUrl);

        webEngine.locationProperty().addListener((obs, oldLoc, newLoc) -> {
            if (newLoc.startsWith("http://localhost")) {
                String code = extractCodeFromUrl(newLoc);
                System.out.println("Code GitHub : " + code);

                GitHubSignInService signInService = new GitHubSignInService();
                Utilisateur userFromGitHub = signInService.getUserInfoFromCode(code);

                if (userFromGitHub != null) {
                    Utilisateur finalUser = saveOrGetUser(userFromGitHub);
                    redirectToHomePage(finalUser);
                }
            }
        });
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

    private void redirectToHomePage(Utilisateur utilisateur) {
        try {
            Stage stage = (Stage) webView.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));
            Parent root = loader.load();

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
