package tn.esprit.entities;

import java.sql.Date;

public class Consultation {

    private int id;
    private String nomPatient;
    private Date dateConsultation;
    private String diagnostic;
    private int userId;

    // Constructor with all fields (including userId)
    public Consultation(int id, String nomPatient, Date dateConsultation, String diagnostic, int userId) {
        this.id = id;
        this.nomPatient = nomPatient;
        this.dateConsultation = dateConsultation;
        this.diagnostic = diagnostic;
        this.userId = userId;
    }

    // Constructor without id (used when creating a new Consultation)
    public Consultation(String nomPatient, Date dateConsultation, String diagnostic, int userId) {
        this.nomPatient = nomPatient;
        this.dateConsultation = dateConsultation;
        this.diagnostic = diagnostic;
        this.userId = userId;
    }

    // Empty constructor
    public Consultation() {}

    @Override
    public String toString() {
        return "Consultation{" +
                "id=" + id +
                ", nomPatient='" + nomPatient + '\'' +
                ", dateConsultation=" + dateConsultation +
                ", diagnostic='" + diagnostic + '\'' +
                ", userId=" + userId +
                '}';
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomPatient() {
        return nomPatient;
    }

    public void setNomPatient(String nomPatient) {
        this.nomPatient = nomPatient;
    }

    public Date getDateConsultation() {
        return dateConsultation;
    }

    public void setDateConsultation(Date dateConsultation) {
        this.dateConsultation = dateConsultation;
    }

    public String getDiagnostic() {
        return diagnostic;
    }

    public void setDiagnostic(String diagnostic) {
        this.diagnostic = diagnostic;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
