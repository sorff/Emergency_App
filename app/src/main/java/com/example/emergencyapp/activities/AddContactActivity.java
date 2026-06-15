package com.example.emergencyapp.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.emergencyapp.R;
import com.example.emergencyapp.database.DatabaseHelper;
import com.example.emergencyapp.models.Contact;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddContactActivity extends AppCompatActivity {

    private static final String[] RELATIONS = {
            "Sélectionner une relation", "Famille", "Ami(e)", "Conjoint(e)",
            "Collègue", "Voisin(e)", "Médecin", "Autre"
    };

    private TextInputLayout tilName, tilPhone;
    private TextInputEditText etName, etPhone;
    private Spinner spRelationship;
    private Switch swPrimary;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        db = new DatabaseHelper(this);

        tilName = findViewById(R.id.tilName);
        tilPhone = findViewById(R.id.tilPhone);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        spRelationship = findViewById(R.id.spRelationship);
        swPrimary = findViewById(R.id.swPrimary);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, RELATIONS);
        spRelationship.setAdapter(adapter);

        findViewById(R.id.btnRetour).setOnClickListener(v -> finish());
        ((MaterialButton) findViewById(R.id.btnCancel)).setOnClickListener(v -> finish());
        ((MaterialButton) findViewById(R.id.btnSave)).setOnClickListener(v -> enregistrer());
    }

    /** Valide les champs et enregistre le contact en base. */
    private void enregistrer() {
        String nom = etName.getText() != null ? etName.getText().toString().trim() : "";
        String tel = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";

        if (nom.isEmpty()) {
            tilName.setError(getString(R.string.erreur_nom_contact));
            return;
        }
        tilName.setError(null);
        if (tel.isEmpty()) {
            tilPhone.setError(getString(R.string.erreur_tel_contact));
            return;
        }
        tilPhone.setError(null);

        String relation = spRelationship.getSelectedItemPosition() == 0
                ? "" : (String) spRelationship.getSelectedItem();
        Contact contact = new Contact(nom, tel, relation, swPrimary.isChecked());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                db.insererContact(contact);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                Toast.makeText(AddContactActivity.this, R.string.save_contact, Toast.LENGTH_SHORT).show();
                finish();
            }
        }.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
