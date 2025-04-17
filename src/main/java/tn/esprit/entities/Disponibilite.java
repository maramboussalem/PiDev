package tn.esprit.entities;

import java.time.LocalDateTime;

public class Disponibilite {
    private int id;
    private LocalDateTime dateHeure;
    private boolean estReserve;
    private int serviceMedId;

    public Disponibilite() {}

    public Disponibilite(int id, LocalDateTime dateHeure, boolean estReserve, int serviceMedId) {
        this.id = id;
        this.dateHeure = dateHeure;
        this.estReserve = estReserve;
        this.serviceMedId = serviceMedId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }

    public boolean isEstReserve() { return estReserve; }
    public void setEstReserve(boolean estReserve) { this.estReserve = estReserve; }

    public int getServiceMedId() { return serviceMedId; }
    public void setServiceMedId(int serviceMedId) { this.serviceMedId = serviceMedId; }

    @Override
    public String toString() {
        return dateHeure + (estReserve ? " (Réservé)" : " (Libre)");
    }
}
