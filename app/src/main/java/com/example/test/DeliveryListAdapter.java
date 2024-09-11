// DeliveryListAdapter.java
package com.example.test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class DeliveryListAdapter extends RecyclerView.Adapter<DeliveryListAdapter.ViewHolder> {

    private final List<String> deliveries;
    private final Map<String, Boolean> deliveryStatus; // Map to hold delivery status
    private final OnDeliveryClickListener onDeliveryClickListener;

    public DeliveryListAdapter(List<String> deliveries, Map<String, Boolean> deliveryStatus, OnDeliveryClickListener onDeliveryClickListener) {
        this.deliveries = deliveries;
        this.deliveryStatus = deliveryStatus;
        this.onDeliveryClickListener = onDeliveryClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delivery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String delivery = deliveries.get(position);
        holder.deliveryTextView.setText(delivery);

        // Set background color based on delivery status
        if (deliveryStatus.getOrDefault(delivery, false)) {
            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(android.R.color.holo_green_light));
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(android.R.color.white));
        }

        holder.itemView.setOnClickListener(v -> onDeliveryClickListener.onDeliveryClick(delivery));
    }

    @Override
    public int getItemCount() {
        return deliveries.size();
    }

    public interface OnDeliveryClickListener {
        void onDeliveryClick(String delivery);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView deliveryTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            deliveryTextView = itemView.findViewById(R.id.deliveryTextView);
        }
    }
}
