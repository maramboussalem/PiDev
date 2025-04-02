package tn.esprit.entities;
import java.util.List;

public class Utilisateur {
    private int id;
    private String nom ,prenom ,email ,sexe ,adresse,telephone ,role ,antecedents_medicaux ,specialite ,hopital ,motdepasse ,verification_code,captcha,img_url ,activation_token;
    private boolean is_active ;
    private List<String> roles;

    public Utilisateur() {
    }

    public Utilisateur(int id, String nom, String prenom, String email, String sexe, String adresse, String telephone, String role, String antecedents_medicaux, String specialite, String hopital, String motdepasse, String verification_code, String captcha, String img_url, String activation_token, boolean is_active, List<String> roles) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.sexe = sexe;
        this.adresse = adresse;
        this.telephone = telephone;
        this.role = role;
        this.antecedents_medicaux = antecedents_medicaux;
        this.specialite = specialite;
        this.hopital = hopital;
        this.motdepasse = motdepasse;
        this.verification_code = verification_code;
        this.captcha = captcha;
        this.img_url = img_url;
        this.activation_token = activation_token;
        this.is_active = is_active;
        this.roles = roles;
    }

    public Utilisateur(String nom, String prenom, String email, String sexe, String adresse, String telephone, String role, String antecedents_medicaux, String specialite, String hopital, String motdepasse, String verification_code, String captcha, String img_url, String activation_token, boolean is_active, List<String> roles) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.sexe = sexe;
        this.adresse = adresse;
        this.telephone = telephone;
        this.role = role;
        this.antecedents_medicaux = antecedents_medicaux;
        this.specialite = specialite;
        this.hopital = hopital;
        this.motdepasse = motdepasse;
        this.verification_code = verification_code;
        this.captcha = captcha;
        this.img_url = img_url;
        this.activation_token = activation_token;
        this.is_active = is_active;
        this.roles = roles;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAntecedents_medicaux() {
        return antecedents_medicaux;
    }

    public void setAntecedents_medicaux(String antecedents_medicaux) {
        this.antecedents_medicaux = antecedents_medicaux;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public String getHopital() {
        return hopital;
    }

    public void setHopital(String hopital) {
        this.hopital = hopital;
    }

    public String getMotdepasse() {
        return motdepasse;
    }

    public void setMotdepasse(String motdepasse) {this.motdepasse = motdepasse;}

    public String getVerification_code() {
        return verification_code;
    }

    public void setVerification_code(String verification_code) {
        this.verification_code = verification_code;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getActivation_token() {
        return activation_token;
    }

    public void setActivation_token(String activation_token) {
        this.activation_token = activation_token;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", sexe='" + sexe + '\'' +
                ", adresse='" + adresse + '\'' +
                ", telephone='" + telephone + '\'' +
                ", role='" + role + '\'' +
                ", antecedents_medicaux='" + antecedents_medicaux + '\'' +
                ", specialite='" + specialite + '\'' +
                ", hopital='" + hopital + '\'' +
                ", motdepasse='" + motdepasse + '\'' +
                ", verification_code='" + verification_code + '\'' +
                ", captcha='" + captcha + '\'' +
                ", img_url='" + img_url + '\'' +
                ", activation_token='" + activation_token + '\'' +
                ", is_active=" + is_active +
                ", roles=" + roles +
                '}';
    }
}
