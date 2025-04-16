package tn.esprit.entities;

import java.sql.Date;

public class Medicament {

    private int id;
    private int fournisseurId;
    private String nom;
    private String description;
    private int quantite;
    private double prix;
    private String type;
    private Date expireAt;
    private String image;
    private boolean isShown;


    public Medicament() {}


    public Medicament(int id, int fournisseurId, String nom, String description, int quantite, double prix, String type, Date expireAt, String image, boolean isShown) {
        this.id = id;
        this.fournisseurId = fournisseurId;
        this.nom = nom;
        this.description = description;
        this.quantite = quantite;
        this.prix = prix;
        this.type = type;
        this.expireAt = expireAt;
        this.image = image;
        this.isShown = isShown;
    }

    @Override
    public String toString() {
        return "Medicament{" +
                "id=" + id +
                ", fournisseurId=" + fournisseurId +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", quantite=" + quantite +
                ", prix=" + prix +
                ", type='" + type + '\'' +
                ", expireAt=" + expireAt +
                ", image='" + image + '\'' +
                ", isShown=" + isShown +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFournisseurId() {
        return fournisseurId;
    }

    public void setFournisseurId(int fournisseurId) {
        this.fournisseurId = fournisseurId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Date expireAt) {
        this.expireAt = expireAt;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isShown() {
        return isShown;
    }

    public void setShown(boolean shown) {
        isShown = shown;
    }
}
