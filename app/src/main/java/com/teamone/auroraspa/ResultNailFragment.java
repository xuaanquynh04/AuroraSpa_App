package com.teamone.auroraspa;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.teamone.auroraspa.databinding.FragmentRecNailBinding;
import com.teamone.auroraspa.databinding.FragmentResultNailBinding;
import com.teamone.models.Feedback;
import com.teamone.models.NailRec;
import com.teamone.models.Product;
import com.teamone.viewmodels.ShareData;

import java.util.ArrayList;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultNailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultNailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ShareData shareData;
    ArrayList<Product> products;
    FragmentResultNailBinding binding;
    FirebaseFirestore db;
    NailRec nailRec;
    Product randomProduct;

    public ResultNailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResultNailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResultNailFragment newInstance(String param1, String param2) {
        ResultNailFragment fragment = new ResultNailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = com.teamone.auroraspa.databinding.FragmentResultNailBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        loadData();
        Log.d("dong 85", "test");
        addEvents();
        return binding.getRoot();
    }

    private void addEvents() {
        binding.btnViewNailDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, DetailFragment.newInstance(randomProduct)) // Truyền ID hoặc Product object
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void getRec() {
        db.collection("nail_recs").whereEqualTo("productID", randomProduct.getProductID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    nailRec = document.toObject(NailRec.class);
                    if (nailRec != null) {
                        binding.txtNailName.setText(nailRec.getName());
                        binding.txtNailDescription.setText(nailRec.getDescription());
                        String productImage = randomProduct.getProductImage();
                        if (productImage != null && !productImage.isEmpty()) {
                            Glide.with(requireActivity()).load(productImage).placeholder(R.drawable.ic_launcher_foreground).error(R.drawable.ic_launcher_background).into(binding.imvNailPhoto);
                        } else
                            binding.imvNailPhoto.setImageResource(R.drawable.nail1);
                    }
            }
        }
        });
    }

    private void loadData() {
        shareData = new ViewModelProvider(requireActivity()).get(ShareData.class);
        shareData.getIsLoadingProducts().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                Log.d("Result fragmet", "product is loading");
            } else {
                Log.d("Result fragment", "loaded");
            }
        });
        shareData.get_newProducts().observe(getViewLifecycleOwner(), products1 -> {
            ArrayList<Product> newProducts = new ArrayList<>();
            if (products1!=null) {
                for (Product p : products1) {
                    if (p.getProductTypeID().equals("NAIL")) {
                        newProducts.add(p);
                        Log.d("Product name", p.getProductName());
                    }
                }
                products = newProducts;
                Log.d("num", String.valueOf(newProducts.size()));
                if (products != null && !products.isEmpty()) {
                    int randomIndex = new Random().nextInt(products.size());
                    randomProduct = products.get(randomIndex);
                    Log.d("Random product", randomProduct.toString());
                    getRec();
                } else {
                    Log.d("Random product", "null");
                }
            } else {
                Log.d("Result fragment", "Observer received null product list.");
            }
        });

    }
}