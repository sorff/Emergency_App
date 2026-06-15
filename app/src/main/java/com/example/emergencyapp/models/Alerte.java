package com.example.emergencyapp.models;

public class Alerte {

    private int id;
    private String type;
    private String description;
    private double latitude;
    private double longitude;
    private String photoPath;
    private String statut;
    private long timestamp;

    public Alerte() {}

    public Alerte(String type, String description, double latitude, double longitude,
                  String photoPath, String statut, long timestamp) {
        this.type = type;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photoPath = photoPath;
        this.statut = statut;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
