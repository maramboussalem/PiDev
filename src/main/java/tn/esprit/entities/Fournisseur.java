package tn.esprit.entities;

public class Fournisseur {
    private int id;
    private String nom_fournisseur;
    private String adresse;
    private String telephone;
    private String email;

    public Fournisseur() {
        // Default constructor
    }

    public Fournisseur(int id, String nom_fournisseur, String adresse, String telephone, String email) {
        this.setId(id);
        this.setNom_fournisseur(nom_fournisseur);
        this.setAdresse(adresse);
        this.setTelephone(telephone);
        this.setEmail(email);
    }

    public Fournisseur(String nom_fournisseur, String adresse, String telephone, String email) {
        this.setNom_fournisseur(nom_fournisseur);
        this.setAdresse(adresse);
        this.setTelephone(telephone);
        this.setEmail(email);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("L'ID ne peut pas être négatif");
        }
        this.id = id;
    }

    @Override
    public String toString() {
        return
                  nom_fournisseur
               ;
    }

    public String getNom_fournisseur() {
        return nom_fournisseur;
    }

    public void setNom_fournisseur(String nom_fournisseur) {
        if (nom_fournisseur == null || nom_fournisseur.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du fournisseur ne peut pas être vide");
        }
        if (nom_fournisseur.length() > 100) {
            throw new IllegalArgumentException("Le nom du fournisseur ne peut pas dépasser 100 caractères");
        }
        this.nom_fournisseur = nom_fournisseur;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        if (adresse == null || adresse.trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse ne peut pas être vide");
        }
        if (adresse.length() > 200) {
            throw new IllegalArgumentException("L'adresse ne peut pas dépasser 200 caractères");
        }
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        if (telephone == null || telephone.trim().isEmpty()) {
            throw new IllegalArgumentException("Le téléphone ne peut pas être vide");
        }
        // Vérification du format du numéro de téléphone (exemple simple)
        if (!telephone.matches("[0-9]+")) {
            throw new IllegalArgumentException("Le téléphone ne doit contenir que des chiffres");
        }
        if (telephone.length() > 15) {
            throw new IllegalArgumentException("Le téléphone ne peut pas dépasser 15 chiffres");
        }
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide");
        }
        // Vérification simple du format de l'email
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Format d'email invalide");
        }
        if (email.length() > 100) {
            throw new IllegalArgumentException("L'email ne peut pas dépasser 100 caractères");
        }
        this.email = email;
    }
}