package com.example.emergencyapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emergencyapp.R;
import com.example.emergencyapp.activities.NouvelleAlerteActivity;
import com.example.emergencyapp.adapters.NotificationAdapter;
import com.example.emergencyapp.database.DatabaseHelper;
import com.example.emergencyapp.models.Alerte;
import com.example.emergencyapp.models.NotifItem;
import com.example.emergencyapp.utils.SharedPrefsHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlertsFragment extends Fragment {

    private RecyclerView rvNotifications;
    private TextView tvTrustedCount;
    private DatabaseHelper db;
    private final List<NotifItem> items = new ArrayList<>();
    private NotificationAdapter adapter;
    private final SimpleDateFormat sdf = new SimpleDateFormat("d MMM 'à' HH:mm", Locale.FRENCH);

    // Notifications de démonstration (prise en charge + infos)
    private static final String[][] MOCK = {
            {"Proximité d'urgence", "Incident à haut risque signalé à moins de 500 m de votre position.", "il y a 2 min"},
            {"Contact en sécurité", "Votre contact de confiance s'est marqué comme en sécurité.", "il y a 15 min"},
            {"Mise à jour système", "AidGuard 2.4 est disponible. Optimisation batterie améliorée.", "il y a 1 h"},
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alerts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DatabaseHelper(requireContext());

        rvNotifications = view.findViewById(R.id.rvNotifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new NotificationAdapter(items);
        rvNotifications.setAdapter(adapter);

        tvTrustedCount = view.findViewById(R.id.tvTrustedCount);

        String nom = SharedPrefsHelper.getNom(requireContext());
        TextView avatar = view.findViewById(R.id.tvAvatarAlerts);
        avatar.setText(nom.isEmpty() ? "U" : nom.substring(0, 1).toUpperCase());
        avatar.setOnClickListener(v ->
                startActivity(new Intent(requireContext(),
                        com.example.emergencyapp.activities.SettingsActivity.class)));

        view.findViewById(R.id.btnMarkAllRead).setOnClickListener(v ->
                Toast.makeText(requireContext(), R.string.mark_all_read, Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.fabNewAlert).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), NouvelleAlerteActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        tvTrustedCount.setText(db.getAllContacts().size() + " actifs");
        chargerNotifications();
    }

    /** Construit la liste : prises en charge des alertes réelles + notifications mock. */
    private void chargerNotifications() {
        items.clear();

        ArrayList<Alerte> alertes = db.getAllAlertes();
        for (Alerte a : alertes) {
            // Chaque alerte enregistrée génère une notification de prise en charge.
            String temps = sdf.format(new Date(a.getTimestamp()));
            int type = "EN_COURS".equals(a.getStatut())
                    ? NotifItem.TYPE_ALERTE : NotifItem.TYPE_PRISE_EN_CHARGE;
            String titre = "EN_COURS".equals(a.getStatut())
                    ? "Alerte envoyée — " + a.getType()
                    : "Prise en charge — " + a.getType();
            String corps = "EN_COURS".equals(a.getStatut())
                    ? "Les secours ont été notifiés. " + safe(a.getDescription())
                    : "Les secours ont pris en charge votre alerte.";
            items.add(new NotifItem(titre, corps, temps, type));
        }

        for (String[] m : MOCK) {
            items.add(new NotifItem(m[0], m[1], m[2], NotifItem.TYPE_INFO));
        }
        adapter.notifyDataSetChanged();
    }

    /** Évite les null dans les descriptions. */
    private String safe(String s) {
        return s != null ? s : "";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (db != null) db.close();
    }
}
