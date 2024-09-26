package com.example.test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder> {
    private List<String> deliveryNames;
    private OnDeliveryClickListener listener;

    public DeliveryAdapter(List<String> deliveryNames, OnDeliveryClickListener listener) {
        this.deliveryNames = deliveryNames;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DeliveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new DeliveryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryViewHolder holder, int position) {
        String deliveryName = deliveryNames.get(position);
        holder.bind(deliveryName, listener);
    }

    @Override
    public int getItemCount() {
        return deliveryNames.size();
    }

    public void updateDeliveries(List<String> newDeliveryNames) {
        this.deliveryNames = newDeliveryNames;
        notifyDataSetChanged();
    }

    public interface OnDeliveryClickListener {
        void onDeliveryClick(String deliveryName);
    }

    class DeliveryViewHolder extends RecyclerView.ViewHolder {
        private TextView deliveryTextView;

        public DeliveryViewHolder(View itemView) {
            super(itemView);
            deliveryTextView = itemView.findViewById(android.R.id.text1);
        }

        public void bind(final String deliveryName, final OnDeliveryClickListener listener) {
            deliveryTextView.setText(deliveryName);
            itemView.setOnClickListener(v -> listener.onDeliveryClick(deliveryName));
        }
    }
}
