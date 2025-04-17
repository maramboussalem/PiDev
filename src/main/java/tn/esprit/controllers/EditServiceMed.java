package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import tn.esprit.entities.ServiceMed;
import tn.esprit.services.ServiceMedService;

public class EditServiceMed {

    @FXML
    private TextField tfNomService;

    @FXML
    private TextField tfDescriptionMed;

    @FXML
    private TextField tfImageM;

    private final ServiceMedService service = new ServiceMedService();
    private ServiceMed currentService;

    public void setServiceMed(ServiceMed serviceMed) {
        this.currentService = serviceMed;
        tfNomService.setText(serviceMed.getNomService());
        tfDescriptionMed.setText(serviceMed.getDescriptionMed());
        tfImageM.setText(serviceMed.getImageM());
    }

    @FXML
    private void handleModifierServiceMed() {
        if (currentService == null) return;

        String nom = tfNomService.getText();
        String description = tfDescriptionMed.getText();
        String image = tfImageM.getText();

        if (nom.isEmpty() || description.isEmpty() || image.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs.").show();
            return;
        }

        currentService.setNomService(nom);
        currentService.setDescriptionMed(description);
        currentService.setImageM(image);

        try {
            service.modifier(currentService);
            new Alert(Alert.AlertType.INFORMATION, "Service modifié avec succès !").show();
            tfNomService.getScene().getWindow().hide();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur lors de la modification : " + e.getMessage()).show();
        }
    }
}
