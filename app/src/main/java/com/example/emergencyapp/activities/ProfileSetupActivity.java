package com.example.emergencyapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.emergencyapp.R;
import com.example.emergencyapp.utils.SharedPrefsHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ProfileSetupActivity extends AppCompatActivity {

    private static final String[] BLOOD_TYPES = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};

    private TextInputLayout tilName;
    private TextInputEditText etName, etPhone, etConditions;
    private GridLayout gridBlood;
    private String groupeSelectionne = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        tilName = findViewById(R.id.tilName);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etConditions = findViewById(R.id.etConditions);
        gridBlood = findViewById(R.id.gridBlood);

        prefRemplir();
        construireGrilleSang();

        ((MaterialButton) findViewById(R.id.btnSaveProfile)).setOnClickListener(v -> sauvegarder());
    }

    /** Pré-remplit les champs depuis les préférences. */
    private void prefRemplir() {
        etPhone.setText(SharedPrefsHelper.getTelephone(this));
        etName.setText(SharedPrefsHelper.getNom(this));
        etConditions.setText(SharedPrefsHelper.getConditions(this));
        groupeSelectionne = SharedPrefsHelper.getBloodType(this);
    }

    /** Construit les 8 boutons de groupe sanguin (sélection unique). */
    private void construireGrilleSang() {
        for (String type : BLOOD_TYPES) {
            TextView btn = creerBoutonSang(type);
            gridBlood.addView(btn);
        }
    }

    /** Crée un bouton de groupe sanguin sélectionnable. */
    private TextView creerBoutonSang(String type) {
        TextView btn = new TextView(this);
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
        lp.width = 0;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
        lp.setMargins(6, 6, 6, 6);
        btn.setLayoutParams(lp);
        btn.setText(type);
        btn.setGravity(Gravity.CENTER);
        btn.setPadding(0, 36, 0, 36);
        btn.setTextSize(15);
        appliquerStyleSang(btn, type.equals(groupeSelectionne));
        btn.setOnClickListener(v -> selectionnerGroupe(type));
        return btn;
    }

    /** Sélectionne un groupe sanguin et met à jour l'apparence. */
    private void selectionnerGroupe(String type) {
        groupeSelectionne = type;
        for (int i = 0; i < gridBlood.getChildCount(); i++) {
            TextView b = (TextView) gridBlood.getChildAt(i);
            appliquerStyleSang(b, b.getText().toString().equals(type));
        }
    }

    /** Applique le style sélectionné ou non à un bouton. */
    private void appliquerStyleSang(TextView btn, boolean selected) {
        if (selected) {
            btn.setBackgroundResource(R.drawable.bg_chip_selected);
            btn.setTextColor(ContextCompat.getColor(this, R.color.white));
        } else {
            btn.setBackgroundResource(R.drawable.bg_otp_box);
            btn.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        }
    }

    /** Valide et enregistre le profil, puis ouvre l'app principale. */
    private void sauvegarder() {
        String nom = etName.getText() != null ? etName.getText().toString().trim() : "";
        if (nom.isEmpty()) {
            tilName.setError(getString(R.string.erreur_nom));
            return;
        }
        tilName.setError(null);

        String tel = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String conditions = etConditions.getText() != null ? etConditions.getText().toString().trim() : "";

        SharedPrefsHelper.saveNom(this, nom);
        SharedPrefsHelper.saveTelephone(this, tel);
        SharedPrefsHelper.saveBloodType(this, groupeSelectionne);
        SharedPrefsHelper.saveConditions(this, conditions);

        Toast.makeText(this, R.string.profil_sauvegarde, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
    }
}
