package tn.esprit.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.Window;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.ServiceUtilisateur;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Optional;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;

public class DetailsUser {

    @FXML
    private Label AntecedentsD;

    @FXML
    private Label adresseD;

    @FXML
    private Label emailD;

    @FXML
    private Label hopitalD;

    @FXML
    private Label nomPrenonUserD;

    @FXML
    private Label roleUserD;

    @FXML
    private Label sexeD;

    @FXML
    private Label specialiteD;

    @FXML
    private Label telephoneD;

    @FXML
    private ImageView profileImage;

    private Utilisateur utilisateur;
    private ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();


    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        if (utilisateur == null) {
            System.out.println("Utilisateur est NULL dans DetailsUser !");
            return;
        }
        System.out.println("Utilisateur bien reçu : " + utilisateur);

        nomPrenonUserD.setText(utilisateur.getNom() + " " + utilisateur.getPrenom());
        emailD.setText(utilisateur.getEmail());
        adresseD.setText(utilisateur.getAdresse() != null ? utilisateur.getAdresse() : "Non renseigné");
        telephoneD.setText(utilisateur.getTelephone() != null ? utilisateur.getTelephone() : "Non renseigné");
        roleUserD.setText(utilisateur.getRole());
        sexeD.setText(utilisateur. getSexe());
        AntecedentsD.setText(utilisateur.getAntecedents_medicaux() != null ? utilisateur.getAntecedents_medicaux() : "Non renseigné");
        specialiteD.setText(utilisateur.getSpecialite() != null ? utilisateur.getSpecialite() : "Non renseigné");
        hopitalD.setText(utilisateur.getHopital() != null ? utilisateur.getHopital() : "Non renseigné");
        loadProfileImage();
    }

    private void loadProfileImage() {
        if (utilisateur != null && utilisateur.getImg_url() != null && !utilisateur.getImg_url().isEmpty() && !"null".equals(utilisateur.getImg_url())) {
            try {
                String imagePath = "src/main/resources/images/profiles/" + utilisateur.getImg_url();
                File imageFile = new File(imagePath);

                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    profileImage.setImage(image);
                    System.out.println("Image chargée : " + imagePath);
                } else {
                    System.err.println("Fichier image introuvable : " + imagePath);
                    loadDefaultImage();
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
                loadDefaultImage();
            }
        } else {
            System.out.println("Aucune image valide trouvée, chargement de l'image par défaut");
            loadDefaultImage();
        }
    }

    private void loadDefaultImage() {
        try {
            InputStream defaultImageStream = getClass().getResourceAsStream("/images/default.png");
            if (defaultImageStream != null) {
                Image defaultImage = new Image(defaultImageStream);
                profileImage.setImage(defaultImage);
            } else {
                System.err.println("Image par défaut introuvable dans les ressources !");
                profileImage.setImage(null);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image par défaut : " + e.getMessage());
            profileImage.setImage(null);
        }
    }

   /* private void afficherDetails() {
        if (utilisateur != null) {
            System.out.println("Affichage des détails de l'utilisateur : " + utilisateur);
            nomPrenonUserD.setText(utilisateur.getNom() + " " + utilisateur.getPrenom());
            emailD.setText(utilisateur.getEmail());
            adresseD.setText(utilisateur.getAdresse());
            telephoneD.setText(utilisateur.getTelephone());
            roleUserD.setText(utilisateur.getRole());
            sexeD.setText(utilisateur.getSexe());
            specialiteD.setText(utilisateur.getSpecialite());
            AntecedentsD.setText(utilisateur.getAntecedents_medicaux());
            hopitalD.setText(utilisateur.getHopital());
        } else {
            System.out.println("Aucun utilisateur trouvé !");
        }
    }*/

    @FXML
    void ModifierUserD(ActionEvent event) {
        afficherInterface("/Utilisateur/update.fxml", utilisateur);
    }

    @FXML
    void supprimerUserD(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer votre compte ?");
        alert.setContentText("Cette action est irréversible. Vous serez déconnecté après la suppression.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceUtilisateur.supprimer(utilisateur.getId());
                logOut(event);
                ((Stage) telephoneD.getScene().getWindow()).close();

            } catch (SQLException e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Erreur");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
                errorAlert.showAndWait();
                System.err.println("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
            }
        } else {
            // Si l'utilisateur annule, ne rien faire
            System.out.println("Suppression annulée par l'utilisateur.");
        }
    }

    private void afficherInterface(String cheminFXML, Utilisateur utilisateur) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(cheminFXML));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof updateuser) {
                updateuser updateController = (updateuser) controller;
                updateController.setUtilisateur(utilisateur);
                updateController.setDetailsUserController(this); // Passer la référence de DetailsUser
            } else {
                System.err.println("Le contrôleur ne possède pas de méthode setUtilisateur : " + controller.getClass().getName());
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture de l'interface : " + e.getMessage());
        }
    }

    @FXML
    void logOut(ActionEvent event) {
        try {
            for (Window window : Window.getWindows()) {
                if (window instanceof Stage) {
                    ((Stage) window).close();
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la fermeture des fenêtres : " + e.getMessage());
            e.printStackTrace();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Utilisateur/login.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Connexion");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la fenêtre de connexion : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void refreshDetails() {
        try {
            Utilisateur updatedUser = serviceUtilisateur.getUserById(utilisateur.getId());
            if (updatedUser != null) {
                this.utilisateur = updatedUser;
                setUtilisateur(updatedUser);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du rechargement des données de l'utilisateur : " + e.getMessage());
        }
    }
}
