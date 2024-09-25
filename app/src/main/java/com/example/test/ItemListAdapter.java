package com.example.test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

    private final List<Item> items;

    public ItemListAdapter(List<Item> items) {
        this.items = items;
    }

    public interface OnItemButtonClickListener {
        void onItemButtonClick(Item item);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        Item item = items.get(position);
        holder.productNameTextView.setText(item.getProductName());
        holder.serialNumberTextView.setText(item.getSerialNumber());
        holder.positionTextView.setText(item.getPosition());
        if (item.isLoaded()) {
            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(android.R.color.holo_green_light));
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(android.R.color.white));
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView serialNumberTextView;
        TextView positionTextView;
        Button actionButton;

        ViewHolder(View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.textViewProductName);
            serialNumberTextView = itemView.findViewById(R.id.textViewSerialNumber);
            positionTextView = itemView.findViewById(R.id.textViewPosition);
            actionButton = itemView.findViewById(R.id.buttonScanner);
        }
    }
}
