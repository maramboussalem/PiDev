package tn.esprit.controllers.ParametresVitaux;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import tn.esprit.entities.ParametresVitaux;
import tn.esprit.services.ParametresVitauxService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class DetaillesParametresVitaux implements Initializable {

    @FXML
    private AnchorPane contentArea;

    @FXML
    private Text dateText, ecgText, fcText, frText, gadText, gscText, nameText, spo2Text, tadText, tasText;

    @FXML
    private ImageView image;

    private ParametresVitaux parametresVitaux;

    private final ParametresVitauxService service = new ParametresVitauxService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadImage();
    }

    private void loadImage() {
        String imagePath = "src/main/resources/images/detaillesParametres.gif";
        File file = new File(imagePath);
        if (file.exists()) {
            image.setImage(new Image(file.toURI().toString()));
        } else {
            System.out.println("Fichier image introuvable : " + imagePath);
            image.setImage(new Image("/images/default.png")); // Image par défaut
        }
    }

    // ✅ Appelé depuis l'autre page pour afficher les données
    public void setParametresVitaux(ParametresVitaux pv) {
        this.parametresVitaux = pv;

        // 🧾 Affichage des détails
        nameText.setText(pv.getName());
        fcText.setText(String.valueOf(pv.getFc()));
        frText.setText(String.valueOf(pv.getFr()));
        ecgText.setText(pv.getEcg());
        tasText.setText(String.valueOf(pv.getTas()));
        tadText.setText(String.valueOf(pv.getTad()));
        spo2Text.setText(String.valueOf(pv.getSpo2()));
        gscText.setText(String.valueOf(pv.getGsc()));
        gadText.setText(String.valueOf(pv.getGad()));

        // Format the date to show only the date part (no time)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // or any format you prefer
        dateText.setText(sdf.format(pv.getCreated_at()));  // Format the date before setting it
    }

    @FXML
    void deleteButton(ActionEvent event) {
        if (parametresVitaux != null) {
            // Create a confirmation alert
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Êtes-vous sûr de vouloir supprimer ce paramètre ?");
            alert.setContentText("Cette action est irréversible.");

            // Wait for the user to respond
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        // Perform the deletion if the user confirmed
                        service.supprimer(parametresVitaux.getId()); // Supprimer de la base
                        System.out.println("✅ Paramètre supprimé : " + parametresVitaux.getName());

                        // Retour à la liste
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ParametresVitaux/IndexParametresVitaux.fxml"));
                            Parent root = loader.load();
                            contentArea.getChildren().clear();
                            contentArea.getChildren().add(root);
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("❌ Erreur lors de la navigation après suppression.");
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("❌ Erreur lors de la suppression : " + e.getMessage());
                    }
                } else {
                    System.out.println("❗ Suppression annulée.");
                }
            });
        } else {
            System.out.println("❗ Aucun paramètre sélectionné pour suppression.");
        }
    }


    @FXML
    void historiqueButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ParametresVitaux/IndexParametresVitaux.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement de la page d'historique : " + e.getMessage());
        }
    }

    @FXML
    void updateButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ParametresVitaux/UpdateParametresVitaux.fxml"));
            Parent root = loader.load();

            // 💡 Récupération du contrôleur de la page de mise à jour
            UpdateParametresVitaux controller = loader.getController();

            // 🟢 Passage des données à mettre à jour
            controller.setParametresVitaux(parametresVitaux);

            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);

        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement de la page de modification : " + e.getMessage());
        }
    }
}
