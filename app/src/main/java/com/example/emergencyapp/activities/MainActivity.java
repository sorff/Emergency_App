package com.example.emergencyapp.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.emergencyapp.R;
import com.example.emergencyapp.fragments.AlertsFragment;
import com.example.emergencyapp.fragments.ContactsFragment;
import com.example.emergencyapp.fragments.HomeFragment;
import com.example.emergencyapp.fragments.MapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(this::onNavItemSelected);

        if (savedInstanceState == null) {
            afficherFragment(new HomeFragment());
        }
        demanderPermissionNotifications();
    }

    /** Demande la permission notifications (obligatoire Android 13+). */
    private void demanderPermissionNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 99);
        }
    }

    /** Affiche le fragment correspondant à l'onglet sélectionné. */
    private boolean onNavItemSelected(@NonNull android.view.MenuItem item) {
        Fragment fragment = null;
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            fragment = new HomeFragment();
        } else if (id == R.id.nav_contacts) {
            fragment = new ContactsFragment();
        } else if (id == R.id.nav_map) {
            fragment = new MapFragment();
        } else if (id == R.id.nav_alerts) {
            fragment = new AlertsFragment();
        }
        if (fragment != null) {
            afficherFragment(fragment);
            return true;
        }
        return false;
    }

    /** Remplace le contenu par le fragment donné. */
    private void afficherFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    /** Permet aux fragments de changer d'onglet par programmation. */
    public void selectionnerOnglet(int menuId) {
        bottomNav.setSelectedItemId(menuId);
    }
}
