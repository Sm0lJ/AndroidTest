// BranchListAdapter.java
package com.example.test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class BranchListAdapter extends RecyclerView.Adapter<BranchListAdapter.ViewHolder> {

    private final List<String> branches;
    private final OnBranchClickListener onBranchClickListener;
    private final Map<String, Boolean> branchStatus;

    public BranchListAdapter(List<String> branches, Map<String, Boolean> branchStatus, OnBranchClickListener onBranchClickListener) {
        this.branches = branches;
        this.branchStatus = branchStatus;
        this.onBranchClickListener = onBranchClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_branch, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String branch = branches.get(position);
        holder.branchTextView.setText(branch);
        if (branchStatus.getOrDefault(branch, false)) {
            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(android.R.color.holo_green_light));
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(android.R.color.white));
        }
        holder.itemView.setOnClickListener(v -> onBranchClickListener.onBranchClick(branch));
    }

    @Override
    public int getItemCount() {
        return branches.size();
    }

    public interface OnBranchClickListener {
        void onBranchClick(String branch);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView branchTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            branchTextView = itemView.findViewById(R.id.branchTextView);
        }
    }
}
