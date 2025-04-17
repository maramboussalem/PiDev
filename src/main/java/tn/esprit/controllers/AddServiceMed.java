package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import tn.esprit.entities.ServiceMed;
import tn.esprit.services.ServiceMedService;

public class AddServiceMed {

    @FXML
    private TextField tfNomService;

    @FXML
    private TextField tfDescriptionMed;

    @FXML
    private TextField tfImageM;

    private final ServiceMedService service = new ServiceMedService();

    @FXML
    public void handleAjouterServiceMed() {
        String nom = tfNomService.getText();
        String description = tfDescriptionMed.getText();
        String image = tfImageM.getText();

        if (nom.isEmpty() || description.isEmpty() || image.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs.").show();
            return;
        }

        ServiceMed newService = new ServiceMed();
        newService.setNomService(nom);
        newService.setDescriptionMed(description);
        newService.setImageM(image);

        try {
            service.ajouter(newService);
            new Alert(Alert.AlertType.INFORMATION, "Service médical ajouté avec succès !").show();
            tfNomService.clear();
            tfDescriptionMed.clear();
            tfImageM.clear();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ajout : " + e.getMessage()).show();
        }
    }
}
