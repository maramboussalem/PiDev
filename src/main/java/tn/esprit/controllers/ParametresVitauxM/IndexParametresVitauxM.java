package tn.esprit.controllers.ParametresVitauxM;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.entities.ParametresVitaux;
import tn.esprit.services.ParametresVitauxService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class IndexParametresVitauxM implements Initializable {

    @FXML
    private AnchorPane contentArea;

    @FXML
    private ImageView image;

    @FXML
    private ListView<ParametresVitaux> listview;

    private final ParametresVitauxService service = new ParametresVitauxService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadParametres();
        loadImage();
        setupDoubleClick();
    }

    private void loadImage() {
        URL imageUrl = getClass().getResource("/images/indexParametres.gif");
        if (imageUrl != null) {
            image.setImage(new Image(imageUrl.toExternalForm()));
        } else {
            System.out.println("Fichier image introuvable dans le classpath !");
            URL defaultImg = getClass().getResource("/images/default.png");
            if (defaultImg != null) {
                image.setImage(new Image(defaultImg.toExternalForm()));
                System.out.println("Image par défaut chargée avec succès");
            }
        }
    }

    private void loadParametres() {
        try {
            List<ParametresVitaux> parametres = service.afficher();
            listview.getItems().setAll(parametres);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement des paramètres vitaux : " + e.getMessage());
        }

        listview.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ParametresVitaux pv, boolean empty) {
                super.updateItem(pv, empty);
                if (empty || pv == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Create a custom layout for each list item
                    AnchorPane cellPane = new AnchorPane();

                    // Create and style name label
                    Label nameLabel = new Label("👤 " + pv.getName());
                    nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #00b8bb;");

                    // Create and style date label
                    Label dateLabel = new Label("📅 " + pv.getCreated_at());
                    dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #4c4c4c;");

                    // Create a separator for design
                    Separator separator = new Separator();
                    separator.setStyle("-fx-background-color: #00b8bb; -fx-background-width: 2;");

                    // Position elements inside the AnchorPane
                    AnchorPane.setTopAnchor(nameLabel, 10.0);
                    AnchorPane.setLeftAnchor(nameLabel, 10.0);

                    AnchorPane.setTopAnchor(dateLabel, 35.0);
                    AnchorPane.setLeftAnchor(dateLabel, 10.0);

                    AnchorPane.setTopAnchor(separator, 60.0);
                    AnchorPane.setLeftAnchor(separator, 10.0);
                    AnchorPane.setRightAnchor(separator, 10.0);

                    // Add elements to the cell pane
                    cellPane.getChildren().addAll(nameLabel, dateLabel, separator);

                    // Set the custom graphic for the cell
                    setGraphic(cellPane);
                }
            }
        });
    }


    private void setupDoubleClick() {
        listview.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                ParametresVitaux selected = listview.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ParametresVitauxM/DetaillesParametresVitauxM.fxml"));
                        Parent root = loader.load();

                        DetaillesParametresVitauxM controller = loader.getController();
                        controller.setParametresVitauxM(selected);

                        contentArea.getChildren().clear();
                        contentArea.getChildren().add(root);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @FXML
    void actualiserButton(ActionEvent event) {
        loadParametres(); // Refresh the list
    }

    @FXML
    void addButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ParametresVitauxM/AddParametresVitauxM.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void closeButton(ActionEvent event) {
        Button button = (Button) event.getSource();

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#4f9efc"));
        shadow.setRadius(10);
        shadow.setSpread(0.5);
        button.setEffect(shadow);

        ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(0.97);
        scale.setToY(0.97);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);

        scale.setOnFinished(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/home.fxml"));
                Parent homeView = loader.load();
                Scene scene = button.getScene();
                Stage stage = (Stage) scene.getWindow();
                stage.setScene(new Scene(homeView));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        scale.play();
    }
}