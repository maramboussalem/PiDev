package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.esprit.entities.Utilisateur;

import java.io.File;
import java.io.IOException;

public class DashboardMedecin {

    @FXML
    private AnchorPane contentArea;

    @FXML
    private Text nomuserConnecte;

    @FXML
    private ImageView imageProfil;

    @FXML
    void logOut(ActionEvent event) {
        try {
            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) contentArea.getScene().getWindow();
            currentStage.close();

            // Charger la page de connexion (login.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Utilisateur/login.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène et une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Connexion");
            stage.setScene(new Scene(root));

            // Afficher la nouvelle fenêtre
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Utilisateur utilisateurConnecte;

    @FXML
    public void initialize() {
        if (utilisateurConnecte != null) {
            nomuserConnecte.setText(utilisateurConnecte.getNom() + " " + utilisateurConnecte.getPrenom());
            loadProfileImage();
        }
    }

    public void setUtilisateurConnecte(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
        System.out.println("Utilisateur connecté : " + utilisateur); // Vérification

        if (utilisateur != null) {
            nomuserConnecte.setText(utilisateur.getNom() + " " + utilisateur.getPrenom());
            loadProfileImage();
        }
    }
    // Méthode pour charger l'image de profil
    private void loadProfileImage() {
        if (utilisateurConnecte != null && utilisateurConnecte.getImg_url() != null && !utilisateurConnecte.getImg_url().isEmpty() && !"null".equals(utilisateurConnecte.getImg_url()) && !"default.png".equals(utilisateurConnecte.getImg_url())) {
            try {
                // Chemin vers le répertoire des images
                String imagePath = "src/main/resources/images/profiles/" + utilisateurConnecte.getImg_url();
                File imageFile = new File(imagePath);

                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    imageProfil.setImage(image);
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
            System.out.println("Aucune image valide trouvée (img_url=" + (utilisateurConnecte != null ? utilisateurConnecte.getImg_url() : "null") + "), chargement de l'image par défaut");
            loadDefaultImage();
        }
    }

    // Méthode pour charger l'image par défaut
    private void loadDefaultImage() {
        try {
            // Charger l'image par défaut depuis les ressources
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/default.png"));
            if (defaultImage != null) {
                imageProfil.setImage(defaultImage);
                System.out.println("Image par défaut chargée avec succès");
            } else {
                System.err.println("Image par défaut introuvable dans les ressources !");
                imageProfil.setImage(null);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image par défaut : " + e.getMessage());
            imageProfil.setImage(null);
        }
    }
    @FXML
    void monProfil(ActionEvent event) {
        try {
            if (utilisateurConnecte == null) {
                System.out.println("Aucun utilisateur connecté !");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Utilisateur/DetailsUser.fxml"));
            Parent root = loader.load();

            DetailsUser controller = loader.getController();
            controller.setUtilisateur(utilisateurConnecte);

            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void refreshPage(ActionEvent event) {
        if (utilisateurConnecte != null) {
            nomuserConnecte.setText(utilisateurConnecte.getNom() + " " + utilisateurConnecte.getPrenom());
            loadProfileImage();
        } else {
            nomuserConnecte.setText("Utilisateur non connecté");
            loadDefaultImage();
        }
    }
}
