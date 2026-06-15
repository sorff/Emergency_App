package com.example.emergencyapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.emergencyapp.models.Alerte;
import com.example.emergencyapp.models.Contact;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "emergency.db";
    private static final int DB_VERSION = 2;

    private static final String TABLE_ALERTES = "alertes";
    private static final String TABLE_CONTACTS = "contacts_urgence";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_ALERTES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "type TEXT," +
                "description TEXT," +
                "latitude REAL," +
                "longitude REAL," +
                "photo_path TEXT," +
                "statut TEXT," +
                "timestamp INTEGER)");

        db.execSQL("CREATE TABLE " + TABLE_CONTACTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nom TEXT," +
                "telephone TEXT," +
                "relation TEXT," +
                "primary_responder INTEGER DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALERTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    /** Insère une alerte dans la base de données. */
    public void insererAlerte(Alerte alerte) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("type", alerte.getType());
        values.put("description", alerte.getDescription());
        values.put("latitude", alerte.getLatitude());
        values.put("longitude", alerte.getLongitude());
        values.put("photo_path", alerte.getPhotoPath());
        values.put("statut", alerte.getStatut());
        values.put("timestamp", alerte.getTimestamp());
        db.insert(TABLE_ALERTES, null, values);
        db.close();
    }

    /** Retourne toutes les alertes triées par timestamp décroissant. */
    public ArrayList<Alerte> getAllAlertes() {
        ArrayList<Alerte> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ALERTES + " ORDER BY timestamp DESC", null);
        if (cursor.moveToFirst()) {
            do {
                Alerte a = new Alerte();
                a.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                a.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
                a.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                a.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")));
                a.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")));
                a.setPhotoPath(cursor.getString(cursor.getColumnIndexOrThrow("photo_path")));
                a.setStatut(cursor.getString(cursor.getColumnIndexOrThrow("statut")));
                a.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")));
                list.add(a);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    /** Supprime une alerte par son identifiant. */
    public void supprimerAlerte(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_ALERTES, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    /** Met à jour le statut d'une alerte. */
    public void changerStatut(int id, String statut) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("statut", statut);
        db.update(TABLE_ALERTES, values, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    /** Supprime toutes les alertes. */
    public void supprimerToutesAlertes() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_ALERTES, null, null);
        db.close();
    }

    /** Insère un contact d'urgence. */
    public void insererContact(Contact contact) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nom", contact.getNom());
        values.put("telephone", contact.getTelephone());
        values.put("relation", contact.getRelation());
        values.put("primary_responder", contact.isPrimaryResponder() ? 1 : 0);
        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }

    /** Retourne tous les contacts d'urgence. */
    public ArrayList<Contact> getAllContacts() {
        ArrayList<Contact> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS + " ORDER BY nom ASC", null);
        if (cursor.moveToFirst()) {
            do {
                Contact c = new Contact();
                c.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                c.setNom(cursor.getString(cursor.getColumnIndexOrThrow("nom")));
                c.setTelephone(cursor.getString(cursor.getColumnIndexOrThrow("telephone")));
                c.setRelation(cursor.getString(cursor.getColumnIndexOrThrow("relation")));
                c.setPrimaryResponder(cursor.getInt(cursor.getColumnIndexOrThrow("primary_responder")) == 1);
                list.add(c);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    /** Supprime un contact par son identifiant. */
    public void supprimerContact(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CONTACTS, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }
}
