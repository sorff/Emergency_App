package com.example.emergencyapp.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.emergencyapp.R;
import com.example.emergencyapp.activities.MainActivity;
import com.example.emergencyapp.database.DatabaseHelper;
import com.example.emergencyapp.models.Contact;
import com.example.emergencyapp.notifications.NotificationHelper;
import com.example.emergencyapp.utils.SharedPrefsHelper;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final long HOLD_DURATION = 3000;

    private TextView tvAvatar, tvActiveContacts, tvLocation, tvSosHint;
    private View vSosRing, btnSOS;
    private DatabaseHelper db;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable holdRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DatabaseHelper(requireContext());

        tvAvatar = view.findViewById(R.id.tvAvatarHome);
        tvActiveContacts = view.findViewById(R.id.tvActiveContacts);
        tvLocation = view.findViewById(R.id.tvLocation);
        tvSosHint = view.findViewById(R.id.tvSosHint);
        vSosRing = view.findViewById(R.id.vSosRing);
        btnSOS = view.findViewById(R.id.btnSOS);

        afficherProfil();
        demarrerAnimation();
        configurerHoldSOS();

        // Tap sur l'avatar du header → écran Réglages
        tvAvatar.setOnClickListener(v ->
                startActivity(new Intent(requireContext(),
                        com.example.emergencyapp.activities.SettingsActivity.class)));

        ((MaterialCardView) view.findViewById(R.id.cardCallPolice))
                .setOnClickListener(v -> composer("17"));
        ((MaterialCardView) view.findViewById(R.id.cardCallMedical))
                .setOnClickListener(v -> composer("15"));
        view.findViewById(R.id.cardNotify).setOnClickListener(v -> ouvrirContacts());
        view.findViewById(R.id.btnLocate).setOnClickListener(v -> chargerPosition());

        chargerPosition();
    }

    @Override
    public void onResume() {
        super.onResume();
        majContactsActifs();
    }

    /** Affiche l'initiale de l'utilisateur dans le header. */
    private void afficherProfil() {
        String nom = SharedPrefsHelper.getNom(requireContext());
        String initiale = nom.isEmpty() ? "U" : nom.substring(0, 1).toUpperCase();
        tvAvatar.setText(initiale);
    }

    /** Lance l'animation pulse continue du ring SOS. */
    private void demarrerAnimation() {
        Animation pulse = AnimationUtils.loadAnimation(requireContext(), R.anim.pulse);
        vSosRing.startAnimation(pulse);
    }

    /** Configure le déclenchement SOS par appui maintenu de 3 secondes. */
    @SuppressLint("ClickableViewAccessibility")
    private void configurerHoldSOS() {
        btnSOS.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                debuterMaintien();
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP
                    || event.getAction() == MotionEvent.ACTION_CANCEL) {
                annulerMaintien();
                return true;
            }
            return false;
        });
    }

    /** Démarre le compte à rebours de l'appui maintenu. */
    private void debuterMaintien() {
        btnSOS.animate().scaleX(0.94f).scaleY(0.94f).setDuration(200).start();
        tvSosHint.setText("…");
        holdRunnable = this::declencherSOS;
        handler.postDelayed(holdRunnable, HOLD_DURATION);
    }

    /** Annule l'appui s'il est relâché avant la fin. */
    private void annulerMaintien() {
        btnSOS.animate().scaleX(1f).scaleY(1f).setDuration(200).start();
        tvSosHint.setText(R.string.sos_hold);
        if (holdRunnable != null) handler.removeCallbacks(holdRunnable);
    }

    /** Déclenche l'alerte SOS : appel, SMS aux contacts et notification. */
    private void declencherSOS() {
        tvSosHint.setText(R.string.sos_hold);
        btnSOS.animate().scaleX(1f).scaleY(1f).setDuration(200).start();
        Toast.makeText(requireContext(), R.string.sos_declenche, Toast.LENGTH_LONG).show();

        NotificationHelper.sendNotification(requireContext(),
                getString(R.string.sos_declenche), getString(R.string.notify_contacts));

        envoyerSMSContacts();
        composer("15");
    }

    /** Envoie un SMS d'urgence à chaque contact enregistré. */
    private void envoyerSMSContacts() {
        String nom = SharedPrefsHelper.getNom(requireContext());
        String message = getString(R.string.message_sos, nom);
        ArrayList<Contact> contacts = db.getAllContacts();
        for (Contact contact : contacts) {
            Intent sms = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + contact.getTelephone()));
            sms.putExtra("sms_body", message);
            startActivity(sms);
        }
    }

    /** Ouvre le composeur téléphonique (sans permission CALL_PHONE). */
    private void composer(String numero) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + numero)));
    }

    /** Bascule sur l'onglet Contacts. */
    private void ouvrirContacts() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).selectionnerOnglet(R.id.nav_contacts);
        }
    }

    /** Met à jour le nombre de contacts actifs. */
    private void majContactsActifs() {
        int n = db.getAllContacts().size();
        tvActiveContacts.setText(getString(R.string.active_contacts, n));
    }

    /** Récupère la dernière position connue et affiche une adresse lisible. */
    private void chargerPosition() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        try {
            LocationManager lm = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
            Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc == null) loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (loc != null) {
                afficherAdresse(loc);
            }
        } catch (SecurityException ignored) {
        }
    }

    /** Reverse-geocode la position en adresse, sinon affiche les coordonnées. */
    private void afficherAdresse(Location loc) {
        try {
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address a = addresses.get(0);
                String ligne = a.getThoroughfare() != null ? a.getThoroughfare() : a.getLocality();
                tvLocation.setText(ligne != null ? ligne : a.getAddressLine(0));
                return;
            }
        } catch (Exception ignored) {
        }
        tvLocation.setText(String.format(Locale.US, "%.4f, %.4f", loc.getLatitude(), loc.getLongitude()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            chargerPosition();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (holdRunnable != null) handler.removeCallbacks(holdRunnable);
        if (db != null) db.close();
    }
}
