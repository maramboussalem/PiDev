package tn.esprit.entities;

import java.sql.Date;

public class ParametresVitaux {
    private int id;
    private String name;
    private int fc;
    private int fr;
    private int ecg;
    private int tas;
    private int tad;
    private int age;
    private int spo2;
    private int gsc;
    private float gad;
    private Date created_at; // ✅ Renamed from dateSaisie

    // ✅ Default constructor
    public ParametresVitaux() {}

    // ✅ Full constructor with ID and created_at
    public ParametresVitaux(int id, String name, int fc, int fr, int ecg, int tas, int tad, int age, int spo2, int gsc, float gad, Date created_at) {
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

    // ✅ Constructor without ID (for insertion)
    public ParametresVitaux(String name, int fc, int fr, int ecg, int tas, int tad, int age, int spo2, int gsc, float gad, Date created_at) {
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

    // ✅ Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFc() {
        return fc;
    }

    public void setFc(int fc) {
        this.fc = fc;
    }

    public int getFr() {
        return fr;
    }

    public void setFr(int fr) {
        this.fr = fr;
    }

    public int getEcg() {
        return ecg;
    }

    public void setEcg(int ecg) {
        this.ecg = ecg;
    }

    public int getTas() {
        return tas;
    }

    public void setTas(int tas) {
        this.tas = tas;
    }

    public int getTad() {
        return tad;
    }

    public void setTad(int tad) {
        this.tad = tad;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSpo2() {
        return spo2;
    }

    public void setSpo2(int spo2) {
        this.spo2 = spo2;
    }

    public int getGsc() {
        return gsc;
    }

    public void setGsc(int gsc) {
        this.gsc = gsc;
    }

    public float getGad() {
        return gad;
    }

    public void setGad(float gad) {
        this.gad = gad;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    // ✅ Updated toString
    @Override
    public String toString() {
        return "ParametresVitaux{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fc=" + fc +
                ", fr=" + fr +
                ", ecg=" + ecg +
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