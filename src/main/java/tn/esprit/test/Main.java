package tn.esprit.test;

import tn.esprit.entities.Utilisateur;  // ✅ Import de la classe Utilisateur
import tn.esprit.services.ServiceUtilisateur;  // ✅ Import du service utilisateur
import tn.esprit.utils.MyDataBase;
import java.util.Arrays;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ServiceUtilisateur serviceUtilisateur = new ServiceUtilisateur();

        try {
            // ✅ Ajouter un utilisateur
            Utilisateur newUser = new Utilisateur();
            newUser.setNom("Dupont");
            newUser.setPrenom("Jean");
            newUser.setEmail("jean.dupont@example.com");
            newUser.setSexe("M");
            newUser.setAdresse("123 Rue de Paris");
            newUser.setTelephone("0123456789");
            newUser.setRole("patient");  // ✅ Role unique
            newUser.setRoles(Arrays.asList("patient"));  // ✅ Liste des rôles au format JSON
            newUser.setAntecedents_medicaux("Aucun");
            newUser.setSpecialite(null);
            newUser.setHopital(null);
            newUser.setMotdepasse("password123");
            newUser.setVerification_code("123456");
            newUser.setCaptcha("captcha_code");
            newUser.setImg_url("https://example.com/image.jpg");
            newUser.setActivation_token("token123");
            newUser.setIs_active(true);

            serviceUtilisateur.ajouter(newUser);
            System.out.println("✅ Utilisateur ajouté avec succès !");

            // ✅ Modifier un utilisateur
            newUser.setNom("Martin");  // Modification du nom
            serviceUtilisateur.modifier(newUser);
            System.out.println("✅ Utilisateur modifié avec succès !");

            // ✅ Afficher tous les utilisateurs
            List<Utilisateur> utilisateurs = serviceUtilisateur.afficher();
            System.out.println("✅ Liste des utilisateurs :");
            for (Utilisateur u : utilisateurs) {
                System.out.println(u);
            }

            // ✅ Supprimer un utilisateur
           /* if (!utilisateurs.isEmpty()) {
                int idToDelete = utilisateurs.get(0).getId();
                serviceUtilisateur.supprimer(idToDelete);
                System.out.println("✅ Utilisateur supprimé avec succès !");
            }*/

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
