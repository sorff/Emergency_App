package com.example.emergencyapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.emergencyapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private LinearLayout llServices;

    private static final String[][] SERVICES = {
            {"🏥", "CHU Urgences Rabat", "0.8 km", "0522000000"},
            {"👮", "Commissariat Central", "1.2 km", "19"},
            {"🏥", "Clinique Agdal", "1.1 km", "0522111111"},
            {"🚑", "SAMU Rabat", "1.5 km", "15"},
    };

    private static final double[][] COORDS = {
            {33.9890, -6.8544},
            {33.9926, -6.8543},
            {33.9751, -6.8650},
            {33.9751, -6.8495},
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        llServices = view.findViewById(R.id.llServices);
        ((TextView) view.findViewById(R.id.tvServicesTitle))
                .setText(getString(R.string.services_proches, SERVICES.length));

        afficherServices();
        view.findViewById(R.id.mapContainer).post(this::initialiserCarte);
    }

    /** Ajoute le fragment Maps par code, une fois l'écran affiché (évite l'ANR). */
    private void initialiserCarte() {
        try {
            SupportMapFragment mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.mapContainer, mapFragment)
                    .commitNowAllowingStateLoss();
            mapFragment.getMapAsync(this);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng centre = new LatLng(33.9716, -6.8498);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centre, 14f));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.addMarker(new MarkerOptions().position(centre).title("Moi")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        float[] couleurs = {BitmapDescriptorFactory.HUE_AZURE, BitmapDescriptorFactory.HUE_BLUE,
                BitmapDescriptorFactory.HUE_AZURE, BitmapDescriptorFactory.HUE_ORANGE};
        for (int i = 0; i < SERVICES.length; i++) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(COORDS[i][0], COORDS[i][1]))
                    .title(SERVICES[i][1]).snippet(SERVICES[i][2])
                    .icon(BitmapDescriptorFactory.defaultMarker(couleurs[i])));
        }
    }

    /** Crée les lignes de services proches dans la carte du bas. */
    private void afficherServices() {
        for (String[] s : SERVICES) {
            View row = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_service_row, llServices, false);
            ((TextView) row.findViewById(R.id.tvServiceIcon)).setText(s[0]);
            ((TextView) row.findViewById(R.id.tvServiceName)).setText(s[1]);
            ((TextView) row.findViewById(R.id.tvServiceDistance)).setText(s[2]);
            String tel = s[3];
            ((MaterialButton) row.findViewById(R.id.btnServiceCall))
                    .setOnClickListener(v -> startActivity(
                            new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel))));
            llServices.addView(row);
        }
    }
}
