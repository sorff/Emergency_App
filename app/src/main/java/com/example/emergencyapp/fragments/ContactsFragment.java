package com.example.emergencyapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.emergencyapp.R;
import com.example.emergencyapp.activities.AddContactActivity;
import com.example.emergencyapp.database.DatabaseHelper;
import com.example.emergencyapp.models.Contact;
import com.example.emergencyapp.utils.SharedPrefsHelper;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {

    private LinearLayout llContacts;
    private TextView tvEmpty;
    private EditText etSearch;
    private DatabaseHelper db;
    private final ArrayList<Contact> tous = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DatabaseHelper(requireContext());
        llContacts = view.findViewById(R.id.llContacts);
        tvEmpty = view.findViewById(R.id.tvEmptyContacts);
        etSearch = view.findViewById(R.id.etSearch);

        String nom = SharedPrefsHelper.getNom(requireContext());
        TextView avatar = view.findViewById(R.id.tvAvatarContacts);
        avatar.setText(nom.isEmpty() ? "U" : nom.substring(0, 1).toUpperCase());
        avatar.setOnClickListener(v ->
                startActivity(new Intent(requireContext(),
                        com.example.emergencyapp.activities.SettingsActivity.class)));

        view.findViewById(R.id.btnAddContact).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), AddContactActivity.class)));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                filtrer(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        tous.clear();
        tous.addAll(db.getAllContacts());
        filtrer(etSearch.getText().toString());
    }

    /** Affiche les contacts filtrés par la recherche. */
    private void filtrer(String requete) {
        llContacts.removeAllViews();
        List<Contact> filtres = new ArrayList<>();
        String q = requete.trim().toLowerCase();
        for (Contact c : tous) {
            if (q.isEmpty() || c.getNom().toLowerCase().contains(q)
                    || c.getTelephone().contains(q)) {
                filtres.add(c);
            }
        }
        tvEmpty.setVisibility(filtres.isEmpty() ? View.VISIBLE : View.GONE);
        for (Contact c : filtres) {
            llContacts.addView(creerCarte(c));
        }
    }

    /** Crée la carte d'un contact avec appel et suppression. */
    private View creerCarte(Contact contact) {
        View card = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_contact_card, llContacts, false);

        String initiale = contact.getNom().isEmpty() ? "?"
                : contact.getNom().substring(0, 1).toUpperCase();
        ((TextView) card.findViewById(R.id.tvAvatar)).setText(initiale);
        ((TextView) card.findViewById(R.id.tvNom)).setText(contact.getNom());
        ((TextView) card.findViewById(R.id.tvTelephone))
                .setText("Mobile • " + contact.getTelephone());

        Switch sw = card.findViewById(R.id.swCall);
        sw.setOnCheckedChangeListener((b, checked) -> {
            if (checked) {
                startActivity(new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + contact.getTelephone())));
                b.setChecked(false);
            }
        });

        card.setOnLongClickListener(v -> {
            confirmerSuppression(contact);
            return true;
        });
        return card;
    }

    /** Demande confirmation puis supprime le contact. */
    private void confirmerSuppression(Contact contact) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.supprimer)
                .setMessage(contact.getNom() + " ?")
                .setPositiveButton(R.string.supprimer, (d, w) -> {
                    db.supprimerContact(contact.getId());
                    tous.remove(contact);
                    filtrer(etSearch.getText().toString());
                })
                .setNegativeButton(R.string.annuler, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (db != null) db.close();
    }
}
