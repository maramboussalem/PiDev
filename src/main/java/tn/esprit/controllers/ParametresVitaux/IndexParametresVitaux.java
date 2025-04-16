package tn.esprit.controllers.ParametresVitaux;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.entities.ParametresVitaux;
import tn.esprit.services.ParametresVitauxService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class IndexParametresVitaux implements Initializable {

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
    }


    private void loadImage() {
        URL imageUrl = getClass().getResource("/images/historiqueParmetres.jpg");
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
            List<ParametresVitaux> parametres = service.afficher();  // La méthode afficher() peut lancer SQLException
            listview.getItems().setAll(parametres);
        } catch (SQLException e) {
            e.printStackTrace();  // Gérer l'exception si elle se produit
            System.out.println("Erreur lors du chargement des paramètres vitaux : " + e.getMessage());
        }

        listview.setCellFactory(lv -> new ListCell<>() {
            private final Button deleteBtn = new Button("Supprimer");
            private final Button editBtn = new Button("Modifier");
            private final HBox buttons = new HBox(10, editBtn, deleteBtn);

            {
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
                editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");

                deleteBtn.setOnAction(event -> {
                    ParametresVitaux pv = getItem();
                    if (pv != null) {
                        try {
                            service.supprimer(pv.getId());  // La méthode supprimer() peut aussi lancer SQLException
                            listview.getItems().remove(pv);
                            System.out.println("Paramètre supprimé : " + pv.getName());
                        } catch (SQLException e) {
                            e.printStackTrace();  // Gérer l'exception de suppression
                            System.out.println("Erreur lors de la suppression : " + e.getMessage());
                        }
                    }
                });

                editBtn.setOnAction(event -> {
                    ParametresVitaux pv = getItem();
                    if (pv != null) {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ParametresVitaux/UpdateParametresVitaux.fxml"));
                            Parent root = loader.load();

                            // Inject the selected ParametresVitaux
                            UpdateParametresVitaux controller = loader.getController();
                            controller.initialize(pv);

                            // Find the contentArea from current scene (assumes the button is inside the same scene)
                            AnchorPane contentArea = (AnchorPane) editBtn.getScene().lookup("#contentArea");
                            contentArea.getChildren().clear();
                            contentArea.getChildren().add(root);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }

            @Override
            protected void updateItem(ParametresVitaux pv, boolean empty) {
                super.updateItem(pv, empty);
                if (empty || pv == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText("👤 " + pv.getName() + " | ❤️ FC: " + pv.getFc() +
                            " | 🫁 FR: " + pv.getFr() + " | ❤️ ECG: " + pv.getEcg() +
                            " | 📈 TAS: " + pv.getTas() + " / TAD: " + pv.getTad() +
                            " | 🧠 GSC: " + pv.getGsc() + " | 🎯 GAD: " + pv.getGad() +
                            " | 📅 " + pv.getCreated_at());
                    setGraphic(buttons);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ParametresVitaux/AddParametresVitaux.fxml"));
            Parent root = loader.load();

            // If AddConsultationController needs the utilisateurConnecte, you can do:
            // AddConsultationController controller = loader.getController();
            // controller.setUtilisateur(utilisateurConnecte);

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
