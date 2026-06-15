package com.example.emergencyapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.emergencyapp.R;
import com.example.emergencyapp.utils.SharedPrefsHelper;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        planifierNavigation();
    }

    /** Après le splash, va à l'accueil si un profil existe, sinon au login. */
    private void planifierNavigation() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(this, prochainEcran()));
            finish();
        }, SPLASH_DELAY);
    }

    /** Détermine l'écran de destination selon l'existence d'un profil sauvegardé. */
    private Class<?> prochainEcran() {
        String nom = SharedPrefsHelper.getNom(this);
        String tel = SharedPrefsHelper.getTelephone(this);
        boolean profilExiste = !nom.isEmpty() && !tel.isEmpty();
        // Profil déjà configuré → app principale ; sinon → vérification d'identité.
        return profilExiste ? MainActivity.class : VerifyIdentityActivity.class;
    }
}
