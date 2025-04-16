package tn.esprit.services;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import tn.esprit.entities.Fournisseur;
import tn.esprit.entities.Medicament;
import tn.esprit.utils.MyDataBase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
 import java.util.List;
 import java.util.stream.Collectors;

public class MedicamentService implements IService<Medicament> {

        private Connection connection = MyDataBase.getInstance().getMyConnection();

    private static final String IMAGE_UPLOAD_DIR = "src/main/resources/images/medicaments/";
    public List<Medicament> getMedicamentsByFournisseur(int fournisseurId) {
        List<Medicament> medicaments = new ArrayList<>();
        String query = "SELECT * FROM medicament WHERE fournisseur_id = ? ";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, fournisseurId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Medicament m = new Medicament();
                m.setId(rs.getInt("id"));
                m.setFournisseurId(rs.getInt("fournisseur_id"));
                m.setNom(rs.getString("nom_medicament"));
                m.setDescription(rs.getString("description"));
                m.setQuantite(rs.getInt("quantite"));
                m.setPrix(rs.getDouble("prix"));
                m.setType(rs.getString("type"));
                m.setExpireAt(rs.getDate("expireAt"));
                m.setImage(rs.getString("image"));
                m.setShown(rs.getBoolean("isShown"));

                medicaments.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des médicaments: " + e.getMessage());
        }

        return medicaments;
    }
    @Override
    public void ajouter(Medicament medicament) {
        String query = "INSERT INTO medicament(nom_medicament, description, quantite, prix, type, expireat, image, fournisseur_id , isshown) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?,?, ?)";

        try {
            // Handle image
            String imagePath = handleImage(medicament.getImage());

            try (PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                pst.setString(1, medicament.getNom());
                pst.setString(2, medicament.getDescription());
                pst.setInt(3, medicament.getQuantite());
                pst.setDouble(4, medicament.getPrix());
                pst.setString(5, medicament.getType());
                pst.setDate(6, new java.sql.Date(medicament.getExpireAt().getTime()));
                pst.setString(7, imagePath);
                pst.setInt(8, medicament.getFournisseurId());
                pst.setInt(9, 1);

                pst.executeUpdate();

                try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        medicament.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException | IOException e) {
            System.err.println("Error creating medication: " + e.getMessage());
            throw new RuntimeException("Error creating medication", e);
        }
    }

    private String handleImage(String imageName) throws IOException {
        // If no image provided, use default
        if (imageName == null || imageName.isEmpty()) {
            return "default-medicament.png";
        }

        // Create upload directory if it doesn't exist
        Path uploadDir = Paths.get(IMAGE_UPLOAD_DIR);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Check if image already exists in target directory
        Path targetPath = uploadDir.resolve(imageName);
        if (!Files.exists(targetPath)) {
            // If this is a new image (shouldn't happen with current flow)
            Files.copy(Paths.get(IMAGE_UPLOAD_DIR + imageName), targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        return imageName;
    }

    @Override
    public void modifier(Medicament medicament) {
        String req = "UPDATE medicament SET fournisseur_id=?, nom_medicament=?, description=?, quantite=?, prix=?, type=?, expireat=?, image=?, isshown=? WHERE id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(req)) {
            setMedicamentParameters(pstmt, medicament);
            pstmt.setInt(10, medicament.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating medication: " + e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) {
        String req = "DELETE FROM medicament WHERE id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(req)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting medication: " + e.getMessage());
        }
    }



    @Override
    public List<Medicament> afficher() {
        List<Medicament> medicaments = new ArrayList<>();
        String req = "SELECT * FROM medicament";

        try (Statement stmt = connection.createStatement();
             ResultSet res = stmt.executeQuery(req)) {

            while (res.next()) {
                medicaments.add(extractMedicamentFromResultSet(res));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all medications: " + e.getMessage());
        }
        return medicaments;
    }



    private void setMedicamentParameters(PreparedStatement pstmt, Medicament medicament) throws SQLException {
        pstmt.setInt(1, medicament.getFournisseurId());
        pstmt.setString(2, medicament.getNom());
        pstmt.setString(3, medicament.getDescription());
        pstmt.setInt(4, medicament.getQuantite());
        pstmt.setDouble(5, medicament.getPrix());
        pstmt.setString(6, medicament.getType());
        pstmt.setDate(7, new java.sql.Date(medicament.getExpireAt().getTime()));
        pstmt.setString(8, medicament.getImage());
        pstmt.setBoolean(9, medicament.isShown());
    }

    private Medicament extractMedicamentFromResultSet(ResultSet res) throws SQLException {
        Medicament medicament = new Medicament();
        medicament.setId(res.getInt("id"));
        medicament.setFournisseurId(res.getInt("fournisseur_id"));
        medicament.setNom(res.getString("nom_medicament"));
        medicament.setDescription(res.getString("description"));
        medicament.setQuantite(res.getInt("quantite"));
        medicament.setPrix(res.getDouble("prix"));
        medicament.setType(res.getString("type"));
        medicament.setExpireAt(res.getDate("expireat"));
        medicament.setImage(res.getString("image"));
        medicament.setShown(res.getBoolean("isshown"));
        return medicament;
    }
    public List<Medicament> sortByName(boolean ascending) {
        return afficher().stream()
                .sorted(ascending ?
                        Comparator.comparing(Medicament::getNom) :
                        Comparator.comparing(Medicament::getNom).reversed())
                .collect(Collectors.toList());
    }





}