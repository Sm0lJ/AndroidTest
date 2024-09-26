package com.example.test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.BranchViewHolder> {
    private List<Item> branches;
    private OnBranchClickListener listener;

    public BranchAdapter(List<Item> branches, OnBranchClickListener listener) {
        this.branches = branches;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BranchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new BranchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BranchViewHolder holder, int position) {
        Item branch = branches.get(position);
        holder.bind(branch, listener);
    }

    @Override
    public int getItemCount() {
        return branches.size();
    }

    public void updateBranches(List<Item> newBranches) {
        this.branches = newBranches;
        notifyDataSetChanged();
    }

    public interface OnBranchClickListener {
        void onBranchClick(Item branch);
    }

    class BranchViewHolder extends RecyclerView.ViewHolder {
        private TextView branchTextView;

        public BranchViewHolder(View itemView) {
            super(itemView);
            branchTextView = itemView.findViewById(android.R.id.text1);
        }

        public void bind(final Item branch, final OnBranchClickListener listener) {
            branchTextView.setText(branch.getBranch());
            itemView.setOnClickListener(v -> listener.onBranchClick(branch));
        }
    }
}
