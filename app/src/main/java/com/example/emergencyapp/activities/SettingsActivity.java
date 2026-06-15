package com.example.emergencyapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.emergencyapp.R;
import com.example.emergencyapp.database.DatabaseHelper;
import com.example.emergencyapp.utils.SharedPrefsHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SettingsActivity extends AppCompatActivity {

    private static final String[] BLOOD_TYPES = {"—", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};

    private TextView tvAvatar;
    private TextInputEditText etNom, etTel, etConditions;
    private Spinner spBlood;
    private Switch swNotifications, swLocalisation;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        db = new DatabaseHelper(this);

        tvAvatar = findViewById(R.id.tvAvatarProfil);
        etNom = findViewById(R.id.etNom);
        etTel = findViewById(R.id.etTel);
        etConditions = findViewById(R.id.etConditions);
        spBlood = findViewById(R.id.spBlood);
        swNotifications = findViewById(R.id.swNotifications);
        swLocalisation = findViewById(R.id.swLocalisation);

        ((ImageButton) findViewById(R.id.btnRetour)).setOnClickListener(v -> finish());

        spBlood.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, BLOOD_TYPES));

        chargerDonnees();
        configurerActions();
    }

    /** Pré-remplit les champs depuis les préférences. */
    private void chargerDonnees() {
        String nom = SharedPrefsHelper.getNom(this);
        etNom.setText(nom);
        etTel.setText(SharedPrefsHelper.getTelephone(this));
        etConditions.setText(SharedPrefsHelper.getConditions(this));
        tvAvatar.setText(nom.isEmpty() ? "U" : nom.substring(0, 1).toUpperCase());

        String blood = SharedPrefsHelper.getBloodType(this);
        for (int i = 0; i < BLOOD_TYPES.length; i++) {
            if (BLOOD_TYPES[i].equals(blood)) {
                spBlood.setSelection(i);
                break;
            }
        }

        swNotifications.setChecked(SharedPrefsHelper.isNotificationsActive(this));
        swLocalisation.setChecked(SharedPrefsHelper.isLocalisationActive(this));
    }

    /** Branche tous les boutons et interrupteurs. */
    private void configurerActions() {
        ((MaterialButton) findViewById(R.id.btnSauvegarder)).setOnClickListener(v -> sauvegarder());

        swNotifications.setOnCheckedChangeListener((b, checked) ->
                SharedPrefsHelper.saveNotifications(this, checked));
        swLocalisation.setOnCheckedChangeListener((b, checked) ->
                SharedPrefsHelper.saveLocalisation(this, checked));

        ((MaterialButton) findViewById(R.id.btnViderHistorique))
                .setOnClickListener(v -> confirmerSuppression());
        ((MaterialButton) findViewById(R.id.btnLogout))
                .setOnClickListener(v -> confirmerLogout());
    }

    /** Enregistre le profil utilisateur. */
    private void sauvegarder() {
        String nom = etNom.getText() != null ? etNom.getText().toString().trim() : "";
        String tel = etTel.getText() != null ? etTel.getText().toString().trim() : "";
        String conditions = etConditions.getText() != null ? etConditions.getText().toString().trim() : "";
        String blood = spBlood.getSelectedItemPosition() == 0 ? "" : (String) spBlood.getSelectedItem();

        SharedPrefsHelper.saveNom(this, nom);
        SharedPrefsHelper.saveTelephone(this, tel);
        SharedPrefsHelper.saveConditions(this, conditions);
        SharedPrefsHelper.saveBloodType(this, blood);

        tvAvatar.setText(nom.isEmpty() ? "U" : nom.substring(0, 1).toUpperCase());
        Toast.makeText(this, R.string.profil_sauvegarde, Toast.LENGTH_SHORT).show();
    }

    /** Confirme puis vide l'historique des alertes. */
    private void confirmerSuppression() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirmer_suppression)
                .setMessage(R.string.message_suppression)
                .setPositiveButton(R.string.supprimer, (d, w) -> {
                    db.supprimerToutesAlertes();
                    Toast.makeText(this, R.string.historique_vide, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.annuler, null)
                .show();
    }

    /** Confirme puis déconnecte (efface le profil) et revient à la vérification. */
    private void confirmerLogout() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.logout)
                .setMessage(R.string.confirmer_logout)
                .setPositiveButton(R.string.logout, (d, w) -> deconnecter())
                .setNegativeButton(R.string.annuler, null)
                .show();
    }

    /** Efface le profil et relance le flux d'onboarding. */
    private void deconnecter() {
        SharedPrefsHelper.saveNom(this, "");
        SharedPrefsHelper.saveTelephone(this, "");
        SharedPrefsHelper.saveSeRappeler(this, false);
        Intent intent = new Intent(this, VerifyIdentityActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
