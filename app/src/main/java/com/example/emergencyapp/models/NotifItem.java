package com.example.emergencyapp.models;

/** Élément affiché dans la liste des notifications/alertes (RecyclerView). */
public class NotifItem {

    public static final int TYPE_INFO = 0;
    public static final int TYPE_ALERTE = 1;
    public static final int TYPE_PRISE_EN_CHARGE = 2;

    private final String titre;
    private final String corps;
    private final String temps;
    private final int type;

    public NotifItem(String titre, String corps, String temps, int type) {
        this.titre = titre;
        this.corps = corps;
        this.temps = temps;
        this.type = type;
    }

    public String getTitre() { return titre; }
    public String getCorps() { return corps; }
    public String getTemps() { return temps; }
    public int getType() { return type; }
}
