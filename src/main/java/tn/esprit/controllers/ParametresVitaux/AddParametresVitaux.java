package tn.esprit.controllers.ParametresVitaux;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.entities.ParametresVitaux;
import tn.esprit.services.ParametresVitauxService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.ResourceBundle;

public class AddParametresVitaux implements Initializable {

    @FXML
    private AnchorPane contentArea;


    @FXML
    private DatePicker created_at;

    @FXML
    private Label dateError;

    @FXML
    private TextField ecg, fc, fr, gad, gsc, name, spo2, tad, tas;

    @FXML
    private Label ecgError, fcError, frError, gadError, gscError, nameError, spo2Error, tadError, tasError;

    @FXML
    private ImageView image;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadImage();
    }


    private void loadImage() {
        String imagePath = "src/main/resources/images/ParametresVitaux.jpg";
        File file = new File(imagePath);
        if (file.exists()) {
            Image img = new Image(file.toURI().toString());
            image.setImage(img);
        } else {
            System.out.println("Fichier image introuvable : " + imagePath);
            image.setImage(new Image("/images/default.png")); // Assurez-vous d’avoir une image par défaut
        }
    }

    @FXML
    void add_parametresVitaux(ActionEvent event) {
        if (validateInputs()) {
            try {
                ParametresVitaux pv = new ParametresVitaux();
                pv.setName(name.getText());
                pv.setFc(Integer.parseInt(fc.getText()));
                pv.setFr(Integer.parseInt(fr.getText()));
                pv.setEcg(Integer.parseInt(ecg.getText()));
                pv.setTas(Integer.parseInt(tas.getText()));
                pv.setTad(Integer.parseInt(tad.getText()));
                pv.setSpo2(Integer.parseInt(spo2.getText()));
                pv.setGsc(Integer.parseInt(gsc.getText()));
                pv.setGad(Integer.parseInt(gad.getText()));
                pv.setCreated_at(Date.valueOf(created_at.getValue()));

                ParametresVitauxService service = new ParametresVitauxService();
                service.ajouter(pv);

                System.out.println("✅ Paramètres Vitaux ajoutés avec succès !");
                historiqueButton(event); // Redirige automatiquement vers la liste après ajout
            } catch (Exception e) {
                System.err.println("❌ Erreur lors de l'ajout des paramètres vitaux : " + e.getMessage());
            }
        }
    }

    @FXML
    void closeButton(ActionEvent event) {
        Button button = (Button) event.getSource();

        // Apply DropShadow effect
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#4f9efc"));
        shadow.setRadius(10);
        shadow.setSpread(0.5);
        button.setEffect(shadow);

        // Soft Scale Animation (smaller effect)
        ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(0.97);  // Just a slight shrink
        scale.setToY(0.97);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);

        scale.setOnFinished(e -> {
            // Navigate to home.fxml
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

    @FXML
    void historiqueButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ParametresVitaux/IndexParametresVitaux.fxml"));
            Parent root = loader.load();

            // ⚠️ Important : vérifier s'il a déjà un parent
            if (root.getParent() != null) {
                ((AnchorPane) root.getParent()).getChildren().remove(root);
            }

            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);

        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement de la page d'historique : " + e.getMessage());
        }
    }



    private boolean validateInputs() {
        boolean isValid = true;

        nameError.setText("");
        fcError.setText("");
        frError.setText("");
        ecgError.setText("");
        tasError.setText("");
        tadError.setText("");
        spo2Error.setText("");
        gscError.setText("");
        gadError.setText("");
        dateError.setText("");

        if (name.getText().isEmpty()) {
            isValid = false;
            nameError.setText("Nom requis.");
        }

        if (!isInteger(fc.getText())) {
            isValid = false;
            fcError.setText("FC invalide.");
        }

        if (!isInteger(fr.getText())) {
            isValid = false;
            frError.setText("FR invalide.");
        }

        if (!isInteger(ecg.getText())) {
            isValid = false;
            ecgError.setText("ECG invalide.");
        }

        if (!isInteger(tas.getText())) {
            isValid = false;
            tasError.setText("TAS invalide.");
        }

        if (!isInteger(tad.getText())) {
            isValid = false;
            tadError.setText("TAD invalide.");
        }

        if (!isInteger(spo2.getText())) {
            isValid = false;
            spo2Error.setText("SpO2 invalide.");
        }

        if (!isInteger(gsc.getText())) {
            isValid = false;
            gscError.setText("GSC invalide.");
        }

        if (!isInteger(gad.getText())) {
            isValid = false;
            gadError.setText("GAD invalide.");
        }

        if (created_at.getValue() == null) {
            isValid = false;
            dateError.setText("Date requise.");
        }

        return isValid;
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
