package com.teamone.auroraspa;

import android.content.DialogInterface;
import android.graphics.Path;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.Firebase;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.teamone.adapters.CustomAdapter;
import com.teamone.auroraspa.databinding.FragmentCustomizeBinding;
import com.teamone.auroraspa.databinding.FragmentListBinding;
import com.teamone.interfaces.DismissCustomize;
import com.teamone.models.CartItem;
import com.teamone.models.Custom;
import com.teamone.models.Option;
import com.teamone.models.Product;
import com.teamone.viewmodels.ShareData;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomizeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomizeFragment extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PRODUCT = "product";
    private static final String BUTTON = "button";
    public static final  String CART_ITEM = "cartItem";
    private DismissCustomize dismissListener;

    // TODO: Rename and change types of parameters
    ShareData shareData;
    ArrayList<Custom> customs = new ArrayList<>();
    FragmentCustomizeBinding binding;
    Product p;
    CustomAdapter customAdapter;
    String button;
    CartItem cartItemUpdate;
    String customerID = null;
    FirebaseFirestore db;
    FirebaseUser customer;

    public CustomizeFragment() {
        // Required empty public constructor
    }

    public static CustomizeFragment newInstance(Product p, String button, @Nullable CartItem cartItemUpdate) {
        CustomizeFragment fragment = new CustomizeFragment();
        Bundle args = new Bundle();
        args.putSerializable(PRODUCT, p);
        args.putString(BUTTON, button);
        if (cartItemUpdate != null) {
            args.putSerializable(CART_ITEM, cartItemUpdate);
            Log.d("cuz frag dong 81", String.valueOf(cartItemUpdate));
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            p = (Product) getArguments().getSerializable(PRODUCT);
            button = (String) getArguments().get(BUTTON);
            customer = FirebaseAuth.getInstance().getCurrentUser();
            if (customer != null) {
                customerID = customer.getUid();
            }
            if (getArguments().containsKey(CART_ITEM)) {
                cartItemUpdate = (CartItem) getArguments().getSerializable(CART_ITEM);
                Log.d("cuz frag dong 99", String.valueOf(cartItemUpdate));
            } else {

            }
        }
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCustomizeBinding.inflate(inflater, container, false);
        if (button.equals("addtocart")) {
            binding.btnAddToCart.setVisibility(View.VISIBLE);
            binding.btnBookNow.setVisibility(View.GONE);
            binding.btnUpdateCustom.setVisibility(View.GONE);
        } else if (button.equals("booknow")) {
            binding.btnBookNow.setVisibility(View.VISIBLE);
            binding.btnAddToCart.setVisibility(View.GONE);
            binding.btnUpdateCustom.setVisibility(View.GONE);
        } else {
            binding.btnBookNow.setVisibility(View.GONE);
            binding.btnAddToCart.setVisibility(View.GONE);
            binding.btnUpdateCustom.setVisibility(View.VISIBLE);
        }

        customs = new ArrayList<>();
        binding.rvCustoms.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        if (cartItemUpdate!=null) {
            customAdapter = new CustomAdapter(customs, requireContext(), p, cartItemUpdate.getSelectedOptions());
            Log.d("dong 134", "cartupdate not null");
        } else {
            Log.d("dong 136", "cartupdate null");
            customAdapter = new CustomAdapter(customs, requireContext(), p, null);
        }
        customAdapter.setOnTotalPriceChangeListener(total -> {
            binding.totalPrice.setText(String.format("%,.0f", total).replace(",", "."));
        });
        binding.rvCustoms.setAdapter(customAdapter);
        binding.totalPrice.setText(String.format("%,.0f", customAdapter.getTotalItemPrice()).replace(",", "."));
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
                        Log.d("custom name", c.getGroupName());
                        newCustoms.add(c);
                    }
                }
            }
            customAdapter.updateData(newCustoms);
        });
        addEvents();


        return binding.getRoot();
    }

    private void addEvents() {
        binding.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customerID==null){
                    Toast.makeText(requireContext(), "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                } else if (!customAdapter.areRequiredOptionsSelected()) {
                    Toast.makeText(requireContext(), "Vui lòng chọn các phần bắt buộc", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("bat buoc done", "da chon bb");
                    Double totalItemPrice = customAdapter.getTotalItemPrice();
                    ArrayList<Option> selectedOptions = customAdapter.getSelectedOptions();
                    Log.d("dong 155", totalItemPrice.toString());
                    CartItem newCartItem = new CartItem(p.getProductName(), p.getProductID(), p.getProductImage(), p.getPrice(), totalItemPrice, customerID, selectedOptions, p.getProductTypeID(), Timestamp.now(), 1);
                    Log.d("dong 158", String.valueOf(newCartItem));
                    customerID = customer.getUid();
                    dupCheckAddNew(newCartItem);
                }
            }
        });

        binding.btnBookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customerID==null){
                    Toast.makeText(requireContext(), "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                } else if (!customAdapter.areRequiredOptionsSelected()) {
                    Toast.makeText(requireContext(), "Vui lòng chọn các phần bắt buộc", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("bat buoc done", "da chon bb");
                    Double totalItemPrice = customAdapter.getTotalItemPrice();
                    ArrayList<Option> selectedOptions = customAdapter.getSelectedOptions();
                    Log.d("dong 155", totalItemPrice.toString());
                    CartItem newCartItem = new CartItem(p.getProductName(), p.getProductID(), p.getProductImage(), p.getPrice(), totalItemPrice, customerID, selectedOptions, p.getProductTypeID(), Timestamp.now(), 1);
                    ArrayList<CartItem> cartItems = new ArrayList<>();
                    cartItems.add(newCartItem);
                    if (newCartItem != null) {
                        BookFragment bookFragment = new BookFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("selectedCartItems", cartItems);
                        bookFragment.setArguments(bundle);
                        dismiss();
                        Log.d("dong 233", String.valueOf(newCartItem));
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, bookFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            }
        });
        binding.btnUpdateCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customerID==null){
                    Toast.makeText(requireContext(), "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                } else if (!customAdapter.areRequiredOptionsSelected()) {
                    customAdapter.notifyDataSetChanged();
                    Toast.makeText(requireContext(), "Vui lòng chọn các phần bắt buộc", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("bat buoc done", "da chon bb");
                    Double totalItemPrice = customAdapter.getTotalItemPrice();
                    ArrayList<Option> selectedOptions = customAdapter.getSelectedOptions();
                    Log.d("dong 155", totalItemPrice.toString());
                    CartItem newCartItem = new CartItem(p.getProductName(), p.getProductID(), p.getProductImage(), p.getPrice(), totalItemPrice, customerID, selectedOptions, p.getProductTypeID(), Timestamp.now(), 1);
                    Log.d("dong 158", String.valueOf(newCartItem));
                    customerID = customer.getUid();
                    dupCheckUpdate(newCartItem);
                }
            }
        });
    }

    private void dupCheckUpdate(CartItem newCartItem) {
        db.collection("cartitem").whereEqualTo("customerID", customerID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean Dup = false;
                        ArrayList<Boolean> isDuplicate = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            CartItem c = doc.toObject(CartItem.class);
                            Log.d("username", c.getCustomerID());
                            if (c.getProductID().equals(newCartItem.getProductID())){
                                Log.d("ten product", c.getProductName());
                                Log.d("c size", String.valueOf(c.getSelectedOptions().size()));
                                Log.d("new size", String.valueOf(newCartItem.getSelectedOptions().size()));
                                if (c.getSelectedOptions().size() == newCartItem.getSelectedOptions().size()) {
                                    Log.d("cung size", String.valueOf(c.getSelectedOptions().size()));
                                    if (c.getSelectedOptions().containsAll(newCartItem.getSelectedOptions())) {
                                        isDuplicate.add(true);
                                        for (Option o2 : c.getSelectedOptions()) {
                                            for (Option o3 : newCartItem.getSelectedOptions()){
                                                Log.d("có trùng", "trùng");
                                                Log.d("o2 c", o2.getName());
                                                Log.d("o3 c", o3.getName());
                                            }
                                        }
                                    } else {isDuplicate.add(false);
                                        Log.d("cung size", "khong trung option");
                                        for (Option o2 : c.getSelectedOptions()) {
                                            for (Option o3 : newCartItem.getSelectedOptions()){
                                                Log.d("o2 c", o2.getName());
                                                Log.d("o3 c", o3.getName());
                                            }
                                        }
                                    }
                                } else {
                                    isDuplicate.add(false);
                                }

                            } else isDuplicate.add(false);
                        }
                        for (Boolean b : isDuplicate) {
                            if (b == true) {
                                Dup = true;
                                break;
                            }
                        }

                        if (Dup) {
                            Toast.makeText(requireContext(), "Dịch vụ đã được thêm vào giỏ hàng trước đó", Toast.LENGTH_SHORT).show();
                        } else {
                            String docId = cartItemUpdate.getId();
                            updateCartItemCustom(docId, newCartItem);
                        }
                    } else {
                        Log.d("loi load cart 1", "unsuccessful");
                    }
                }).toString();
    }

    private void updateCart(CartItem newCartItem) {
        db.collection("cartitem").add(newCartItem).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Log.d("Add to cart", "success");
                    dismiss();
                }
                else
                    Log.d("add to cart", "failed");
            }
        });
    }
    private void updateCartItemCustom(String docId, CartItem updatedItem) {
        db.collection("cartitem").document(docId)
                .set(updatedItem)
                .addOnSuccessListener(aVoid -> {
                    Log.d("UpdateCart", "Cập nhật thành công");
                    Toast.makeText(requireContext(), "Đã cập nhật dịch vụ", Toast.LENGTH_SHORT).show();
                    dismiss(); // Dismiss bottom sheet
                })
                .addOnFailureListener(e -> {
                    Log.e("UpdateCart", "Lỗi khi cập nhật", e);
                    Toast.makeText(requireContext(), "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show();
                });
    }
    public void setDismissListener(DismissCustomize listener) {
        this.dismissListener = listener;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissListener != null) {
            dismissListener.onCustomizeDismiss();
        }
    }
    public void dupCheckAddNew(CartItem newCartItem) {
        db.collection("cartitem").whereEqualTo("customerID", customerID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean Dup = false;
                        ArrayList<Boolean> isDuplicate = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            CartItem c = doc.toObject(CartItem.class);
                            Log.d("username", c.getCustomerID());
                            if (c.getProductID().equals(newCartItem.getProductID())){
                                Log.d("ten product", c.getProductName());
                                Log.d("c size", String.valueOf(c.getSelectedOptions().size()));
                                Log.d("new size", String.valueOf(newCartItem.getSelectedOptions().size()));
                                if (c.getSelectedOptions().size() == newCartItem.getSelectedOptions().size()) {
                                    Log.d("cung size", String.valueOf(c.getSelectedOptions().size()));
                                    if (c.getSelectedOptions().containsAll(newCartItem.getSelectedOptions())) {
                                        isDuplicate.add(true);
                                        for (Option o2 : c.getSelectedOptions()) {
                                            for (Option o3 : newCartItem.getSelectedOptions()){
                                                Log.d("có trùng", "trùng");
                                                Log.d("o2 c", o2.getName());
                                                Log.d("o3 c", o3.getName());
                                            }
                                        }
                                    } else {isDuplicate.add(false);
                                        Log.d("cung size", "khong trung option");
                                        for (Option o2 : c.getSelectedOptions()) {
                                            for (Option o3 : newCartItem.getSelectedOptions()){
                                                Log.d("o2 c", o2.getName());
                                                Log.d("o3 c", o3.getName());
                                            }
                                        }
                                    }
                                } else {
                                    isDuplicate.add(false);
                                }

                            } else isDuplicate.add(false);
                        }
                        for (Boolean b : isDuplicate) {
                            if (b == true) {
                                Dup = true;
                                break;
                            }
                        }

                        if (Dup) {
                            Toast.makeText(requireContext(), "Dịch vụ đã được thêm vào giỏ hàng trước đó", Toast.LENGTH_SHORT).show();
                        } else {
                            updateCart(newCartItem);
                        }
                    } else {
                        Log.d("loi load cart 1", "unsuccessful");
                    }
                }).toString();
    }

}