package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import tn.esprit.entities.Consultation;
import tn.esprit.services.ConsultationService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class IndexConsultation implements Initializable {

    private final ConsultationService service = new ConsultationService();
    private final ObservableList<Consultation> consultationList = FXCollections.observableArrayList();

    @FXML
    private ListView<Consultation> listview;

    @FXML
    private Button add;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listview.setItems(consultationList);
        add.setOnAction(this::gotoadd);
        try {
            refreshList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        setUpButtons();
    }

    public void refreshList() throws SQLException {
        consultationList.clear();
        consultationList.addAll(service.afficher());
    }

    private void setUpButtons() {
        listview.setCellFactory(param -> new ListCell<Consultation>() {
            @Override
            protected void updateItem(Consultation consultation, boolean empty) {
                super.updateItem(consultation, empty);
                if (empty || consultation == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox container = new VBox(8);
                    container.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 15; -fx-background-radius: 10;");
                    container.setPrefWidth(880);
                    container.setMaxWidth(Double.MAX_VALUE);

                    Label name = new Label("👤 Nom: " + consultation.getNomPatient());
                    name.setStyle("-fx-font-weight: bold; -fx-font-size: 15;");

                    Label date = new Label("📅 Date: " + consultation.getDateConsultation());
                    date.setStyle("-fx-font-size: 14;");

                    Label diag = new Label("🩺 Diagnostic: " + consultation.getDiagnostic());
                    diag.setStyle("-fx-font-size: 14;");

                    Button detailButton = new Button("🔍 Détails");

                    Button modifyButton = new Button("✏️ Modifier");
                    Button deleteButton = new Button("🗑 Supprimer");

                    detailButton.setStyle("-fx-background-color: #5c67f2; -fx-text-fill: white; -fx-background-radius: 5;");

                    modifyButton.setStyle("-fx-background-color: #00b8bb; -fx-text-fill: white; -fx-background-radius: 5;");
                    deleteButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-background-radius: 5;");

                    detailButton.setOnAction(event -> showDetailsConsultation(consultation));

                    modifyButton.setOnAction(event -> modifyConsultation(consultation));
                    deleteButton.setOnAction(event -> {
                        try {
                            deleteConsultation(consultation);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });

                    HBox buttonBox = new HBox(10, detailButton, modifyButton, deleteButton);
                    buttonBox.setAlignment(Pos.CENTER_RIGHT);

                    container.getChildren().addAll(name, date, diag, buttonBox);
                    setGraphic(container);
                }
            }
        });
    }

    private void modifyConsultation(Consultation consultation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/consultation/update.fxml"));
            Parent root = loader.load();

            UpdateConsultation updateController = loader.getController();
            updateController.setConsultation(consultation);

            Stage stage = new Stage();
            stage.setTitle("Modifier Consultation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteConsultation(Consultation consultation) throws SQLException {
        service.supprimer(consultation.getId());
        refreshList();
    }

    @FXML
    public void gotoadd(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/consultation/addConsultation.fxml"));
            Parent root = loader.load();


            Stage newStage = new Stage(); // Create new window
            newStage.setTitle("Ajouter Consultation");
            newStage.setScene(new Scene(root));
            newStage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    private void showDetailsConsultation(Consultation consultation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/consultation/showDetailsConsultation.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the selected consultation
            showDetailsConsultation controller = loader.getController();
            controller.setConsultation(consultation);

            // Open new window
            Stage stage = new Stage();
            stage.setTitle("Détails Consultation");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
