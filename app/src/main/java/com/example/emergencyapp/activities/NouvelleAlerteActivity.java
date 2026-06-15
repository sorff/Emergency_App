package com.example.emergencyapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.emergencyapp.R;
import com.example.emergencyapp.database.DatabaseHelper;
import com.example.emergencyapp.models.Alerte;
import com.example.emergencyapp.models.AlerteApi;
import com.example.emergencyapp.network.RetrofitClient;
import com.example.emergencyapp.notifications.NotificationHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NouvelleAlerteActivity extends AppCompatActivity {

    private static final int CODE_CAMERA = 100;
    private static final int CODE_GPS = 102;

    private TextView chipPanne, chipAccident, chipMedical, chipIncendie, chipAutre;
    private TextInputLayout tilDescription;
    private TextInputEditText etDescription;
    private TextView tvNbCaracteres;
    private Switch swGPS;
    private TextView tvGpsStatut;
    private FrameLayout framePhoto;
    private LinearLayout llPhotoVide;
    private ImageView ivPhoto;
    private ImageButton btnSupprimerPhoto;
    private MaterialButton btnEnvoyer;

    private String typeSelectionne = null;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nouvelle_alerte);
        db = new DatabaseHelper(this);
        initViews();
        configurerChips();
        configurerGPS();
        configurerPhoto();
        configurerEnvoi();
        findViewById(R.id.btnRetour).setOnClickListener(v -> finish());
    }

    /** Initialise toutes les vues. */
    private void initViews() {
        chipPanne = findViewById(R.id.chipPanne);
        chipAccident = findViewById(R.id.chipAccident);
        chipMedical = findViewById(R.id.chipMedical);
        chipIncendie = findViewById(R.id.chipIncendie);
        chipAutre = findViewById(R.id.chipAutre);
        tilDescription = findViewById(R.id.tilDescription);
        etDescription = findViewById(R.id.etDescription);
        tvNbCaracteres = findViewById(R.id.tvNbCaracteres);
        swGPS = findViewById(R.id.swGPS);
        tvGpsStatut = findViewById(R.id.tvGpsStatut);
        framePhoto = findViewById(R.id.framePhoto);
        llPhotoVide = findViewById(R.id.llPhotoVide);
        ivPhoto = findViewById(R.id.ivPhoto);
        btnSupprimerPhoto = findViewById(R.id.btnSupprimerPhoto);
        btnEnvoyer = findViewById(R.id.btnEnvoyer);
    }

    /** Configure la sélection des chips de type d'urgence. */
    private void configurerChips() {
        View[] chips = {chipPanne, chipAccident, chipMedical, chipIncendie, chipAutre};
        String[] types = {"Panne", "Accident", "Médical", "Incendie", "Autre"};

        for (int i = 0; i < chips.length; i++) {
            int index = i;
            chips[i].setOnClickListener(v -> selectionnerChip(chips, (TextView) chips[index], types[index]));
        }

        etDescription.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvNbCaracteres.setText(getString(R.string.nb_caracteres, s.length()));
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    /** Sélectionne un chip et désélectionne les autres. */
    private void selectionnerChip(View[] chips, TextView selected, String type) {
        for (View chip : chips) {
            chip.setBackgroundResource(R.drawable.bg_chip_unselected);
            ((TextView) chip).setTextColor(getColor(R.color.primary));
        }
        selected.setBackgroundResource(R.drawable.bg_chip_selected);
        selected.setTextColor(getColor(R.color.white));
        typeSelectionne = type;
    }

    /** Configure le switch GPS. */
    private void configurerGPS() {
        swGPS.setOnCheckedChangeListener((btn, isChecked) -> {
            tvGpsStatut.setText(isChecked ? R.string.gps_activee : R.string.gps_desactivee);
        });
    }

    /** Configure la zone photo. */
    private void configurerPhoto() {
        framePhoto.setOnClickListener(v -> {
            if (ivPhoto.getVisibility() == View.GONE) ouvrirCamera();
        });

        btnSupprimerPhoto.setOnClickListener(v -> {
            ivPhoto.setVisibility(View.GONE);
            llPhotoVide.setVisibility(View.VISIBLE);
            btnSupprimerPhoto.setVisibility(View.GONE);
        });
    }

    /** Ouvre la caméra après vérification de la permission. */
    private void ouvrirCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CODE_CAMERA);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CODE_CAMERA);
        }
    }

    /** Configure le bouton d'envoi de l'alerte. */
    private void configurerEnvoi() {
        RetrofitClient retrofitClient = RetrofitClient.getInstance();
        btnEnvoyer.setOnClickListener(v -> validerEtEnvoyer(retrofitClient));
    }

    /** Valide les champs et envoie l'alerte. */
    private void validerEtEnvoyer(RetrofitClient retrofitClient) {
        if (typeSelectionne == null) {
            Toast.makeText(this, R.string.erreur_type, Toast.LENGTH_SHORT).show();
            return;
        }

        String description = etDescription.getText() != null
                ? etDescription.getText().toString().trim() : "";
        if (description.isEmpty()) {
            tilDescription.setError(getString(R.string.erreur_description));
            return;
        }
        tilDescription.setError(null);

        if (swGPS.isChecked()) {
            obtenirPosition();
        }

        Alerte alerte = new Alerte(typeSelectionne, description, latitude, longitude,
                null, "EN_COURS", System.currentTimeMillis());

        AlerteApi alerteApi = new AlerteApi(typeSelectionne, description, 1);
        retrofitClient.getApiService().envoyerAlerte(alerteApi).enqueue(new Callback<AlerteApi>() {
            @Override
            public void onResponse(@NonNull Call<AlerteApi> call, @NonNull Response<AlerteApi> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(NouvelleAlerteActivity.this, R.string.alerte_envoyee, Toast.LENGTH_SHORT).show();
                }
                sauvegarderEtTerminer(alerte);
            }

            @Override
            public void onFailure(@NonNull Call<AlerteApi> call, @NonNull Throwable t) {
                Toast.makeText(NouvelleAlerteActivity.this, R.string.erreur_reseau, Toast.LENGTH_SHORT).show();
                sauvegarderEtTerminer(alerte);
            }
        });
    }

    /** Sauvegarde l'alerte en SQLite et ferme l'activité. */
    private void sauvegarderEtTerminer(Alerte alerte) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                db.insererAlerte(alerte);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                NotificationHelper.sendNotification(NouvelleAlerteActivity.this,
                        "Alerte envoyée", "Votre alerte " + alerte.getType() + " a été transmise aux secours.");
                planifierPriseEnCharge(alerte.getType());
                finish();
            }
        }.execute();
    }

    /** Simule la notification de prise en charge par les secours après quelques secondes. */
    private void planifierPriseEnCharge(String type) {
        new Handler(Looper.getMainLooper()).postDelayed(() ->
                NotificationHelper.sendNotification(getApplicationContext(),
                        "Prise en charge ✓",
                        "Les secours ont pris en charge votre alerte « " + type + " ». Restez en sécurité."),
                7000);
    }

    /** Obtient la position GPS courante. */
    private void obtenirPosition() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CODE_GPS);
            return;
        }
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc == null) {
            loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (loc != null) {
            latitude = loc.getLatitude();
            longitude = loc.getLongitude();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_CAMERA && resultCode == RESULT_OK && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ivPhoto.setImageBitmap(photo);
            ivPhoto.setVisibility(View.VISIBLE);
            llPhotoVide.setVisibility(View.GONE);
            btnSupprimerPhoto.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_CAMERA && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ouvrirCamera();
        } else if (requestCode == CODE_GPS && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            obtenirPosition();
        } else {
            Toast.makeText(this, R.string.permission_camera_refusee, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
