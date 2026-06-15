package com.example.emergencyapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emergencyapp.R;
import com.example.emergencyapp.models.NotifItem;

import java.util.List;

/** Adapter RecyclerView pour la liste des notifications/alertes. */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.VH> {

    private final List<NotifItem> items;

    public NotificationAdapter(List<NotifItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        NotifItem item = items.get(position);
        holder.tvTitle.setText(item.getTitre());
        holder.tvBody.setText(item.getCorps());
        holder.tvTime.setText(item.getTemps());
        holder.ivIcon.setImageResource(iconePour(item.getType()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /** Choisit l'icône selon le type de notification. */
    private int iconePour(int type) {
        switch (type) {
            case NotifItem.TYPE_PRISE_EN_CHARGE: return R.drawable.ic_medical;
            case NotifItem.TYPE_ALERTE: return R.drawable.ic_alerts;
            default: return R.drawable.ic_warning;
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tvTitle, tvBody, tvTime;
        final ImageView ivIcon;

        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotifTitle);
            tvBody = itemView.findViewById(R.id.tvNotifBody);
            tvTime = itemView.findViewById(R.id.tvNotifTime);
            ivIcon = itemView.findViewById(R.id.ivNotifIcon);
        }
    }
}
