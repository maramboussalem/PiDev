package tn.esprit.services;

import tn.esprit.entities.Rdv;
import tn.esprit.entities.ServiceMed;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RdvService implements IService<Rdv> {

    private final Connection cnx;

    public RdvService() {
        this.cnx = MyDataBase.getInstance().getMyConnection();
        if (cnx == null) {
            System.err.println("Erreur: Connexion à la base de données échouée dans RdvService !");
        }
    }

    @Override
    public void ajouter(Rdv rdv) throws SQLException {
        String query = "INSERT INTO rdv (service_name_id, dispo_id, date_rdv) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            if (rdv.getServiceNameId() != null) {
                pstmt.setInt(1, rdv.getServiceNameId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            if (rdv.getDispoId() != null) {
                pstmt.setInt(2, rdv.getDispoId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            pstmt.setTimestamp(3, Timestamp.valueOf(rdv.getDateRdv()));

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Rendez-vous ajouté avec succès !");
            } else {
                System.err.println("Erreur: Le rendez-vous n’a pas été ajouté.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de l'ajout du rendez-vous: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Rdv> afficher() throws SQLException {
        List<Rdv> rdvs = new ArrayList<>();
        String query = "SELECT * FROM rdv";

        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Rdv rdv = new Rdv();
                rdv.setId(rs.getInt("id"));
                rdv.setServiceNameId(rs.getObject("service_name_id") != null ? rs.getInt("service_name_id") : null); // Fixed column name
                rdv.setDispoId(rs.getObject("dispo_id") != null ? rs.getInt("dispo_id") : null);
                rdv.setDateRdv(rs.getTimestamp("date_rdv").toLocalDateTime());
                rdvs.add(rdv);
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la récupération des rendez-vous: " + e.getMessage());
            throw e;
        }
        return rdvs;
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM rdv WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Rendez-vous supprimé avec succès !");
            } else {
                System.err.println("Erreur: Rendez-vous non trouvé.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la suppression du rendez-vous: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void modifier(Rdv rdv) throws SQLException {
        String query = "UPDATE rdv SET service_name_id = ?, dispo_id = ?, date_rdv = ? WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(query)) {
            if (rdv.getServiceNameId() != null) {
                pstmt.setInt(1, rdv.getServiceNameId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            if (rdv.getDispoId() != null) {
                pstmt.setInt(2, rdv.getDispoId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setTimestamp(3, Timestamp.valueOf(rdv.getDateRdv()));
            pstmt.setInt(4, rdv.getId());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Rendez-vous mis à jour avec succès !");
            } else {
                System.err.println("Erreur: Rendez-vous non trouvé.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la mise à jour du rendez-vous: " + e.getMessage());
            throw e;
        }
    }

    public List<ServiceMed> getServices() throws SQLException {
        List<ServiceMed> services = new ArrayList<>();
        String query = "SELECT id, nom_service, description_med, image_m FROM service_med";
        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                ServiceMed service = new ServiceMed();
                service.setId(rs.getInt("id"));
                service.setNomService(rs.getString("nom_service"));
                service.setDescriptionMed(rs.getString("description_med"));
                service.setImageM(rs.getString("image_m"));
                services.add(service);
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la récupération des services: " + e.getMessage());
            throw e;
        }
        return services;
    }

    public Map<Integer, String> getServiceNameMap() throws SQLException {
        Map<Integer, String> serviceNameMap = new HashMap<>();
        String query = "SELECT id, nom_service FROM service_med";
        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                serviceNameMap.put(rs.getInt("id"), rs.getString("nom_service"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la récupération des noms de services: " + e.getMessage());
            throw e;
        }
        return serviceNameMap;
    }
}