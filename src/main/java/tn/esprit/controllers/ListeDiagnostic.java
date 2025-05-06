package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import tn.esprit.entities.Diagnostic;
import tn.esprit.services.diagnosticService;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListeDiagnostic implements Initializable {

    @FXML
    private AnchorPane contentArea;

    @FXML
    private ImageView image;

    @FXML
    private ListView<Diagnostic> listview;

    @FXML
    private TextField searchBar;

    private final diagnosticService service = new diagnosticService();
    private List<Diagnostic> allDiagnostics;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadImage();
        loadDiagnostics();
        setupDoubleClick();

        searchBar.textProperty().addListener((obs, oldVal, newVal) -> filterDiagnostics(newVal));
    }

    private void loadImage() {
        URL imageUrl = getClass().getResource("/images/indexDiagnostic.gif");
        if (imageUrl != null) {
            image.setImage(new Image(imageUrl.toExternalForm()));
        }
    }

    private void loadDiagnostics() {
        try {
            allDiagnostics = service.afficher();
            listview.getItems().setAll(allDiagnostics);
        } catch (Exception e) {
            System.err.println("Erreur de chargement : " + e.getMessage());
        }

        listview.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Diagnostic d, boolean empty) {
                super.updateItem(d, empty);
                if (empty || d == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    AnchorPane cellPane = new AnchorPane();

                    Label nameLabel = new Label("🧠 " + d.getName());
                    nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #00b8bb;");

                    Label dateLabel = new Label("📅 " + d.getDate_diagnostic());
                    dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #4c4c4c;");

                    Separator separator = new Separator();

                    AnchorPane.setTopAnchor(nameLabel, 10.0);
                    AnchorPane.setLeftAnchor(nameLabel, 10.0);

                    AnchorPane.setTopAnchor(dateLabel, 35.0);
                    AnchorPane.setLeftAnchor(dateLabel, 10.0);

                    AnchorPane.setTopAnchor(separator, 60.0);
                    AnchorPane.setLeftAnchor(separator, 10.0);
                    AnchorPane.setRightAnchor(separator, 10.0);

                    cellPane.getChildren().addAll(nameLabel, dateLabel, separator);
                    setGraphic(cellPane);
                }
            }
        });
    }

    private void setupDoubleClick() {
        listview.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Diagnostic selected = listview.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Diagnostic/detailsDiagnostic.fxml"));
                        Parent root = loader.load();
                        DetailsDiagnostic controller = loader.getController();
                        controller.initialize(selected);
                        contentArea.getChildren().clear();
                        contentArea.getChildren().add(root);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void filterDiagnostics(String searchText) {
        List<Diagnostic> filtered = allDiagnostics.stream()
                .filter(d -> d.getName().toLowerCase().contains(searchText.toLowerCase()))
                .toList();
        listview.getItems().setAll(filtered);
    }

    @FXML
    public void addButton(javafx.event.ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Diagnostic/addDiagnostic.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void updateButton(javafx.event.ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Diagnostic/UpdateDiagnostic.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showButton(javafx.event.ActionEvent actionEvent) {
        // Get the selected diagnostic
        Diagnostic selected = listview.getSelectionModel().getSelectedItem();

        if (selected != null) {
            try {
                // Load the FXML for the DetailsDiagnostic view
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Diagnostic/DetailsDiagnostic.fxml"));
                Parent root = loader.load();

                // Get the controller of the DetailsDiagnostic view
                DetailsDiagnostic controller = loader.getController();

                // Pass the selected diagnostic to the controller
                controller.initialize(selected);

                // Clear the content area and add the DetailsDiagnostic view
                contentArea.getChildren().clear();
                contentArea.getChildren().add(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No diagnostic selected.");
        }
    }

}
