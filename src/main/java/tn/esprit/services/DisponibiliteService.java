package tn.esprit.services;

import tn.esprit.entities.Disponibilite;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DisponibiliteService {

    private Connection cnx = MyDataBase.getInstance().getMyConnection();

    public DisponibiliteService() {

    }

    public void ajouter(Disponibilite dispo) throws SQLException {
        String sql = "INSERT INTO disponibilite (date_heure, est_reserve, service_med_id) VALUES (?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setTimestamp(1, Timestamp.valueOf(dispo.getDateHeure()));
            pst.setBoolean(2, dispo.isEstReserve());
            pst.setInt(3, dispo.getServiceMedId());
            pst.executeUpdate();
        }
    }

    public List<Disponibilite> getByServiceId(int serviceId) throws SQLException {
        List<Disponibilite> list = new ArrayList<>();
        String sql = "SELECT * FROM disponibilite WHERE service_med_id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, serviceId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Disponibilite d = new Disponibilite();
                d.setId(rs.getInt("id"));
                d.setDateHeure(rs.getTimestamp("date_heure").toLocalDateTime());
                d.setEstReserve(rs.getBoolean("est_reserve"));
                d.setServiceMedId(rs.getInt("service_med_id"));
                list.add(d);
            }
        }
        return list;
    }
}
