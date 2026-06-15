package com.example.emergencyapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.emergencyapp.R;
import com.example.emergencyapp.utils.AuthHelper;
import com.example.emergencyapp.utils.SharedPrefsHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class VerifyIdentityActivity extends AppCompatActivity {

    private TextInputLayout tilPhone;
    private TextInputEditText etPhone;
    private MaterialButton btnSendCode;
    private final AuthHelper authHelper = new AuthHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_identity);
        tilPhone = findViewById(R.id.tilPhone);
        etPhone = findViewById(R.id.etPhone);
        btnSendCode = findViewById(R.id.btnSendCode);
        btnSendCode.setOnClickListener(v -> envoyerCode());
    }

    /** Valide le numéro et lance l'envoi du code OTP. */
    private void envoyerCode() {
        String saisie = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        if (saisie.isEmpty()) {
            tilPhone.setError(getString(R.string.erreur_telephone));
            return;
        }
        tilPhone.setError(null);

        // Normalise au format international marocain pour Firebase.
        String numeroComplet = saisie.startsWith("+") ? saisie : "+212" + saisie.replaceAll("[^0-9]", "");
        SharedPrefsHelper.saveTelephone(this, numeroComplet);
        btnSendCode.setEnabled(false);

        authHelper.envoyerCode(this, numeroComplet, new AuthHelper.Callback() {
            @Override
            public void onCodeSent(String verificationId) {
                Toast.makeText(VerifyIdentityActivity.this, R.string.code_envoye, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(VerifyIdentityActivity.this, OtpActivity.class);
                intent.putExtra("phone", numeroComplet);
                intent.putExtra("verificationId", verificationId);
                startActivity(intent);
                btnSendCode.setEnabled(true);
            }

            @Override
            public void onVerified() {
                // Auto-récupération possible : on passe directement au profil.
                allerAuProfil();
            }

            @Override
            public void onError(String message) {
                btnSendCode.setEnabled(true);
                Toast.makeText(VerifyIdentityActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    /** Ouvre l'écran de configuration du profil. */
    private void allerAuProfil() {
        startActivity(new Intent(this, ProfileSetupActivity.class));
        finish();
    }
}
