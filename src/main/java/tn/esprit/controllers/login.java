package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.ServiceUtilisateur;

import java.io.IOException;

public class login {

    @FXML
    private TextField emailLogin;

    @FXML
    private PasswordField motdepasseLogin;

    private ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

    @FXML
    void SeConnecter(ActionEvent event) {
        String email = emailLogin.getText();
        String motDePasse = motdepasseLogin.getText();

        try {
            Utilisateur utilisateur = serviceUtilisateur.login(email, motDePasse);

            Stage stage = (Stage) emailLogin.getScene().getWindow();

            if ("patient".equals(utilisateur.getRole())) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));
                AnchorPane root = loader.load();
                Home homeController = loader.getController();
                homeController.setUtilisateurConnecte(utilisateur);
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Page d'Accueil du Patient");

            } else if ("medecin".equals(utilisateur.getRole())) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardMedecin.fxml"));
                AnchorPane root = loader.load();
                DashboardMedecin dashboardControllerM = loader.getController();
                dashboardControllerM.setUtilisateurConnecte(utilisateur);
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Tableau de Bord du Médecin");

            } else if ("admin".equals(utilisateur.getRole())) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardAdmin.fxml"));
                AnchorPane root = loader.load();
                DashboardAdmin dashboardController = loader.getController();
                dashboardController.setUtilisateurConnecte(utilisateur); // Passer l'utilisateur connecté
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Tableau de Bord de l'Admin");
            }
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur de Connexion");
            alert.setHeaderText("Échec de la connexion");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    void Sinscrire(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Utilisateur/adduser.fxml"));
            AnchorPane root = loader.load();

            Stage stage = (Stage) emailLogin.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Créer un compte");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

