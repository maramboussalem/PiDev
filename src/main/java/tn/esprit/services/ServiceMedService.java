package tn.esprit.services;

import tn.esprit.entities.Disponibilite;
import tn.esprit.entities.ServiceMed;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceMedService {
    private Connection cnx = MyDataBase.getInstance().getMyConnection();

    public ServiceMedService() {

    }

    public void ajouter(ServiceMed serviceMed) throws SQLException {
        String sql = "INSERT INTO service_med (nom_service, description_med, image_m) VALUES (?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, serviceMed.getNomService());
            ps.setString(2, serviceMed.getDescriptionMed());
            ps.setString(3, serviceMed.getImageM());
            ps.executeUpdate();
            System.out.println("Service médical ajouté avec succès !");
        }
    }

    public List<ServiceMed> afficher() throws SQLException {
        List<ServiceMed> list = new ArrayList<>();
        String sql = "SELECT * FROM service_med";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);
        DisponibiliteService dispoService = new DisponibiliteService();

        while (rs.next()) {
            ServiceMed s = new ServiceMed(
                    rs.getInt("id"),
                    rs.getString("nom_service"),
                    rs.getString("description_med"),
                    rs.getString("image_m")
            );

            List<Disponibilite> disponibilites = dispoService.getByServiceId(s.getId());
            s.setDisponibilites(disponibilites);

            list.add(s);
        }
        return list;
    }
    public void modifier(ServiceMed s) throws SQLException {
        String sql = "UPDATE service_med SET nom_service = ?, description_med = ?, image_m = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, s.getNomService());
            ps.setString(2, s.getDescriptionMed());
            ps.setString(3, s.getImageM());
            ps.setInt(4, s.getId());
            ps.executeUpdate();
            System.out.println("Service médical modifié avec succès !");
        }
    }

    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM service_med WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }
}
