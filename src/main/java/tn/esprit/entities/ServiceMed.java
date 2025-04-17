package tn.esprit.entities;

import java.util.ArrayList;
import java.util.List;

public class ServiceMed {
    private int id;
    private String nomService;
    private String descriptionMed;
    private String imageM;
    private List<Disponibilite> disponibilites = new ArrayList<>();

    public ServiceMed() {}

    public ServiceMed(int id, String nomService, String descriptionMed, String imageM) {
        this.id = id;
        this.nomService = nomService;
        this.descriptionMed = descriptionMed;
        this.imageM = imageM;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNomService() { return nomService; }
    public void setNomService(String nomService) { this.nomService = nomService; }

    public String getDescriptionMed() { return descriptionMed; }
    public void setDescriptionMed(String descriptionMed) { this.descriptionMed = descriptionMed; }

    public String getImageM() { return imageM; }
    public void setImageM(String imageM) { this.imageM = imageM; }

    public List<Disponibilite> getDisponibilites() { return disponibilites; }
    public void setDisponibilites(List<Disponibilite> disponibilites) { this.disponibilites = disponibilites; }

    @Override
    public String toString() {
        return nomService;
    }
}
