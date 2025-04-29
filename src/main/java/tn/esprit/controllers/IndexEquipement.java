package tn.esprit.controllers;

import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.entities.Equipement;
import tn.esprit.services.EquipementService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class IndexEquipement {

    @FXML
    private ListView<Equipement> listViewEquipements;
    @FXML
    private TextField searchField;
    @FXML
    private Pagination pagination;
    @FXML
    private PieChart etatPieChart;
    @FXML
    private ListView<String> abimeListView;
    @FXML
    private Label abimeLabel;

    private final EquipementService service = new EquipementService();
    private ObservableList<Equipement> allEquipements;
    private static final int ITEMS_PER_PAGE = 5;

    @FXML
    public void initialize() {
        loadEquipements();
        searchField.textProperty().addListener((obs, oldText, newText) -> filterEquipements(newText));

        listViewEquipements.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double click
                Equipement selected = listViewEquipements.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    afficherQRCode(selected);
                }
            }
        });
    }

    private void loadEquipements() {
        try {
            allEquipements = FXCollections.observableArrayList(service.afficher());
            setupPagination();
            updateEtatStatistics();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupPagination() {
        int pageCount = (int) Math.ceil((double) allEquipements.size() / ITEMS_PER_PAGE);
        pagination.setPageCount(pageCount);
        pagination.setPageFactory(this::createPage);
    }

    private VBox createPage(int pageIndex) {
        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, allEquipements.size());
        List<Equipement> pageItems = allEquipements.subList(fromIndex, toIndex);
        listViewEquipements.setItems(FXCollections.observableArrayList(pageItems));

        listViewEquipements.setCellFactory(param -> new ListCell<Equipement>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(Equipement item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    imageView.setFitWidth(60);
                    imageView.setFitHeight(60);
                    try {
                        Image img = new Image("file:" + item.getImg());
                        imageView.setImage(img);
                    } catch (Exception e) {
                        System.out.println("Erreur chargement image: " + e.getMessage());
                    }

                    VBox container = new VBox(5, imageView);

                    setGraphic(container);
                    setText("Nom: " + item.getNomEquipement() +
                            "\nDescription: " + item.getDescription() +
                            "\nQuantité: " + item.getQuantiteStock() +
                            "\nPrix: " + item.getPrixUnitaire() +
                            "\nÉtat: " + item.getEtatEquipement() +
                            "\nDate: " + item.getDateAchat());
                }
            }
        });

        return new VBox(listViewEquipements);
    }

    private void filterEquipements(String keyword) {
        List<Equipement> filtered = allEquipements.stream()
                .filter(e -> e.getNomEquipement().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        allEquipements.setAll(filtered);
        setupPagination();
        updateEtatStatistics();
    }

    private Image generateQRCodeImage(String text) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 150;
        int height = 150;
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
            WritableImage image = new WritableImage(width, height);
            PixelWriter pw = image.getPixelWriter();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    pw.setArgb(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return image;
        } catch (WriterException e) {
            e.printStackTrace();
            return new WritableImage(width, height);
        }
    }

    private void updateEtatStatistics() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        long total = allEquipements.size();
        long neufs = allEquipements.stream().filter(e -> e.getEtatEquipement().equalsIgnoreCase("Neuf")).count();
        long abimes = allEquipements.stream().filter(e -> e.getEtatEquipement().equalsIgnoreCase("Abimé")).count();
        long reparés = allEquipements.stream().filter(e -> e.getEtatEquipement().equalsIgnoreCase("Réparé")).count();

        if (neufs > 0) pieChartData.add(new PieChart.Data("Neuf", neufs));
        if (abimes > 0) pieChartData.add(new PieChart.Data("Abimé", abimes));
        if (reparés > 0) pieChartData.add(new PieChart.Data("Réparé", reparés));

        etatPieChart.setData(pieChartData);
        etatPieChart.setTitle("État des équipements");
        afficherEquipementsAbimes(); // ajouttaha sahih

        checkAnomalies(abimes, total);


    }

    private void afficherEquipementsAbimes() {
        if (allEquipements == null) return; // extra sécurité

        List<String> abimes = allEquipements.stream()
                .filter(e -> e.getEtatEquipement() != null && e.getEtatEquipement().toLowerCase().contains("abim"))
                .map(Equipement::getNomEquipement)
                .collect(Collectors.toList());

        abimeListView.getItems().setAll(abimes);

        abimeLabel.setVisible(true);
        abimeLabel.setText("Équipements Abîmés:");
        abimeListView.setVisible(true);
    }

    private void afficherEquipementsRepares() {
        if (allEquipements == null) return; // extra sécurité

        List<String> repares = allEquipements.stream()
                .filter(e -> e.getEtatEquipement() != null && e.getEtatEquipement().toLowerCase().contains("répar"))
                .map(Equipement::getNomEquipement)
                .collect(Collectors.toList());

        abimeListView.getItems().setAll(repares);

        abimeLabel.setVisible(true);
        abimeLabel.setText("Équipements Réparés:");
        abimeListView.setVisible(true);
    }

    private void afficherEquipementsNeufs() {
        if (allEquipements == null) return; // extra sécurité

        List<String> neufs = allEquipements.stream()
                .filter(e -> e.getEtatEquipement() != null && e.getEtatEquipement().toLowerCase().contains("neuf"))
                .map(Equipement::getNomEquipement)
                .collect(Collectors.toList());

        abimeListView.getItems().setAll(neufs);

        abimeLabel.setVisible(true);
        abimeLabel.setText("Équipements Neufs:");
        abimeListView.setVisible(true);
    }



    private void checkAnomalies(long abimes, long total) {
        if (total == 0) return;
        double ratio = (double) abimes / total;

        if (ratio >= 0.5) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Anomalie détectée");
            alert.setHeaderText("Trop d’équipements Abimés !");
            alert.setContentText("Plus de 50% des équipements sont en état 'Abimés'. Il peut y avoir un problème de maintenance ou de qualité.");
            alert.show();
        }
    }

    @FXML
    private void supprimerEquipement() {
        Equipement selected = listViewEquipements.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un équipement à supprimer !");
            alert.show();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Êtes-vous sûr de vouloir supprimer cet équipement ?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    service.supprimer(selected.getId());
                    loadEquipements();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Équipement supprimé avec succès !");
                    alert.show();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la suppression : " + e.getMessage());
                    alert.show();
                }
            }
        });
    }

    @FXML
    private void modifierEquipement() {
        Equipement selected = listViewEquipements.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un équipement à modifier !");
            alert.show();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Service/EditEquipement.fxml"));
            Parent root = loader.load();
            EditEquipement controller = loader.getController();
            controller.setEquipement(selected);

            Stage stage = new Stage();
            stage.setTitle("Modifier Équipement");
            stage.setScene(new Scene(root));
            stage.show();
            stage.setOnHidden(event -> loadEquipements());

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur chargement fenêtre de modification : " + e.getMessage());
            alert.show();
        }
    }

    @FXML
    private void ouvrirAjoutEquipement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Service/AddEquipement.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter Équipement");
            stage.setScene(new Scene(root));
            stage.show();
            stage.setOnHidden(event -> loadEquipements());
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la fenêtre d'ajout : " + e.getMessage()).show();
        }
    }

    private void afficherQRCode(Equipement equipement) {
        try {
            Stage stage = new Stage();
            stage.setTitle("QR Code pour " + equipement.getNomEquipement());

            ImageView qrImageView = new ImageView(generateQRCodeImage(equipement.getNomEquipement()));
            qrImageView.setFitWidth(300);
            qrImageView.setFitHeight(300);

            ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.5), qrImageView);
            scaleTransition.setFromX(0);
            scaleTransition.setFromY(0);
            scaleTransition.setToX(1);
            scaleTransition.setToY(1);
            scaleTransition.play();

            VBox vbox = new VBox(10, qrImageView);
            vbox.setStyle("-fx-alignment: center; -fx-padding: 20;");

            Scene scene = new Scene(vbox, 350, 350);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur d'affichage du QR code: " + e.getMessage()).show();
        }
    }
}
