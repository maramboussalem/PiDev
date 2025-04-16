package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.esprit.services.ServiceUtilisateur;
import tn.esprit.entities.Utilisateur;
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
            // Mettre à jour la liste affichée
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
                    if (empty || utilisateur == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Text userInfo = new Text( utilisateur.getId() + " - " + utilisateur.getNom() + " " + utilisateur.getPrenom() + " - " + utilisateur.getEmail() + " - " + utilisateur.getTelephone() + " - " + utilisateur.getAdresse() + " - " + utilisateur.getSexe() + " - " + utilisateur.getRole() + " - " + utilisateur.getAntecedents_medicaux() + " - " + utilisateur.getHopital() + " - " + utilisateur.getSpecialite() + "-" + (utilisateur.isIs_active() ? "Actif" : "Inactif"));

                        /*Button btnSupprimer = new Button("Supprimer");
                        btnSupprimer.setStyle("-fx-background-color: #ff8800; -fx-text-fill: white;");
                        btnSupprimer.setOnAction(event -> supprimerUtilisateur(utilisateur));

                        Button btnDetails = new Button("Détails");
                        btnDetails.setStyle("-fx-background-color: blue; -fx-text-fill: white;");
                        btnDetails.setOnAction(event -> afficherDetailsUtilisateur(utilisateur));*/

                        // Nouveau bouton Activer/Désactiver
                        Button btnToggleStatus = new Button(utilisateur.isIs_active() ? "Désactiver" : "Activer");
                        btnToggleStatus.setStyle("-fx-background-color: " + (utilisateur.isIs_active() ? "#f62424" : "#189830") + "; -fx-text-fill: white;");
                        btnToggleStatus.setOnAction(event -> {
                            try {
                                serviceUtilisateur.toggleAccountStatus(utilisateur.getId());
                                // Rafraîchir la liste après modification
                                afficherUtilisateurs();
                            } catch (SQLException e) {
                                System.err.println("Erreur lors de la modification du statut : " + e.getMessage());
                            }
                        });

                        HBox hbox = new HBox(10, userInfo, btnToggleStatus/*, btnDetails, btnSupprimer*/ );
                        hbox.setStyle("-fx-padding: 5; -fx-alignment: center-left;");

                        setGraphic(hbox);
                    }
                }
            });
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'affichage des utilisateurs : " + e.getMessage());
        }
    }

   /* private void supprimerUtilisateur(Utilisateur utilisateur) {
        try {
            serviceUtilisateur.supprimer(utilisateur.getId());
            listeUser.getItems().remove(utilisateur);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
        }
    }

    private void afficherDetailsUtilisateur(Utilisateur utilisateur) {
        afficherInterface("/Utilisateur/DetailsUser.fxml", utilisateur);
    }*/

    private void afficherInterface(String cheminFXML, Utilisateur utilisateur) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(cheminFXML));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof DetailsUser) {
                ((DetailsUser) controller).setUtilisateur(utilisateur);
            } else if (controller instanceof updateuser) {
                ((updateuser) controller).setUtilisateur(utilisateur);
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
