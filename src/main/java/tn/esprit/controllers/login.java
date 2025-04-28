package tn.esprit.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.ServiceUtilisateur;

import java.io.IOException;

public class login {

    @FXML
    private TextField emailLogin;

    @FXML
    private Button SeConnecterb;

    @FXML
    private PasswordField motdepasseLogin;

    @FXML
    private Label lockoutLabel;

    private ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();
    private int failedAttempts = 0; // Compteur de tentatives échouées
    private boolean isLocked = false; // État de blocage
    private Timeline lockoutTimeline; // Timer pour le compte à rebours

    @FXML
    void SeConnecter(ActionEvent event) {

        if (isLocked) {
            return; // Ignorer si l'utilisateur est bloqué
        }

        String email = emailLogin.getText();
        String motDePasse = motdepasseLogin.getText();

        try {
            Utilisateur utilisateur = serviceUtilisateur.login(email, motDePasse);
            failedAttempts = 0; // Réinitialiser les tentatives en cas de succès
            lockoutLabel.setVisible(false); // Cacher le label

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
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Connexion Réussie");
            alert.setHeaderText(null);
            alert.setContentText("Bienvenue " + utilisateur.getNom() + " ! Vous êtes connecté en tant que " + utilisateur.getRole() + ".");
            alert.showAndWait();
        } catch (Exception e) {
            failedAttempts++;
            if (failedAttempts >= 3) {
                startLockout();
            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Erreur de Connexion");
                alert.setHeaderText("Échec de la connexion");
                alert.setContentText(e.getMessage() + "\nTentatives restantes : " + (3 - failedAttempts));
                alert.showAndWait();
            }
        }
    }

    private void startLockout() {
        isLocked = true;
        failedAttempts = 0; // Réinitialiser les tentatives
        SeConnecterb.setDisable(true); // Désactiver le bouton de connexion
        lockoutLabel.setVisible(true); // Afficher le label

        final int[] secondsRemaining = {30}; // Compte à rebours
        lockoutLabel.setText("Compte bloqué. Veuillez attendre " + secondsRemaining[0] + " secondes.");

        lockoutTimeline = new Timeline();
        lockoutTimeline.setCycleCount(30);
        lockoutTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), e -> {
            secondsRemaining[0]--;
            lockoutLabel.setText("Compte bloqué. Veuillez attendre " + secondsRemaining[0] + " secondes.");
            if (secondsRemaining[0] <= 0) {
                lockoutTimeline.stop();
                isLocked = false;
                SeConnecterb.setDisable(false);
                lockoutLabel.setVisible(false);
            }
        }));
        lockoutTimeline.play();
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

    @FXML
    void motdepasseOublie(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Utilisateur/MotdepasseOublie.fxml"));
            AnchorPane root = loader.load();

            Stage stage = (Stage) emailLogin.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Créer un compte");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ConnecterGoogle(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Utilisateur/GoogleLogin.fxml"));
            AnchorPane root = loader.load();

            Stage stage = (Stage) emailLogin.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Se Connecter avec Google");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @FXML
    void connecteritHub(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Utilisateur/GitHubLogin.fxml"));
            AnchorPane root = loader.load();

            Stage stage = (Stage) emailLogin.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Se Connecter avec GitHub");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

