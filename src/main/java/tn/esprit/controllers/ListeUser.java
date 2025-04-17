package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.esprit.services.ServiceUtilisateur;
import tn.esprit.entities.Utilisateur;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.VBox;

public class ListeUser implements Initializable {

    @FXML
    private TextField searchField;

    @FXML
    private ListView<Utilisateur> listeUser;

    @FXML
    private ComboBox<String> sortComboBox;

    @FXML
    private Label adresse;

    @FXML
    private Label email;

    @FXML
    private ImageView image;

    @FXML
    private Label nom;

    @FXML
    private Label prenom;

    @FXML
    private Label role;

    @FXML
    private Label sexe;

    @FXML
    private Label tel;

    @FXML
    private Button btnToggleStatus; // Le bouton global
    @FXML
    private Label statutLabel; // Le label qui affiche le statut de l'utilisateur

    private Utilisateur utilisateurSelectionne;

    private ServiceUtilisateur serviceUtilisateur;

    public ListeUser() {
        serviceUtilisateur = new ServiceUtilisateur();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        afficherUtilisateurs();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrerUtilisateurs(newValue);
        });

        sortComboBox.getItems().addAll("Nom", "Prénom", "Email");
        sortComboBox.setOnAction(event -> trierUtilisateurs(sortComboBox.getValue()));

    }
    @FXML
    private void btnToggleStatus(ActionEvent event) {
        // Code pour activer ou désactiver un utilisateur
        Utilisateur selectedUser = listeUser.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                boolean newStatus = !selectedUser.isIs_active(); // Inverser le statut
                serviceUtilisateur.updateStatus(selectedUser.getId(), newStatus); // Mettre à jour dans la base de données
                selectedUser.setIs_active(newStatus); // Mettre à jour localement l'objet sélectionné

                // Rafraîchir l'affichage des utilisateurs (utile pour les autres vues)
                afficherUtilisateurs(); // Rafraîchir l'affichage pour refléter le changement de statut

                // Mettre à jour l'affichage du bouton et du label
                updateStatusLabel(); // Mettre à jour le label et le bouton du statut
            } catch (SQLException e) {
                System.err.println("Erreur lors de la mise à jour du statut : " + e.getMessage());
            }
        }
    }


    private void filtrerUtilisateurs(String critere) {
        try {
            List<Utilisateur> utilisateurs = serviceUtilisateur.afficher();
            ObservableList<Utilisateur> utilisateursFiltres = FXCollections.observableArrayList();

            for (Utilisateur utilisateur : utilisateurs) {
                if (utilisateur.getNom().toLowerCase().contains(critere.toLowerCase()) ||
                        utilisateur.getPrenom().toLowerCase().contains(critere.toLowerCase()) ||
                        utilisateur.getEmail().toLowerCase().contains(critere.toLowerCase())  ||
                        utilisateur.getRole().toLowerCase().contains(critere.toLowerCase())) {
                    utilisateursFiltres.add(utilisateur);
                }
            }
            listeUser.setItems(utilisateursFiltres);
        } catch (SQLException e) {
            System.err.println("Erreur lors du filtrage des utilisateurs : " + e.getMessage());
        }
    }

    private void trierUtilisateurs(String critere) {
        try {
            List<Utilisateur> utilisateurs = serviceUtilisateur.afficher();
            ObservableList<Utilisateur> utilisateursObservable = FXCollections.observableArrayList(utilisateurs);

            utilisateursObservable.sort((u1, u2) -> {
                switch (critere) {
                    case "Nom":
                        return u1.getNom().compareToIgnoreCase(u2.getNom());
                    case "Prénom":
                        return u1.getPrenom().compareToIgnoreCase(u2.getPrenom());
                    case "Email":
                        return u1.getEmail().compareToIgnoreCase(u2.getEmail());
                    default:
                        return 0;
                }
            });
            listeUser.setItems(utilisateursObservable);
        } catch (SQLException e) {
            System.err.println("Erreur lors du tri des utilisateurs : " + e.getMessage());
        }
    }

    private void afficherUtilisateurs() {
        try {
            List<Utilisateur> utilisateurs = serviceUtilisateur.afficher();
            ObservableList<Utilisateur> utilisateursObservable = FXCollections.observableArrayList(utilisateurs);
            listeUser.setItems(utilisateursObservable);

            listeUser.setCellFactory(param -> new ListCell<Utilisateur>() {
                @Override
                protected void updateItem(Utilisateur utilisateur, boolean empty) {
                    super.updateItem(utilisateur, empty);
                    if (utilisateur != null && !empty) {
                        // Créer une nouvelle ImageView pour chaque utilisateur dans la cellule
                        ImageView imageView = new ImageView();

                        String cheminImage = utilisateur.getImg_url();  // Récupère le chemin de l'image
                        URL imageUrl = getClass().getResource("/images/profiles/" + cheminImage);

                        if (imageUrl != null) {
                            // Si l'image existe, la charger
                            imageView.setImage(new Image(imageUrl.toExternalForm()));
                        } else {
                            // Si l'image n'existe pas, utiliser une image par défaut
                            imageView.setImage(new Image("file:src/main/resources/images/default.png"));
                        }


                        imageView.setFitHeight(80);
                        imageView.setFitWidth(80);
                        imageView.setPreserveRatio(true);

                        // Infos texte
                        VBox infoBox = new VBox(
                                new Label("Nom: " + utilisateur.getNom()),
                                new Label("Prénom: " + utilisateur.getPrenom()),
                                new Label("Email: " + utilisateur.getEmail()),
                                new Label("Téléphone: " + utilisateur.getTelephone()),
                                new Label("Adresse: " + utilisateur.getAdresse()),
                                new Label("Sexe: " + utilisateur.getSexe()),
                                new Label("Rôle: " + utilisateur.getRole()),
                                new Label("Statut: " + (utilisateur.isIs_active() ? "Actif" : "Désactivé")) // Afficher le statut
                        );
                        infoBox.setSpacing(2);



                        // Action de sélection
                        setOnMouseClicked(event -> {
                            utilisateurSelectionne = utilisateur;
                            updateStatusLabel(); // Mettre à jour le statut
                        });

                        HBox hbox = new HBox(10, imageView, infoBox);
                        hbox.setStyle("-fx-padding: 10; -fx-background-radius: 8; -fx-border-color: lightgray; -fx-border-radius: 8;");
                        hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                        setGraphic(hbox);
                    } else {
                        setGraphic(null);
                    }
                }
            });

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'affichage des utilisateurs : " + e.getMessage());
        }
    }

    private void updateStatusLabel() {
        if (utilisateurSelectionne != null) {
            statutLabel.setText(utilisateurSelectionne.isIs_active() ? "Statut: Actif" : "Statut: Désactivé");
            btnToggleStatus.setText(utilisateurSelectionne.isIs_active() ? "Désactiver" : "Activer");
            btnToggleStatus.setStyle("-fx-background-color: " + (utilisateurSelectionne.isIs_active() ? "#f62424" : "#189830") + "; -fx-text-fill: white;");
        }
    }
    @FXML
    void btnStats(ActionEvent event) {
        try {
            List<Utilisateur> utilisateurs = serviceUtilisateur.afficher();

            // Compter les rôles
            long patients = utilisateurs.stream().filter(u -> "patient".equalsIgnoreCase(u.getRole())).count();
            long medecins = utilisateurs.stream().filter(u -> "medecin".equalsIgnoreCase(u.getRole())).count();
            long autres = utilisateurs.stream().filter(u -> !"patient".equalsIgnoreCase(u.getRole()) && !"medecin".equalsIgnoreCase(u.getRole())).count();

            // Créer les données pour le diagramme en secteurs
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("Patients", patients),
                    new PieChart.Data("Médecins", medecins),
                    new PieChart.Data("Admins", autres)
            );

            // Créer le diagramme en secteurs
            PieChart pieChart = new PieChart(pieChartData);
            pieChart.setTitle("Répartition des utilisateurs par rôle");
            pieChart.setLabelsVisible(true); // Afficher les étiquettes
            pieChart.setLegendVisible(true); // Afficher la légende

            // Créer une nouvelle scène pour afficher le diagramme
            VBox vbox = new VBox(pieChart);
            vbox.setPadding(new javafx.geometry.Insets(10));
            Scene scene = new Scene(vbox, 600, 400);

            // Ouvrir une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Statistiques des utilisateurs");
            stage.setScene(scene);
            stage.show();

        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul des statistiques : " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors du chargement des statistiques");
            alert.setContentText("Une erreur s'est produite : " + e.getMessage());
            alert.showAndWait();
        }
    }
}
