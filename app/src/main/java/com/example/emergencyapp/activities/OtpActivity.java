package com.example.emergencyapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.emergencyapp.R;
import com.example.emergencyapp.utils.AuthHelper;
import com.google.android.material.button.MaterialButton;

public class OtpActivity extends AppCompatActivity {

    private EditText[] cases;
    private final AuthHelper authHelper = new AuthHelper();
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        phone = getIntent().getStringExtra("phone");
        authHelper.setVerificationId(getIntent().getStringExtra("verificationId"));

        cases = new EditText[]{
                findViewById(R.id.otp1), findViewById(R.id.otp2), findViewById(R.id.otp3),
                findViewById(R.id.otp4), findViewById(R.id.otp5), findViewById(R.id.otp6)
        };
        configurerCases();

        if (phone != null) {
            ((android.widget.TextView) findViewById(R.id.tvSubtitle))
                    .setText(getString(R.string.enter_code, phone));
        }

        findViewById(R.id.btnRetour).setOnClickListener(v -> finish());
        ((MaterialButton) findViewById(R.id.btnVerify)).setOnClickListener(v -> verifier());
        findViewById(R.id.btnResend).setOnClickListener(v -> renvoyer());
    }

    /** Gère l'auto-avancement et le retour arrière entre les cases. */
    private void configurerCases() {
        for (int i = 0; i < cases.length; i++) {
            final int index = i;
            cases[i].addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
                @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                    if (s.length() == 1 && index < cases.length - 1) {
                        cases[index + 1].requestFocus();
                    }
                }
                @Override public void afterTextChanged(Editable s) {}
            });
            cases[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN
                        && cases[index].getText().toString().isEmpty() && index > 0) {
                    cases[index - 1].requestFocus();
                    cases[index - 1].setText("");
                    return true;
                }
                return false;
            });
        }
    }

    /** Concatène les chiffres et vérifie le code. */
    private void verifier() {
        StringBuilder code = new StringBuilder();
        for (EditText c : cases) code.append(c.getText().toString().trim());

        if (code.length() < 6) {
            Toast.makeText(this, R.string.code_invalide, Toast.LENGTH_SHORT).show();
            return;
        }

        authHelper.verifierCode(code.toString(), new AuthHelper.Callback() {
            @Override public void onCodeSent(String verificationId) {}

            @Override
            public void onVerified() {
                startActivity(new Intent(OtpActivity.this, ProfileSetupActivity.class));
                finishAffinity();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(OtpActivity.this, R.string.code_invalide, Toast.LENGTH_LONG).show();
            }
        });
    }

    /** Relance l'envoi du code. */
    private void renvoyer() {
        if (phone == null) return;
        authHelper.envoyerCode(this, phone, new AuthHelper.Callback() {
            @Override public void onCodeSent(String verificationId) {
                Toast.makeText(OtpActivity.this, R.string.code_envoye, Toast.LENGTH_SHORT).show();
            }
            @Override public void onVerified() {}
            @Override public void onError(String message) {
                Toast.makeText(OtpActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
