package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import tn.esprit.entities.ConnectionHistory;
import tn.esprit.entities.Utilisateur;
import tn.esprit.services.ServiceUtilisateur;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class HistoriqueController {

    @FXML
    private TableView<ConnectionHistory> historyTable;

    @FXML
    private TableColumn<ConnectionHistory, Timestamp> timestampColumn;

    @FXML
    private TableColumn<ConnectionHistory, String> eventTypeColumn;

    private Utilisateur utilisateur;
    private ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        initializeTable();
    }

    private void initializeTable() {
        // Configurer les colonnes
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        eventTypeColumn.setCellValueFactory(new PropertyValueFactory<>("eventType"));

        // Formatter le timestamp pour un affichage lisible
        timestampColumn.setCellFactory(column -> new javafx.scene.control.TableCell<ConnectionHistory, Timestamp>() {
            private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(dateFormat.format(item));
                }
            }
        });

        // Formatter les types d'événements en français
        eventTypeColumn.setCellFactory(column -> new javafx.scene.control.TableCell<ConnectionHistory, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    switch (item) {
                        case "CONNEXION":
                            setText("Connexion");
                            break;
                        case "CHANGEMENT_MOT_DE_PASSE":
                            setText("Changement de mot de passe");
                            break;
                        case "MODIFICATION_INFORMATIONS":
                            setText("Modification des informations");
                            break;
                        case "INSCRIPTION":
                            setText("Inscription");
                            break;
                        case "SUPPRESSION_COMPTE":
                            setText("Suppression du compte");
                            break;
                        case "ACTIVATION_COMPTE":
                            setText("Activation du compte");
                            break;
                        case "DESACTIVATION_COMPTE":
                            setText("Désactivation du compte");
                            break;
                        default:
                            setText(item);
                    }
                }
            }
        });

        // Charger les données
        loadHistory();
    }

    private void loadHistory() {
        try {
            ObservableList<ConnectionHistory> historyList = FXCollections.observableArrayList(
                    serviceUtilisateur.getConnectionHistory(utilisateur.getId())
            );
            historyTable.setItems(historyList);
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement de l'historique : " + e.getMessage());
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible de charger l'historique : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    void closeWindow(ActionEvent event) {
        Stage stage = (Stage) historyTable.getScene().getWindow();
        stage.close();
    }
}