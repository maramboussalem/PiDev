package tn.esprit.entities;

import java.sql.Timestamp;

public class ParametresVitaux {
    private int id;
    private String name;
    private int fc;
    private int fr;
    private String ecg; // Changed to String
    private int tas;
    private int tad;
    private int age;
    private int spo2;
    private int gsc;
    private double gad;
    private Timestamp created_at;

    public ParametresVitaux() {}

    public ParametresVitaux(int id, String name, int fc, int fr, String ecg, int tas, int tad, int age, int spo2, int gsc, double gad, Timestamp created_at) {
        this.id = id;
        this.name = name;
        this.fc = fc;
        this.fr = fr;
        this.ecg = ecg;
        this.tas = tas;
        this.tad = tad;
        this.age = age;
        this.spo2 = spo2;
        this.gsc = gsc;
        this.gad = gad;
        this.created_at = created_at;
    }

    public ParametresVitaux(String name, int fc, int fr, String ecg, int tas, int tad, int age, int spo2, int gsc, double gad, Timestamp created_at) {
        this.name = name;
        this.fc = fc;
        this.fr = fr;
        this.ecg = ecg;
        this.tas = tas;
        this.tad = tad;
        this.age = age;
        this.spo2 = spo2;
        this.gsc = gsc;
        this.gad = gad;
        this.created_at = created_at;
    }

    // Getters and Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getFc() { return fc; }
    public void setFc(int fc) { this.fc = fc; }

    public int getFr() { return fr; }
    public void setFr(int fr) { this.fr = fr; }

    public String getEcg() { return ecg; }
    public void setEcg(String ecg) { this.ecg = ecg; }

    public int getTas() { return tas; }
    public void setTas(int tas) { this.tas = tas; }

    public int getTad() { return tad; }
    public void setTad(int tad) { this.tad = tad; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public int getSpo2() { return spo2; }
    public void setSpo2(int spo2) { this.spo2 = spo2; }

    public int getGsc() { return gsc; }
    public void setGsc(int gsc) { this.gsc = gsc; }

    public double getGad() { return gad; }
    public void setGad(double gad) { this.gad = gad; }

    public Timestamp getCreated_at() { return created_at; }
    public void setCreated_at(Timestamp created_at) { this.created_at = created_at; }

    @Override
    public String toString() {
        return "ParametresVitaux{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fc=" + fc +
                ", fr=" + fr +
                ", ecg='" + ecg + '\'' +
                ", tas=" + tas +
                ", tad=" + tad +
                ", age=" + age +
                ", spo2=" + spo2 +
                ", gsc=" + gsc +
                ", gad=" + gad +
                ", created_at=" + created_at +
                '}';
    }
}
