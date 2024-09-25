package com.example.test;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemFragment extends Fragment {

    private static final String ARG_BRANCH = "branch";
    private static final String ARG_DELIVERY = "delivery";

    private String selectedBranch;
    private String selectedDelivery;
    private RecyclerView recyclerView;
    private List<Item> items;
    private Map<String, List<Item>> branchesByDelivery;

    public static ItemFragment newInstance(String delivery, String branch) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DELIVERY, delivery);
        args.putString(ARG_BRANCH, branch);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedDelivery = getArguments().getString(ARG_DELIVERY);
            selectedBranch = getArguments().getString(ARG_BRANCH);
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                branchesByDelivery = mainActivity.getBranchesByDelivery();
            }
            items = getItemsForBranch(selectedBranch);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        TextView deliveryNameTextView = view.findViewById(R.id.textViewDeliveryName);
        TextView branchNameTextView = view.findViewById(R.id.textViewBranchName);

        // Set the delivery and branch names
        deliveryNameTextView.setText(selectedDelivery);
        branchNameTextView.setText(selectedBranch);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (items != null && !items.isEmpty()) {
            ItemListAdapter adapter = new ItemListAdapter(items);
            recyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(getContext(), "No items available", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private List<Item> getItemsForBranch(String branch) {
        List<Item> itemsForBranch = new ArrayList<>();
        if (branchesByDelivery != null) {
            for (List<Item> itemList : branchesByDelivery.values()) {
                for (Item item : itemList) {
                    if (branch.equals(item.getBranch())) {
                        itemsForBranch.add(item);
                    }
                }
            }
        }
        return itemsForBranch;
    }
}
