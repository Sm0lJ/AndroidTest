package com.example.test;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryFragment extends Fragment {

    private static final String ARG_BRANCHES_BY_DELIVERY = "branchesByDelivery";
    private Map<String, List<Item>> branchesByDelivery = new HashMap<>();
    private RecyclerView recyclerView;

    public static DeliveryFragment newInstance(Map<String, List<Item>> branchesByDelivery) {
        DeliveryFragment fragment = new DeliveryFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BRANCHES_BY_DELIVERY, new HashMap<>(branchesByDelivery));
        fragment.setArguments(args);
        return fragment;
    }
    private OnScannerButtonClickListener scannerButtonClickListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            scannerButtonClickListener = (OnScannerButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnScannerButtonClickListener");
        }}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        if (getArguments() != null) {
            branchesByDelivery = (Map<String, List<Item>>) getArguments().getSerializable(ARG_BRANCHES_BY_DELIVERY);
        }

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (branchesByDelivery != null) {
            List<String> deliveries = new ArrayList<>(branchesByDelivery.keySet());

            // Prepare delivery status
            Map<String, Boolean> deliveryStatus = prepareDeliveryStatus();

            // Set adapter
            DeliveryListAdapter adapter = new DeliveryListAdapter(deliveries, deliveryStatus, this::onDeliverySelected);
            recyclerView.setAdapter(adapter);
        } else {
            // Handle case when branchesByDelivery is null
            Toast.makeText(getContext(), "No deliveries available", Toast.LENGTH_SHORT).show();
        }
        Button btnScanner = view.findViewById(R.id.buttonScanner);
        btnScanner.setOnClickListener(v -> {
            // Notify the activity that the scanner button was clicked
            scannerButtonClickListener.onScanButtonClick();
        });

        return view;
    }

    private Map<String, Boolean> prepareDeliveryStatus() {
        Map<String, Boolean> deliveryStatus = new HashMap<>();
        for (String delivery : branchesByDelivery.keySet()) {
            boolean allBranchesLoaded = areAllBranchesLoaded(delivery);
            deliveryStatus.put(delivery, allBranchesLoaded);
        }
        return deliveryStatus;
    }

    private boolean areAllBranchesLoaded(String delivery) {
        List<Item> itemsForDelivery = branchesByDelivery.getOrDefault(delivery, new ArrayList<>());
        Map<String, Boolean> branchStatus = new HashMap<>();
        for (Item item : itemsForDelivery) {
            String branch = item.getBranch();
            branchStatus.put(branch, branchStatus.getOrDefault(branch, true) && item.isLoaded());
        }
        // Check if all branches are loaded
        for (boolean status : branchStatus.values()) {
            if (!status) {
                return false;
            }
        }
        return true;
    }

    private void onDeliverySelected(String delivery) {
        Toast.makeText(getContext(), "Selected delivery: " + delivery, Toast.LENGTH_SHORT).show();

        // Load BranchFragment with the selected delivery and branchesByDelivery
        BranchFragment branchFragment = BranchFragment.newInstance(delivery, branchesByDelivery);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, branchFragment)
                .addToBackStack(null)
                .commit();
    }
}
