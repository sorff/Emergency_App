package com.example.emergencyapp.models;

public class Contact {

    private int id;
    private String nom;
    private String telephone;
    private String relation;
    private boolean primaryResponder;

    public Contact() {}

    public Contact(String nom, String telephone) {
        this.nom = nom;
        this.telephone = telephone;
    }

    public Contact(String nom, String telephone, String relation, boolean primaryResponder) {
        this.nom = nom;
        this.telephone = telephone;
        this.relation = relation;
        this.primaryResponder = primaryResponder;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getRelation() { return relation; }
    public void setRelation(String relation) { this.relation = relation; }

    public boolean isPrimaryResponder() { return primaryResponder; }
    public void setPrimaryResponder(boolean primaryResponder) { this.primaryResponder = primaryResponder; }
}
