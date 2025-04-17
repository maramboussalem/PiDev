package tn.esprit.services;

import tn.esprit.entities.Equipement;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipementService implements IService<Equipement> {

   // private Connection cnx;
    private Connection cnx = MyDataBase.getInstance().getMyConnection();
    public EquipementService() {

    }

    @Override
    public void ajouter(Equipement equipement) throws SQLException {
        String sql = "INSERT INTO equipement (description, nom_equipement, quantite_stock, prix_unitaire, etat_equipement, date_achat, img) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, equipement.getDescription());
            pst.setString(2, equipement.getNomEquipement());
            pst.setInt(3, equipement.getQuantiteStock());
            pst.setDouble(4, equipement.getPrixUnitaire());
            pst.setString(5, equipement.getEtatEquipement());
            pst.setDate(6, Date.valueOf(equipement.getDateAchat()));
            pst.setString(7, equipement.getImg());
            pst.executeUpdate();
            System.out.println("Équipement ajouté avec succès !");
        }
    }

    @Override
    public void modifier(Equipement e) throws SQLException {
        String req = "UPDATE equipement SET nom_equipement = ?, description = ?, quantite_stock = ?, prix_unitaire = ?, etat_equipement = ?, date_achat = ?, img = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, e.getNomEquipement());
            ps.setString(2, e.getDescription());
            ps.setInt(3, e.getQuantiteStock());
            ps.setDouble(4, e.getPrixUnitaire());
            ps.setString(5, e.getEtatEquipement());
            ps.setDate(6, Date.valueOf(e.getDateAchat()));
            ps.setString(7, e.getImg());
            ps.setInt(8, e.getId());
            ps.executeUpdate();
            System.out.println("Équipement modifié avec succès !");
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM equipement WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Équipement supprimé avec succès !");
        }
    }

    @Override
    public List<Equipement> afficher() throws SQLException {
        List<Equipement> equipements = new ArrayList<>();
        String sql = "SELECT * FROM equipement";
        try (Statement statement = cnx.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                Equipement e = new Equipement();
                e.setId(rs.getInt("id"));
                e.setDescription(rs.getString("description"));
                e.setNomEquipement(rs.getString("nom_equipement"));
                e.setQuantiteStock(rs.getInt("quantite_stock"));
                e.setPrixUnitaire(rs.getDouble("prix_unitaire"));
                e.setEtatEquipement(rs.getString("etat_equipement"));
                e.setDateAchat(rs.getDate("date_achat").toLocalDate());
                e.setImg(rs.getString("img"));
                equipements.add(e);
            }
        }
        return equipements;
    }
}
