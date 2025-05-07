package tn.esprit.controllers.ParametresVitaux;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
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
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

public class IndexParametresVitaux implements Initializable {

    @FXML
    private AnchorPane contentArea;

    @FXML
    private LineChart<String, Number> chartArea;

    @FXML
    private ImageView image;

    @FXML
    private TextField ecgFilter;

    @FXML
    private TextField fcFilter;

    @FXML
    private AnchorPane filterArea;

    @FXML
    private TextField frFilter;

    @FXML
    private TextField gadFilter;

    @FXML
    private TextField gscFilter;

    @FXML
    private TextField nameFilter;

    @FXML
    private TextField tadFilter;

    @FXML
    private TextField tasFilter;

    @FXML
    private ListView<ParametresVitaux> listview;

    private final ParametresVitauxService service = new ParametresVitauxService();

    private boolean chartVisible = false;
    private boolean filterVisible = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadParametres();
        loadImage();
        setupDoubleClick();
        chartArea.setVisible(false);
        filterArea.setVisible(false);
        setupFilterListeners();
    }

    private void loadImage() {
        URL imageUrl = getClass().getResource("/images/#indexParametres.gif");
        if (imageUrl != null) {
            image.setImage(new Image(imageUrl.toExternalForm()));
        } else {
            System.out.println("Fichier image introuvable dans le classpath !");
            URL defaultImg = getClass().getResource("/images/#default.png");
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
                    AnchorPane cellPane = new AnchorPane();
                    Label nameLabel = new Label("\uD83D\uDC64 " + pv.getName());
                    nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #00b8bb;");

                    Label dateLabel = new Label("\uD83D\uDCC5 " + pv.getCreated_at());
                    dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #4c4c4c;");

                    Separator separator = new Separator();
                    separator.setStyle("-fx-background-color: #00b8bb; -fx-background-width: 2;");

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
                ParametresVitaux selected = listview.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ParametresVitaux/DetaillesParametresVitaux.fxml"));
                        Parent root = loader.load();

                        DetaillesParametresVitaux controller = loader.getController();
                        controller.setParametresVitaux(selected);

                        contentArea.getChildren().clear();
                        contentArea.getChildren().add(root);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setupFilterListeners() {
        // Add listeners to all filter text fields for real-time filtering
        nameFilter.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        ecgFilter.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        fcFilter.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        frFilter.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        tasFilter.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        tadFilter.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        gscFilter.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        gadFilter.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());
    }

    private void applyFilters() {
        try {
            List<ParametresVitaux> allParams = service.afficher();
            List<ParametresVitaux> filtered = allParams.stream().filter(pv -> {
                boolean matches = true;

                if (!nameFilter.getText().isEmpty())
                    matches &= pv.getName().toLowerCase().contains(nameFilter.getText().toLowerCase());

                if (!ecgFilter.getText().isEmpty())
                    matches &= pv.getEcg().toLowerCase().contains(ecgFilter.getText().toLowerCase());

                if (!fcFilter.getText().isEmpty())
                    matches &= String.valueOf(pv.getFc()).contains(fcFilter.getText());

                if (!frFilter.getText().isEmpty())
                    matches &= String.valueOf(pv.getFr()).contains(frFilter.getText());

                if (!tasFilter.getText().isEmpty())
                    matches &= String.valueOf(pv.getTas()).contains(tasFilter.getText());

                if (!tadFilter.getText().isEmpty())
                    matches &= String.valueOf(pv.getTad()).contains(tadFilter.getText());

                if (!gscFilter.getText().isEmpty())
                    matches &= String.valueOf(pv.getGsc()).contains(gscFilter.getText());

                if (!gadFilter.getText().isEmpty())
                    matches &= String.valueOf(pv.getGad()).contains(gadFilter.getText());

                return matches;
            }).toList();

            listview.getItems().setAll(filtered);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void actualiserButton(ActionEvent event) {
        loadParametres();
    }

    @FXML
    void addButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ParametresVitaux/AddParametresVitaux.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void statsButton(ActionEvent event) {
        if (chartVisible) {
            contentArea.getChildren().remove(chartArea);
            chartVisible = false;
        } else {
            chartArea.setVisible(true);
            chartArea.getData().clear();

            try {
                List<ParametresVitaux> parametresList = service.afficher();

                XYChart.Series<String, Number> fcSeries = new XYChart.Series<>();
                fcSeries.setName("FC");

                XYChart.Series<String, Number> frSeries = new XYChart.Series<>();
                frSeries.setName("FR");

                XYChart.Series<String, Number> tasSeries = new XYChart.Series<>();
                tasSeries.setName("TAS");

                XYChart.Series<String, Number> tadSeries = new XYChart.Series<>();
                tadSeries.setName("TAD");

                XYChart.Series<String, Number> gscSeries = new XYChart.Series<>();
                gscSeries.setName("GSC");

                XYChart.Series<String, Number> gadSeries = new XYChart.Series<>();
                gadSeries.setName("GAD");

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");

                boolean outOfRange = false;
                StringBuilder anomalies = new StringBuilder();

                for (ParametresVitaux pv : parametresList) {
                    String formattedDate = sdf.format(pv.getCreated_at());

                    fcSeries.getData().add(new XYChart.Data<>(formattedDate, pv.getFc()));
                    tasSeries.getData().add(new XYChart.Data<>(formattedDate, pv.getTas()));
                    tadSeries.getData().add(new XYChart.Data<>(formattedDate, pv.getTad()));
                    gscSeries.getData().add(new XYChart.Data<>(formattedDate, pv.getGsc()));
                    gadSeries.getData().add(new XYChart.Data<>(formattedDate, pv.getGad()));

                    if (pv.getFc() < 60 || pv.getFc() > 100) {
                        anomalies.append("FC: ").append(pv.getFc()).append(" (").append(formattedDate).append(")\n");
                        outOfRange = true;
                    }
                    if (pv.getTas() < 90 || pv.getTas() > 140) {
                        anomalies.append("TAS: ").append(pv.getTas()).append(" (").append(formattedDate).append(")\n");
                        outOfRange = true;
                    }
                    if (pv.getTad() < 60 || pv.getTad() > 90) {
                        anomalies.append("TAD: ").append(pv.getTad()).append(" (").append(formattedDate).append(")\n");
                        outOfRange = true;
                    }
                    if (pv.getGsc() < 15 || pv.getGsc() > 15) {
                        anomalies.append("GSC: ").append(pv.getGsc()).append(" (").append(formattedDate).append(")\n");
                        outOfRange = true;
                    }
                    if (pv.getGad() < 70 || pv.getGad() > 110) {
                        anomalies.append("GAD: ").append(pv.getGad()).append(" (").append(formattedDate).append(")\n");
                        outOfRange = true;
                    }
                }

                chartArea.getData().addAll(fcSeries, frSeries, tasSeries, tadSeries, gscSeries, gadSeries);

                chartArea.getXAxis().setTickLabelsVisible(false);
                chartArea.getXAxis().setTickMarkVisible(false);
                chartArea.getXAxis().setVisible(false);

                if (!contentArea.getChildren().contains(chartArea)) {
                    contentArea.getChildren().add(chartArea);
                }

                chartVisible = true;

                if (outOfRange) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Alerte Santé");
                    alert.setHeaderText("Paramètres vitaux anormaux détectés");
                    alert.setContentText(anomalies.toString());
                    alert.showAndWait();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void filterButton(ActionEvent event) {
        filterVisible = !filterVisible;
        filterArea.setVisible(filterVisible);

        if (!filterVisible) {
            // Clear filters and reset the list
            nameFilter.clear();
            ecgFilter.clear();
            fcFilter.clear();
            frFilter.clear();
            tasFilter.clear();
            tadFilter.clear();
            gscFilter.clear();
            gadFilter.clear();
            loadParametres(); // Reset to full list
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