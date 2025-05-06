package tn.esprit.entities;

import java.time.LocalDateTime;

public class Reclamation {
    private int id;
    private Integer consultationId; // Nullable foreign key
    private String utilisateurId;
    private String sujet;
    private String description;
    private String statut;
    private LocalDateTime dateCreation;
    private String reponse;

    // Constructors
    public Reclamation() {}

    public Reclamation(Integer consultationId, String utilisateurId, String sujet, String description, String statut, LocalDateTime dateCreation, String reponse) {
        this.consultationId = consultationId;
        this.utilisateurId = utilisateurId;
        this.sujet = sujet;
        this.description = description;
        this.statut = statut;
        this.dateCreation = dateCreation;
        this.reponse = reponse;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getConsultationId() {
        return consultationId;
    }

    public void setConsultationId(Integer consultationId) {
        this.consultationId = consultationId;
    }

    public String getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(String utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    @Override
    public String toString() {
        return "Reclamation{" +
                "id=" + id +
                ", consultationId=" + consultationId +
                ", utilisateurId='" + utilisateurId + '\'' +
                ", sujet='" + sujet + '\'' +
                ", description='" + description + '\'' +
                ", statut='" + statut + '\'' +
                ", dateCreation=" + dateCreation +
                ", reponse='" + reponse + '\'' +
                '}';
    }
}
