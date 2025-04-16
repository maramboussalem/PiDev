package tn.esprit.entities;
import java.sql.Date;

public class Diagnostic {
    private int id;
    private String name;
    private String description;
    private Date date_diagnostic ;
    private int patient_id;
    private int medecin_id;

    public Diagnostic() {}

    public Diagnostic(int id, String name, String description, Date date_diagnostic, int patient_id, int medecin_id) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date_diagnostic = date_diagnostic;
        this.patient_id = patient_id;
        this.medecin_id = medecin_id;
    }

    public Diagnostic(String name, String description, Date date_diagnostic, int patient_id, int medecin_id) {
        this.name = name;
        this.description = description;
        this.date_diagnostic = date_diagnostic;
        this.patient_id = patient_id;
        this.medecin_id = medecin_id;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate_diagnostic() {
        return date_diagnostic;
    }

    public void setDate_diagnostic(Date date_diagnostic) {
        this.date_diagnostic = date_diagnostic;
    }

    public int getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }

    public int getMedecin_id() {
        return medecin_id;
    }

    public void setMedecin_id(int medecin_id) {
        this.medecin_id = medecin_id;
    }

    @Override
    public String toString() {
        return String.format(
                "ID: %d | Nom: %s | Description: %s | Date: %s",
                id,
                name,
                description,
                (date_diagnostic != null ? date_diagnostic.toString() : "N/A")
        );
    }

}
