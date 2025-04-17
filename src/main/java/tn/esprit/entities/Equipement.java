package tn.esprit.entities;

import java.time.LocalDate;

public class Equipement {
    private int id;
    private String description;
    private String nomEquipement;
    private int quantiteStock;
    private double prixUnitaire;
    private String etatEquipement; // "Neuf", "Abimé", "Réparé"
    private LocalDate dateAchat;
    private String img; // path de l'image ou base64

    public Equipement() {}

    public Equipement(int id, String description, String nomEquipement, int quantiteStock, double prixUnitaire,
                      String etatEquipement, LocalDate dateAchat, String img) {
        this.id = id;
        this.description = description;
        this.nomEquipement = nomEquipement;
        this.quantiteStock = quantiteStock;
        this.prixUnitaire = prixUnitaire;
        this.etatEquipement = etatEquipement;
        this.dateAchat = dateAchat;
        this.img = img;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getNomEquipement() { return nomEquipement; }
    public void setNomEquipement(String nomEquipement) { this.nomEquipement = nomEquipement; }

    public int getQuantiteStock() { return quantiteStock; }
    public void setQuantiteStock(int quantiteStock) { this.quantiteStock = quantiteStock; }

    public double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(double prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public String getEtatEquipement() { return etatEquipement; }
    public void setEtatEquipement(String etatEquipement) { this.etatEquipement = etatEquipement; }

    public LocalDate getDateAchat() { return dateAchat; }
    public void setDateAchat(LocalDate dateAchat) { this.dateAchat = dateAchat; }


    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }
}
