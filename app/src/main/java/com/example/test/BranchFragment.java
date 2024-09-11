package com.example.test;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class BranchFragment extends Fragment {

    private static final String ARG_DELIVERY = "delivery";
    private static final String ARG_BRANCHES_BY_DELIVERY = "branchesByDelivery";
    private String selectedDelivery;
    private RecyclerView recyclerView;
    private List<String> branches; // Populate based on selectedDelivery
    private Map<String, List<Item>> branchesByDelivery;

    public static BranchFragment newInstance(String delivery, Map<String, List<Item>> branchesByDelivery) {
        BranchFragment fragment = new BranchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DELIVERY, delivery);
        args.putSerializable(ARG_BRANCHES_BY_DELIVERY, new HashMap<>(branchesByDelivery));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedDelivery = getArguments().getString(ARG_DELIVERY);
            branchesByDelivery = (Map<String, List<Item>>) getArguments().getSerializable(ARG_BRANCHES_BY_DELIVERY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch branches for the selected delivery
        branches = getBranchesForDelivery(selectedDelivery);

        // Prepare branch status map
        Map<String, Boolean> branchStatus = prepareBranchStatus();

        if (branches != null && !branches.isEmpty()) {
            BranchListAdapter adapter = new BranchListAdapter(branches, branchStatus, this::onBranchSelected);
            recyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(getContext(), "No branches available", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private List<String> getBranchesForDelivery(String delivery) {
        List<String> branches = new ArrayList<>();
        if (branchesByDelivery != null && branchesByDelivery.containsKey(delivery)) {
            for (Item item : branchesByDelivery.get(delivery)) {
                String branch = item.getBranch();
                if (!branches.contains(branch)) {
                    branches.add(branch);
                }
            }
        }
        return branches;
    }

    private Map<String, Boolean> prepareBranchStatus() {
        Map<String, Boolean> branchStatus = new HashMap<>();
        for (String branch : branches) {
            boolean allItemsLoaded = areAllItemsLoaded(branch);
            branchStatus.put(branch, allItemsLoaded);
        }
        return branchStatus;
    }

    private boolean areAllItemsLoaded(String branch) {
        List<Item> itemsForBranch = branchesByDelivery.getOrDefault(selectedDelivery, new ArrayList<>());
        for (Item item : itemsForBranch) {
            if (branch.equals(item.getBranch()) && !item.isLoaded()) {
                return false;
            }
        }
        return true;
    }

    private void onBranchSelected(String branch) {
        Toast.makeText(getContext(), "Selected branch: " + branch, Toast.LENGTH_SHORT).show();
        ItemFragment itemFragment = ItemFragment.newInstance(selectedDelivery, branch);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, itemFragment)
                .addToBackStack(null)
                .commit();
    }
}
