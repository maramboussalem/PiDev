package tn.esprit.controllers.ParametresVitauxM;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import tn.esprit.entities.ParametresVitaux;
import tn.esprit.services.ParametresVitauxService;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class IndexParametresVitauxM implements Initializable {

    @FXML
    private AnchorPane contentArea;

    @FXML
    private ImageView image;

    @FXML
    private ListView<ParametresVitaux> listview;

    @FXML
    private Button riskAssessmentButton;

    @FXML
    private Button diagButton;

    private final ParametresVitauxService service = new ParametresVitauxService();

    // Track processed entries (diagnosed) to hide risk score and set green color
    private final Set<Integer> processedEntryIds = new HashSet<>();

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

            // Sort the list by risk score in descending order
            parametres.sort((pv1, pv2) -> {
                double riskScore1 = calculateRiskScore(pv1);
                double riskScore2 = calculateRiskScore(pv2);
                return Double.compare(riskScore2, riskScore1); // Sort in descending order
            });

            // Set the sorted list in the ListView
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
                    setStyle("");
                } else {
                    // Create a custom layout for each list item
                    AnchorPane cellPane = new AnchorPane();

                    // Create and style name label
                    Label nameLabel = new Label("👤 " + pv.getName());
                    nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #00b8bb;");

                    // Create and style date label
                    Label dateLabel = new Label("📅 " + pv.getCreated_at());
                    dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #4c4c4c;");

                    // Create and style risk score label (only if not processed)
                    Label riskLabel = null;
                    boolean isProcessed = processedEntryIds.contains(pv.getId());
                    if (!isProcessed) {
                        double riskScore = calculateRiskScore(pv);
                        riskLabel = new Label("⚠ Score de Risque: " + String.format("%.2f", riskScore));
                        riskLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + (riskScore > 70 ? "#ff0000" : "#4c4c4c") + ";");
                    }

                    // Create a separator for design
                    Separator separator = new Separator();
                    separator.setStyle("-fx-background-color: #00b8bb; -fx-background-width: 2;");

                    // Position elements inside the AnchorPane
                    AnchorPane.setTopAnchor(nameLabel, 10.0);
                    AnchorPane.setLeftAnchor(nameLabel, 10.0);

                    AnchorPane.setTopAnchor(dateLabel, 35.0);
                    AnchorPane.setLeftAnchor(dateLabel, 10.0);

                    if (riskLabel != null) {
                        AnchorPane.setTopAnchor(riskLabel, 55.0);
                        AnchorPane.setLeftAnchor(riskLabel, 10.0);
                        AnchorPane.setTopAnchor(separator, 80.0);
                    } else {
                        AnchorPane.setTopAnchor(separator, 60.0); // Adjust for no risk label
                    }
                    AnchorPane.setLeftAnchor(separator, 10.0);
                    AnchorPane.setRightAnchor(separator, 10.0);

                    // Add elements to the cell pane
                    cellPane.getChildren().addAll(nameLabel, dateLabel, separator);
                    if (riskLabel != null) {
                        cellPane.getChildren().add(riskLabel);
                    }

                    // Set the custom graphic for the cell
                    setGraphic(cellPane);

                    // Set background color: green for processed, red for high risk, none otherwise
                    if (isProcessed) {
                        setStyle("-fx-background-color: #ccffcc;"); // Light green for processed
                    } else {
                        double riskScore = calculateRiskScore(pv);
                        if (riskScore > 70) {
                            setStyle("-fx-background-color: #ffcccc;"); // Light red for high risk
                        } else {
                            setStyle("");
                        }
                    }
                }
            }
        });
    }


    private double calculateRiskScore(ParametresVitaux pv) {
        double score = 0.0;

        // FC: Normal range 60–100
        if (pv.getFc() < 60 || pv.getFc() > 100) {
            score += 20 * (Math.abs(pv.getFc() - 80) / 20.0); // 80 is midpoint
        }

        // TAS: Normal range 90–140
        if (pv.getTas() < 90 || pv.getTas() > 140) {
            score += 20 * (Math.abs(pv.getTas() - 115) / 25.0); // 115 is midpoint
        }

        // TAD: Normal range 60–90
        if (pv.getTad() < 60 || pv.getTad() > 90) {
            score += 20 * (Math.abs(pv.getTad() - 75) / 15.0); // 75 is midpoint
        }

        // GSC: Normal value 15
        if (pv.getGsc() != 15) {
            score += 20 * (Math.abs(pv.getGsc() - 15) / 5.0);
        }

        // GAD: Normal range 7–11
        if (pv.getGad() < 7 || pv.getGad() > 11) {
            score += 20 * (Math.abs(pv.getGad() - 9) / 2.0); // 90 is midpoint
        }

        // FR: Normal range 12–20 (assumed)
        if (pv.getFr() < 12 || pv.getFr() > 20) {
            score += 20 * (Math.abs(pv.getFr() - 16) / 4.0); // 16 is midpoint
        }

        // Cap score at 100
        return Math.min(score, 100.0);
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
    void assessRisk(ActionEvent event) {
        // Get the selected patient from the ListView
        ParametresVitaux selectedPatient = listview.getSelectionModel().getSelectedItem();

        if (selectedPatient == null) {
            // Show alert if no patient is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sélection de patient");
            alert.setHeaderText("Aucun patient sélectionné");
            alert.setContentText("Veuillez sélectionner un patient dans la liste.");
            alert.showAndWait();
            return;
        }

        // Calculate the risk score for the selected patient
        double riskScore = calculateRiskScore(selectedPatient);
        StringBuilder riskReport = new StringBuilder();

        // If the risk score is high, we show the detailed report
        if (riskScore > 70) {
            riskReport.append("Patient: ").append(selectedPatient.getName())
                    .append(", Date: ").append(selectedPatient.getCreated_at())
                    .append(", Score de Risque: ").append(String.format("%.2f", riskScore))
                    .append("\nDétails:\n");

            // Check for abnormal parameters and append to the report
            if (selectedPatient.getFc() < 60 || selectedPatient.getFc() > 100) {
                riskReport.append("- FC anormal (").append(selectedPatient.getFc()).append("): Risque cardiaque potentiel.\n");
            }
            if (selectedPatient.getTas() < 90 || selectedPatient.getTas() > 140) {
                riskReport.append("- TAS anormal (").append(selectedPatient.getTas()).append("): Risque d'hypertension/hypotension.\n");
            }
            if (selectedPatient.getTad() < 60 || selectedPatient.getTad() > 90) {
                riskReport.append("- TAD anormal (").append(selectedPatient.getTad()).append("): Risque cardiovasculaire.\n");
            }
            if (selectedPatient.getGsc() != 15) {
                riskReport.append("- GSC anormal (").append(selectedPatient.getGsc()).append("): Risque neurologique.\n");
            }
            if (selectedPatient.getGad() < 7 || selectedPatient.getGad() > 11) {
                riskReport.append("- GAD anormal (").append(selectedPatient.getGad()).append("): Risque métabolique.\n");
            }
            if (selectedPatient.getFr() < 12 || selectedPatient.getFr() > 20) {
                riskReport.append("- FR anormal (").append(selectedPatient.getFr()).append("): Risque respiratoire.\n");
            }
            riskReport.append("\n");

            // Show the alert with the high risk details
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Évaluation des Risques Sanitaires");
            alert.setHeaderText("Risque élevé détecté pour " + selectedPatient.getName());
            alert.setContentText(riskReport.toString());
            alert.showAndWait();
        } else {
            // If risk score is not high, inform the user
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Évaluation des Risques Sanitaires");
            alert.setHeaderText("Risque faible pour " + selectedPatient.getName());
            alert.setContentText("Tous les paramètres vitaux sont dans les plages normales.");
            alert.showAndWait();
        }
    }

    @FXML
    void diagButton(ActionEvent event) {
        // Get the selected patient from the ListView
        ParametresVitaux selected = listview.getSelectionModel().getSelectedItem();

        if (selected == null) {
            // Show an alert if no patient is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun patient sélectionné");
            alert.setHeaderText("Sélection requise");
            alert.setContentText("Veuillez sélectionner un patient dans la liste avant de passer au diagnostic.");
            alert.showAndWait();
            return;
        }

        try {
            // Mark the entry as processed (add to processed list)
            processedEntryIds.add(selected.getId());

            // Refresh the ListView to update the display
            listview.refresh();

            // Load the diagnostic page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Diagnostic/addDiagnostic.fxml"));
            Parent root = loader.load();

            // Navigate to the diagnostic page
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            // Handle the error and display a specific error alert
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Échec du chargement de la page de diagnostic");
            alert.setContentText("Une erreur s'est produite lors du chargement de la page de diagnostic. Veuillez réessayer.");
            alert.showAndWait();
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur inconnue");
            alert.setHeaderText("Une erreur inattendue est survenue");
            alert.setContentText("Détails de l'erreur: " + e.getMessage());
            alert.showAndWait();
        }
    }

}