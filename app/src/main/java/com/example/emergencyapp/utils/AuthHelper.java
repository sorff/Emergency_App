package com.example.emergencyapp.utils;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

/**
 * Gère la vérification du numéro par OTP.
 * Si Firebase est configuré (google-services.json présent), utilise le vrai SMS.
 * Sinon, bascule automatiquement sur un mode simulé (n'importe quel code à 6 chiffres).
 */
public class AuthHelper {

    public interface Callback {
        /** Le code a été envoyé ; verificationId à conserver. */
        void onCodeSent(String verificationId);
        /** Vérification réussie. */
        void onVerified();
        /** Erreur ; message à afficher. */
        void onError(String message);
    }

    private String verificationId;

    /** Restaure l'identifiant de vérification (transmis entre écrans). */
    public void setVerificationId(String id) {
        this.verificationId = id;
    }

    /** Indique si Firebase est réellement configuré sur l'appareil. */
    public static boolean firebaseDisponible(Context ctx) {
        try {
            FirebaseApp.getInstance();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /** Démarre la vérification du numéro de téléphone. */
    public void envoyerCode(Activity activity, String telephone, Callback callback) {
        if (!firebaseDisponible(activity)) {
            // Mode simulé : on considère le code comme envoyé immédiatement.
            verificationId = "MOCK";
            callback.onCodeSent(verificationId);
            return;
        }

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber(telephone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        // Auto-récupération du code : connexion directe.
                        connecter(credential, callback);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        callback.onError(e.getMessage());
                    }

                    @Override
                    public void onCodeSent(@NonNull String id,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        verificationId = id;
                        callback.onCodeSent(id);
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    /** Vérifie le code saisi par l'utilisateur. */
    public void verifierCode(String code, Callback callback) {
        if ("MOCK".equals(verificationId)) {
            // Mode simulé : tout code à 6 chiffres est accepté.
            if (code != null && code.length() == 6) {
                callback.onVerified();
            } else {
                callback.onError("Code invalide");
            }
            return;
        }
        if (verificationId == null) {
            callback.onError("Aucun code envoyé");
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        connecter(credential, callback);
    }

    /** Connexion Firebase avec les identifiants téléphone. */
    private void connecter(PhoneAuthCredential credential, Callback callback) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onVerified();
                    } else {
                        String msg = task.getException() != null
                                ? task.getException().getMessage() : "Échec de la vérification";
                        callback.onError(msg);
                    }
                });
    }
}
