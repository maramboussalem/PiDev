// Ajoutez cette classe dans votre projet
package tn.esprit.utils;

import tn.esprit.entities.Medicament;
import java.util.HashMap;
import java.util.Map;

public class SessionPanier {
    private static SessionPanier instance;
    private final Map<Medicament, Integer> items = new HashMap<>();

    private SessionPanier() {}

    public static synchronized SessionPanier getInstance() {
        if (instance == null) {
            instance = new SessionPanier();
        }
        return instance;
    }

    public void ajouterAuPanier(Medicament medicament, int quantite) {
        items.merge(medicament, quantite, Integer::sum);
    }

    public void retirerDuPanier(Medicament medicament) {
        items.remove(medicament);
    }

    public void modifierQuantite(Medicament medicament, int nouvelleQuantite) {
        if (nouvelleQuantite <= 0) {
            retirerDuPanier(medicament);
        } else {
            items.put(medicament, nouvelleQuantite);
        }
    }

    public Map<Medicament, Integer> getItems() {
        return new HashMap<>(items);
    }

    public void viderPanier() {
        items.clear();
    }

    public double getTotal() {
        return items.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrix() * entry.getValue())
                .sum();
    }

    public int getNombreItems() {
        return items.values().stream().mapToInt(Integer::intValue).sum();
    }
}