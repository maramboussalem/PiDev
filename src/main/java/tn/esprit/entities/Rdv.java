package tn.esprit.entities;

import java.time.LocalDateTime;

public class Rdv {
    private int id;
    private Integer serviceNameId; // Nullable Integer for service_name_id
    private Integer dispoId; // Nullable Integer for dispo_id
    private LocalDateTime dateRdv;

    // Default constructor
    public Rdv() {
    }

    // Constructor with fields
    public Rdv(int id, Integer serviceNameId, Integer dispoId, LocalDateTime dateRdv) {
        this.id = id;
        this.serviceNameId = serviceNameId;
        this.dispoId = dispoId;
        this.dateRdv = dateRdv;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getServiceNameId() {
        return serviceNameId;
    }

    public void setServiceNameId(Integer serviceNameId) {
        this.serviceNameId = serviceNameId;
    }

    public Integer getDispoId() {
        return dispoId;
    }

    public void setDispoId(Integer dispoId) {
        this.dispoId = dispoId;
    }

    public LocalDateTime getDateRdv() {
        return dateRdv;
    }

    public void setDateRdv(LocalDateTime dateRdv) {
        this.dateRdv = dateRdv;
    }
}