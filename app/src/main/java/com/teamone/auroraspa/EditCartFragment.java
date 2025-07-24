package com.teamone.auroraspa;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.teamone.adapters.CustomAdapter;
import com.teamone.auroraspa.databinding.FragmentEditCartBinding;
import com.teamone.models.CartItem;
import com.teamone.models.Custom;
import com.teamone.models.Option;
import com.teamone.models.Product;
import com.teamone.viewmodels.ShareData;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditCartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditCartFragment extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CART_ITEM = "cart_item";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    CartItem cartItem;
    FragmentEditCartBinding binding;
    CustomAdapter customAdapter;
    FirebaseFirestore db;
    ArrayList<Custom> customs = new ArrayList<>();
    ShareData shareData;
    Product p;

    public EditCartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EditCartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditCartFragment newInstance(CartItem cartItem) {
        EditCartFragment fragment = new EditCartFragment();
        Bundle args = new Bundle();
        args.putSerializable(CART_ITEM, cartItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cartItem = (CartItem) getArguments().getSerializable(CART_ITEM);
            p = getProduct(cartItem.getProductID());}
        db = FirebaseFirestore.getInstance();
    }

    private Product getProduct(String productID) {
        AtomicReference<Product> newProduct = new AtomicReference<>(new Product());
        shareData = new ViewModelProvider(requireActivity()).get(ShareData.class);
        shareData.getIsLoadingProducts().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                Log.d("EditCartFragment", "product is loading");
            }
            else {
                Log.d("EditcartFragment", "loaded");
            }
        });
        shareData.get_newProducts().observe(getViewLifecycleOwner(), customs1 -> {
            if (customs1 != null){
                for (Product p : customs1) {
                    if (p.getProductID().equals(productID)) {
                        newProduct.set(p);
                    }
                }
            }
        });
        return newProduct.get();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditCartBinding.inflate(inflater, container, false);
        updateUI();
        addEvents();
        return binding.getRoot();
    }

    private void addEvents() {
        String customerID = "123";
        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!customAdapter.areRequiredOptionsSelected()) {
                    Toast.makeText(requireContext(), "Vui lòng chọn các phần bắt buộc", Toast.LENGTH_SHORT).show();
                }
                else {
                    Double totalItemPrice = customAdapter.getTotalItemPrice();
                    ArrayList<Option> selectedOptions = customAdapter.getSelectedOptions();
                    CartItem newCartItem = new CartItem(p.getProductName(), p.getProductID(), p.getProductImage(), p.getPrice(), totalItemPrice, customerID, selectedOptions, p.getProductTypeID(), Timestamp.now(), 1);
                    
                }
            }
        });
    }

    private void updateUI() {
        customs = new ArrayList<>();
        binding.rvCustoms.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        customAdapter = new CustomAdapter(customs, requireContext(), p, null);
        customAdapter.setOnTotalPriceChangeListener(total -> {
            binding.totalPrice.setText(String.format("%,.0f", total));
        });
        binding.rvCustoms.setAdapter(customAdapter);
        binding.totalPrice.setText(String.format("%,.0f", p.getPrice()));
        shareData = new ViewModelProvider(requireActivity()).get(ShareData.class);
        shareData.getIsLoadingCustoms().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                Log.d("ListFragment", "product is loading");
            }
            else {
                Log.d("ListFragment", "loaded");
            }
        });
        shareData.getCustoms().observe(getViewLifecycleOwner(), customs1 -> {
            ArrayList<Custom> newCustoms = new ArrayList<>();

            if (p == null || p.getProductTypeID() == null) {
                Log.e("CustomizeFragment", "Product or ProductTypeID is null, cannot filter options.");
                return;
            }

            if (customs1 != null) {
                for (Custom c : customs1) {
                    if (c != null && c.getProductTypeID() != null && c.getProductTypeID().equals(p.getProductTypeID())) {
                        Log.d("custom", c.getGroupName());
                        newCustoms.add(c);
                    }
                }
            }
            customAdapter.updateData(newCustoms);
        });
    }
}