package tn.esprit.services;

import tn.esprit.entities.Utilisateur;
import tn.esprit.utils.MyDataBase;
import org.json.JSONArray;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceUtilisateur implements IService< Utilisateur>{

    private Connection connection = MyDataBase.getInstance().getMyConnection();

    public ServiceUtilisateur() {
    }
    public void ajouter(Utilisateur utilisateur) throws SQLException {
        String sql = "INSERT INTO `utilisateur`(`nom`, `prenom`, `email`, `sexe`, `adresse`, `telephone`, `role`, `roles`, `antecedents_medicaux`, `specialite`, `hopital`, `motdepasse`, `verification_code`, `captcha`, `img_url`, `activation_token`, `is_active`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = connection.prepareStatement(sql);

        String hashedPassword = BCrypt.hashpw(utilisateur.getMotdepasse(), BCrypt.gensalt());

        // Convertir la liste de rôles en format JSON
        JSONArray rolesJson = new JSONArray(utilisateur.getRoles());

        // Vérification du rôle
        String antecedents_medicaux = utilisateur.getAntecedents_medicaux();
        String specialite = utilisateur.getSpecialite();
        String hopital = utilisateur.getHopital();

        if ("medecin".equals(utilisateur.getRole())) {
            antecedents_medicaux = null;
        } else if ("patient".equals(utilisateur.getRole())) {
            specialite = null;
            hopital = null;
        }
        pst.setString(1, utilisateur.getNom());
        pst.setString(2, utilisateur.getPrenom());
        pst.setString(3, utilisateur.getEmail());
        pst.setString(4, utilisateur.getSexe());
        pst.setString(5, utilisateur.getAdresse());
        pst.setString(6, utilisateur.getTelephone());
        pst.setString(7, utilisateur.getRole()); // Stocke "patient"
        pst.setString(8, rolesJson.toString()); // Stocke '["patient"]'
        pst.setString(9, antecedents_medicaux);
        pst.setString(10, specialite);
        pst.setString(11, hopital);
        pst.setString(12, hashedPassword);
        pst.setString(13, utilisateur.getVerification_code());
        pst.setString(14, utilisateur.getCaptcha());
        pst.setString(15, utilisateur.getImg_url());
        pst.setString(16, utilisateur.getActivation_token());
        pst.setBoolean(17, utilisateur.isIs_active());
        System.out.println("Ajout dans la BD, img_url = " + utilisateur.getImg_url());
        pst.executeUpdate();
    }

    public void modifier(Utilisateur utilisateur) throws SQLException {
        String sql = "UPDATE `utilisateur` SET `nom`=?, `prenom`=?, `email`=?, `sexe`=?, `adresse`=?, `telephone`=?, `role`=?, `roles`=?, `antecedents_medicaux`=?, `specialite`=?, `hopital`=?, `motdepasse`=?, `verification_code`=?, `captcha`=?, `img_url`=?, `activation_token`=?, `is_active`=? WHERE `id`=?";
        PreparedStatement pst = connection.prepareStatement(sql);

        // Convertir la liste des rôles en format JSON
        JSONArray rolesJson = new JSONArray(utilisateur.getRoles());

        // Vérification du rôle pour les champs spécifiques
        String antecedents_medicaux = utilisateur.getAntecedents_medicaux();
        String specialite = utilisateur.getSpecialite();
        String hopital = utilisateur.getHopital();

        if ("medecin".equals(utilisateur.getRole())) {
            antecedents_medicaux = null;
        } else if ("patient".equals(utilisateur.getRole())) {
            specialite = null;
            hopital = null;
        }

        pst.setString(1, utilisateur.getNom());
        pst.setString(2, utilisateur.getPrenom());
        pst.setString(3, utilisateur.getEmail());
        pst.setString(4, utilisateur.getSexe());
        pst.setString(5, utilisateur.getAdresse());
        pst.setString(6, utilisateur.getTelephone());
        pst.setString(7, utilisateur.getRole());
        pst.setString(8, rolesJson.toString());
        pst.setString(9, antecedents_medicaux);
        pst.setString(10, specialite);
        pst.setString(11, hopital);
        pst.setString(12, utilisateur.getMotdepasse()); // On utilise directement le mot de passe existant
        pst.setString(13, utilisateur.getVerification_code());
        pst.setString(14, utilisateur.getCaptcha());
        pst.setString(15, utilisateur.getImg_url());
        pst.setString(16, utilisateur.getActivation_token());
        pst.setBoolean(17, utilisateur.isIs_active());
        pst.setInt(18, utilisateur.getId());

        pst.executeUpdate();
    }

    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM `utilisateur` WHERE `id`=?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, id);
        pst.executeUpdate();
    }

    public List<Utilisateur> afficher() throws SQLException {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM `utilisateur`";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setId(rs.getInt("id"));
            utilisateur.setNom(rs.getString("nom"));
            utilisateur.setPrenom(rs.getString("prenom"));
            utilisateur.setEmail(rs.getString("email"));
            utilisateur.setSexe(rs.getString("sexe"));
            utilisateur.setAdresse(rs.getString("adresse"));
            utilisateur.setTelephone(rs.getString("telephone"));
            utilisateur.setRole(rs.getString("role"));
            utilisateur.setAntecedents_medicaux(rs.getString("antecedents_medicaux"));
            utilisateur.setSpecialite(rs.getString("specialite"));
            utilisateur.setHopital(rs.getString("hopital"));
            utilisateur.setMotdepasse(rs.getString("motdepasse"));
            utilisateur.setVerification_code(rs.getString("verification_code"));
            utilisateur.setCaptcha(rs.getString("captcha"));
            utilisateur.setImg_url(rs.getString("img_url"));
            utilisateur.setActivation_token(rs.getString("activation_token"));
            utilisateur.setIs_active(rs.getBoolean("is_active"));

            // Convertir le champ `roles` en liste de rôles
            String rolesString = rs.getString("roles");
            if (rolesString != null && !rolesString.isEmpty()) {
                JSONArray rolesArray = new JSONArray(rolesString); // Convertir JSON string en tableau
                List<String> rolesList = new ArrayList<>();
                for (int i = 0; i < rolesArray.length(); i++) {
                    rolesList.add(rolesArray.getString(i)); // Ajoute chaque rôle dans la liste
                }
                utilisateur.setRoles(rolesList);
            } else {
                utilisateur.setRoles(new ArrayList<>()); // Aucun rôle
            }

            utilisateurs.add(utilisateur);
        }
        return utilisateurs;
    }
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM utilisateur WHERE email = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, email);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0; // Si le nombre d'entrées est > 0, l'email existe
        }
        return false;
    }

    public Utilisateur login(String email, String motDePasse) throws SQLException {
        String sql = "SELECT * FROM utilisateur WHERE email = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, email);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            String hashedPassword = rs.getString("motdepasse");

            if (BCrypt.checkpw(motDePasse, hashedPassword)) {
                Utilisateur utilisateur = new Utilisateur();
                utilisateur.setId(rs.getInt("id"));
                utilisateur.setNom(rs.getString("nom"));
                utilisateur.setPrenom(rs.getString("prenom"));
                utilisateur.setEmail(rs.getString("email"));
                utilisateur.setRole(rs.getString("role"));
                utilisateur.setIs_active(rs.getBoolean("is_active"));
                utilisateur.setSexe(rs.getString("sexe"));
                utilisateur.setAdresse(rs.getString("adresse"));
                utilisateur.setTelephone(rs.getString("telephone"));
                utilisateur.setAntecedents_medicaux(rs.getString("antecedents_medicaux"));
                utilisateur.setSpecialite(rs.getString("specialite"));
                utilisateur.setHopital(rs.getString("hopital"));
                utilisateur.setImg_url(rs.getString("img_url"));
                utilisateur.setMotdepasse(rs.getString("motdepasse"));

                // Vérifier si l'utilisateur est activé
                if (!utilisateur.isIs_active()) {
                    throw new SQLException("Votre compte n'est pas activé.");
                }
                return utilisateur; // Retourne l'utilisateur pour gérer la redirection
            } else {
                throw new SQLException("Mot de passe incorrect.");
            }
        } else {
            throw new SQLException("Email non trouvé.");
        }
    }
    // Dans ServiceUtilisateur.java
    public Utilisateur getUserById(int id) throws SQLException {
        String sql = "SELECT * FROM utilisateur WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setId(rs.getInt("id"));
            utilisateur.setNom(rs.getString("nom"));
            utilisateur.setPrenom(rs.getString("prenom"));
            utilisateur.setEmail(rs.getString("email"));
            utilisateur.setSexe(rs.getString("sexe"));
            utilisateur.setAdresse(rs.getString("adresse"));
            utilisateur.setTelephone(rs.getString("telephone"));
            utilisateur.setRole(rs.getString("role"));
            utilisateur.setAntecedents_medicaux(rs.getString("antecedents_medicaux"));
            utilisateur.setSpecialite(rs.getString("specialite"));
            utilisateur.setHopital(rs.getString("hopital"));
            utilisateur.setMotdepasse(rs.getString("motdepasse"));
            utilisateur.setIs_active(rs.getBoolean("is_active"));
            utilisateur.setImg_url(rs.getString("img_url"));
            // Gérer les roles si nécessaire
            String rolesString = rs.getString("roles");
            if (rolesString != null && !rolesString.isEmpty()) {
                JSONArray rolesArray = new JSONArray(rolesString);
                List<String> rolesList = new ArrayList<>();
                for (int i = 0; i < rolesArray.length(); i++) {
                    rolesList.add(rolesArray.getString(i));
                }
                utilisateur.setRoles(rolesList);
            }
            return utilisateur;
        }
        return null;
    }

    public void toggleAccountStatus(int id) throws SQLException {
        // Récupérer l'utilisateur pour vérifier son état actuel
        Utilisateur utilisateur = getUserById(id);
        if (utilisateur != null) {
            // Inverser l'état actuel
            boolean newStatus = !utilisateur.isIs_active();

            String sql = "UPDATE `utilisateur` SET `is_active`=? WHERE `id`=?";
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setBoolean(1, newStatus);
            pst.setInt(2, id);
            pst.executeUpdate();
        } else {
            throw new SQLException("Utilisateur non trouvé avec l'ID : " + id);
        }
    }
}
