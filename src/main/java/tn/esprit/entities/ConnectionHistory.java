package tn.esprit.entities;

import java.sql.Timestamp;

public class ConnectionHistory {
    private int id;
    private int utilisateurId;
    private Timestamp timestamp;
    private String eventType;

    public ConnectionHistory() {
    }

    public ConnectionHistory(int utilisateurId, Timestamp timestamp, String eventType) {
        this.utilisateurId = utilisateurId;
        this.timestamp = timestamp;
        this.eventType = eventType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(int utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "ConnectionHistory{" +
                "id=" + id +
                ", utilisateurId=" + utilisateurId +
                ", timestamp=" + timestamp +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}