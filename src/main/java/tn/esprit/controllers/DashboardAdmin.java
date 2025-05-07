package tn.esprit.controllers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import tn.esprit.entities.Utilisateur;

import java.io.File;
import java.io.IOException;

public class DashboardAdmin {
    @FXML
    private AnchorPane contentArea;

    @FXML
    private Label userConnecte;

    @FXML
    private ImageView imageProfil;

    private Utilisateur utilisateurConnecte;

    @FXML
    public void initialize() {
        if (utilisateurConnecte != null) {
            userConnecte.setText(utilisateurConnecte.getNom() + " " + utilisateurConnecte.getPrenom());
            loadProfileImage();
        }
    }

    // Méthode pour définir l'utilisateur connecté
    public void setUtilisateurConnecte(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
        System.out.println("Utilisateur connecté : " + utilisateur); // Vérification

        if (utilisateur != null) {
            userConnecte.setText(utilisateur.getNom() + " " + utilisateur.getPrenom());
            loadProfileImage();
        }
    }

    private void loadProfileImage() {
        if (utilisateurConnecte != null && utilisateurConnecte.getImg_url() != null && !utilisateurConnecte.getImg_url().isEmpty() && !"null".equals(utilisateurConnecte.getImg_url()) && !"default.jpg".equals(utilisateurConnecte.getImg_url())) {
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

            // Vérifier les valeurs des attributs avant d'ouvrir la fenêtre
            System.out.println("Utilisateur connecté : " + utilisateurConnecte);

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
    void utilisateur(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Utilisateur/ListeUser.fxml"));  // Mettre le chemin correct vers votre fichier FXML
            Parent root = loader.load();

            // Remplacer le contenu actuel par celui de la liste des utilisateurs
            contentArea.getChildren().clear();  // Supprimer tout contenu existant dans contentArea
            contentArea.getChildren().add(root);  // Ajouter la nouvelle vue (Liste des utilisateurs)
        } catch (IOException e) {
            e.printStackTrace();  // Afficher une erreur si le chargement échoue
        }
    }
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
    @FXML
    void refreshPage(ActionEvent event) {
        if (utilisateurConnecte != null) {
            userConnecte.setText(utilisateurConnecte.getNom() + " " + utilisateurConnecte.getPrenom());
            loadProfileImage();
        } else {
            userConnecte.setText("Utilisateur non connecté");
            loadDefaultImage();
        }
    }

    @FXML
    void medicament(ActionEvent event) {


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/medicament/IndexMedicament.fxml"));
            Parent root = loader.load();

            // If AddConsultationController needs the utilisateurConnecte, you can do:
            // AddConsultationController controller = loader.getController();
            // controller.setUtilisateur(utilisateurConnecte);

            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void Fournisseur(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fournisseur/index.fxml"));
            Parent root = loader.load();

            // If AddConsultationController needs the utilisateurConnecte, you can do:
            // AddConsultationController controller = loader.getController();
            // controller.setUtilisateur(utilisateurConnecte);

            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void service(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Service/IndexServiceMed.fxml"));
            Parent root = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void Equipement(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Service/IndexEquipement.fxml"));
            Parent root = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openReclamation(ActionEvent event) {
        try {
            AnchorPane reclamationPage = FXMLLoader.load(getClass().getResource("/Reclamation/adminReclamation.fxml"));
            contentArea.getChildren().setAll(reclamationPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void diagnosticButton (ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ParametresVitaux/IndexParametresVitaux.fxml"));
            Parent root = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
