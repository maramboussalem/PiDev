package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.Reclamation;
import tn.esprit.services.ReclamationService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class IndexReclamation implements Initializable {

    private final ReclamationService service = new ReclamationService();
    private final ObservableList<Reclamation> reclamationList = FXCollections.observableArrayList();

    @FXML
    private ListView<Reclamation> listview;

    @FXML
    private Button add;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listview.setItems(reclamationList);


        try {
            refreshList();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setUpButtons();
    }

    public void refreshList() throws SQLException {
        reclamationList.clear();
        reclamationList.addAll(service.afficher());
    }

    private void setUpButtons() {
        listview.setCellFactory(param -> new ListCell<Reclamation>() {
            @Override
            protected void updateItem(Reclamation rec, boolean empty) {
                super.updateItem(rec, empty);
                if (empty || rec == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox container = new VBox(8);
                    container.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 15; -fx-background-radius: 10;");
                    container.setPrefWidth(880);
                    container.setMaxWidth(Double.MAX_VALUE);

                    // Labels for reclamation details
                    Label utilisateur = new Label("👤 Nom et prenom: " + rec.getUtilisateurId());
                    utilisateur.setStyle("-fx-font-size: 14;");

                    Label sujet = new Label("📌 Sujet: " + rec.getSujet());
                    sujet.setStyle("-fx-font-size: 14;");

                    Label description = new Label("📝 Description: " + rec.getDescription());
                    description.setStyle("-fx-font-size: 14;");

                    Label statut = new Label("📍 Statut: " + rec.getStatut());
                    statut.setStyle("-fx-font-size: 14;");

                    Label date = new Label("📅 Date: " + rec.getDateCreation());
                    date.setStyle("-fx-font-size: 14;");

                    Label reponse = new Label("💬 Réponse: " + rec.getReponse());
                    reponse.setStyle("-fx-font-size: 14;");

                    // Buttons for each reclamation
                    Button modifyButton = new Button("✏️ Modifier");
                    Button deleteButton = new Button("🗑 Supprimer");
                    Button conversationButton = new Button("💬 Conversation");

                    modifyButton.setStyle("-fx-background-color: #00b8bb; -fx-text-fill: white; -fx-background-radius: 5;");
                    deleteButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-background-radius: 5;");
                    conversationButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5;");

                    // Enable conversation button only if there is a response
                    conversationButton.setDisable(rec.getReponse() == null || rec.getReponse().isEmpty());

                    modifyButton.setOnAction(event -> modifyReclamation(rec));
                    deleteButton.setOnAction(event -> {
                        try {
                            deleteReclamation(rec);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                    conversationButton.setOnAction(event -> openConversation(rec));

                    HBox buttonBox = new HBox(10, modifyButton, deleteButton, conversationButton);
                    buttonBox.setAlignment(Pos.CENTER_RIGHT);

                    container.getChildren().addAll(utilisateur, sujet, description, statut, date);

                    if (rec.getReponse() != null && !rec.getReponse().isEmpty()) {
                        container.getChildren().add(reponse);
                    }

                    container.getChildren().add(buttonBox);

                    setGraphic(container);
                }
            }
        });
    }

    private void modifyReclamation(Reclamation rec) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reclamation/UpdateReclamation.fxml"));
            Parent root = loader.load();

            UpdateReclamation controller = loader.getController();
            controller.setReclamation(rec, this);

            Stage stage = new Stage();
            stage.setTitle("Modifier Réclamation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteReclamation(Reclamation rec) throws SQLException {
        service.supprimer(rec.getId());
        refreshList();
    }

    private void openConversation(Reclamation rec) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reclamation/Conversation.fxml"));
            Parent root = loader.load();

            ConversationController controller = loader.getController();
            controller.setReclamation(rec, false); // Patient view (isAdmin = false)

            Stage stage = new Stage();
            stage.setTitle("Conversation - " + rec.getSujet());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de l'ouverture de la fenêtre de conversation : " + e.getMessage());
            alert.showAndWait();
        }
    }


}