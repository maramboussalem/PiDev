package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.esprit.entities.Diagnostic;
import tn.esprit.services.diagnosticService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListeDiagnostic implements Initializable {

    @FXML
    private ListView<Diagnostic> listview;

    private final diagnosticService service = new diagnosticService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDiagnostics();

        listview.setCellFactory(lv -> new ListCell<>() {
            private final Button deleteBtn = new Button("Supprimer");
            private final Button editBtn = new Button("Modifier");
            private final Button detailsBtn = new Button("Afficher Détaille");
            private final HBox buttons = new HBox(10, editBtn, deleteBtn, detailsBtn);

            {
                // Action for delete button
                deleteBtn.setOnAction(event -> {
                    Diagnostic d = getItem();
                    if (d != null) {
                        try {
                            service.supprimer(d.getId());
                            listview.getItems().remove(d);
                            System.out.println("Diagnostic supprimé : " + d.getName());
                        } catch (Exception e) {
                            System.err.println("Erreur lors de la suppression : " + e.getMessage());
                        }
                    }
                });

                // Action for edit button
                editBtn.setOnAction(event -> {
                    Diagnostic d = getItem();
                    if (d != null) {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Diagnostic/updateDiagnostic.fxml"));
                            Parent root = loader.load();

                            // Access controller and pass selected diagnostic
                            tn.esprit.controllers.UpdateDiagnostic controller = loader.getController();
                            controller.initialize(d);  // call method to set data

                            Stage stage = new Stage();
                            stage.setTitle("Modifier Diagnostic");
                            stage.setScene(new Scene(root));
                            stage.show();
                        } catch (IOException e) {
                            System.err.println("Erreur lors de l'ouverture de la fenêtre de mise à jour : " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });

                // Action for details button
                detailsBtn.setOnAction(event -> {
                    Diagnostic d = getItem();
                    if (d != null) {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Diagnostic/detailsDiagnostic.fxml"));
                            Parent root = loader.load();

                            // Access controller and pass selected diagnostic
                            tn.esprit.controllers.DetailsDiagnostic controller = loader.getController();
                            controller.initialize(d);  // call method to set data

                            Stage stage = new Stage();
                            stage.setTitle("Détail Diagnostic");
                            stage.setScene(new Scene(root));
                            stage.show();
                        } catch (IOException e) {
                            System.err.println("Erreur lors de l'ouverture de la fenêtre des détails : " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Diagnostic diagnostic, boolean empty) {
                super.updateItem(diagnostic, empty);
                if (empty || diagnostic == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(String.format("Nom: %s | Desc: %s | Date: %s",
                            diagnostic.getName(),
                            diagnostic.getDescription(),
                            diagnostic.getDate_diagnostic().toString()));
                    setGraphic(buttons);
                }
            }
        });
    }

    private void loadDiagnostics() {
        try {
            List<Diagnostic> diagnostics = service.afficher();
            listview.getItems().clear();
            listview.getItems().addAll(diagnostics);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des diagnostics : " + e.getMessage());
        }
    }

    @FXML
    void add(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Diagnostic/addDiagnostic.fxml"));
            Parent root = loader.load();

            Stage newStage = new Stage();
            newStage.setTitle("Ajouter Diagnostic");
            newStage.setScene(new Scene(root));
            newStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Bouton Ajouter cliqué");
    }
}
