package tn.esprit.services;

import tn.esprit.entities.Fournisseur;
import tn.esprit.entities.Utilisateur;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FournisseurService implements IService<Fournisseur>{

    private Connection connection  = MyDataBase.getInstance().getMyConnection();

    public void ajouter(Fournisseur fournisseur) {
        String query = "INSERT INTO fournisseur(nom_fournisseur, adresse, telephone, email) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, fournisseur.getNom_fournisseur());
            pst.setString(2, fournisseur.getAdresse());
            pst.setString(3, fournisseur.getTelephone());
            pst.setString(4, fournisseur.getEmail());

            pst.executeUpdate();

            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    fournisseur.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding supplier: " + e.getMessage());
            throw new RuntimeException("Error adding supplier", e);
        }
    }

    public void modifier(Fournisseur fournisseur) {
        String req = "UPDATE fournisseur SET nom_fournisseur=?, adresse=?, telephone=?, email=? WHERE id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(req)) {
            pstmt.setString(1, fournisseur.getNom_fournisseur());
            pstmt.setString(2, fournisseur.getAdresse());
            pstmt.setString(3, fournisseur.getTelephone());
            pstmt.setString(4, fournisseur.getEmail());
            pstmt.setInt(5, fournisseur.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating supplier: " + e.getMessage());
        }
    }

    public void supprimer(int id) {
        String req = "DELETE FROM fournisseur WHERE id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(req)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting supplier: " + e.getMessage());
        }
    }

    public Fournisseur findById(int id) {
        String req = "SELECT * FROM fournisseur WHERE id=?";

        try (PreparedStatement pstmt = connection.prepareStatement(req)) {
            pstmt.setInt(1, id);
            try (ResultSet res = pstmt.executeQuery()) {
                if (res.next()) {
                    return extractFournisseurFromResultSet(res);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding supplier by ID: " + e.getMessage());
        }
        return null;
    }

    public List<Fournisseur> afficher() {
        // empty list
        List<Fournisseur> fournisseurs = new ArrayList<>();
        //declarion de requete comoma variable
        String req = "SELECT * FROM fournisseur";

        try (Statement stmt = connection.createStatement();
             ResultSet res = stmt.executeQuery(req)) {

            while (res.next()) {
                fournisseurs.add(extractFournisseurFromResultSet(res));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all suppliers: " + e.getMessage());
        }
        return fournisseurs;
    }

    public List<Fournisseur> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return afficher();
        }
        return afficher().stream()
                .filter(f -> f.getNom_fournisseur().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Fournisseur> searchByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return afficher();
        }
        return afficher().stream()
                .filter(f -> f.getEmail().equalsIgnoreCase(email))
                .collect(Collectors.toList());
    }

    public List<Fournisseur> sortByName(boolean ascending) {
        return afficher().stream()
                .sorted(ascending ?
                        Comparator.comparing(Fournisseur::getNom_fournisseur) :
                        Comparator.comparing(Fournisseur::getNom_fournisseur).reversed())
                .collect(Collectors.toList());
    }

    private Fournisseur extractFournisseurFromResultSet(ResultSet res) throws SQLException {
        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setId(res.getInt("id"));
        fournisseur.setNom_fournisseur(res.getString("nom_fournisseur"));
        fournisseur.setAdresse(res.getString("adresse"));
        fournisseur.setTelephone(res.getString("telephone"));
        fournisseur.setEmail(res.getString("email"));
        return fournisseur;
    }

    // Additional useful methods
    public boolean exists(int id) {
        return findById(id) != null;
    }

    public boolean emailExists(String email) {
        return afficher().stream()
                .anyMatch(f -> f.getEmail().equalsIgnoreCase(email));
    }
}