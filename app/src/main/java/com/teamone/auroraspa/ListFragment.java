package com.teamone.auroraspa;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.teamone.adapters.ProductSearchAdapter;
import com.teamone.auroraspa.databinding.FragmentListBinding;
import com.teamone.models.Product;
import com.teamone.viewmodels.ShareData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment implements ProductSearchAdapter.OnItemClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LIST_NAME = "listName";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String listName;
    private String mParam2;
    FragmentListBinding binding;
    ProductSearchAdapter productAdapter;
    ArrayList<Product> products;
    FirebaseDatabase db;
    ShareData shareData;
    private Double currentMinPrice = 0.0;
    private Double currentMaxPrice = 0.0;

    public ListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance(String listName) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(LIST_NAME, listName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listName = getArguments().getString(LIST_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(inflater, container, false);
        products = new ArrayList<>();
        binding.rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productAdapter = new ProductSearchAdapter(products, requireActivity(), R.layout.item_product_search, this);
        binding.rvProducts.setAdapter(productAdapter);
        shareData = new ViewModelProvider(requireActivity()).get(ShareData.class);
        shareData.getIsLoadingProducts().observe(getViewLifecycleOwner(), isLoading ->{
            if (isLoading) {
                Log.d("ListFragment", "product is loading");
            }
            else {
                Log.d("ListFragment", "loaded");
            }
        });
        shareData.get_newProducts().observe(getViewLifecycleOwner(), products1 -> {
            ArrayList<Product> newProducts = new ArrayList<>();
            if (products1 != null){
                Log.d("List name", listName);
                Log.d("Number of products", String.valueOf(products1.size()));
                if ("ALL".equals(listName)) {
                    for (Product p : products1) {
                        newProducts.add(p);
                        Log.d("Product name", p.getProductName());
                    }
                } else if (listName.equals("NAIL") || listName.equals("WASH") || listName.equals("WAX")){
                    Log.d("list name 2", listName);
                    for (Product p : products1) {
                        Log.d("product type", p.getProductTypeID());
                        if (p.getProductTypeID().equals(listName)) {
                            newProducts.add(p);
                            Log.d("Product " + listName, p.getProductName());
                        }
                    }
                } else if (!listName.isEmpty()){
                    for (Product p : products1) {
                        Log.d("theo keyword", p.getProductName());
                        if (p.getProductName().toLowerCase().contains(listName.toLowerCase())) {
                            newProducts.add(p);
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Vui lòng nhập từ khóa", Toast.LENGTH_SHORT).show();
                }
            }
            productAdapter.setRootProducts(newProducts);
            productAdapter.updateData(newProducts);
        });
        addEvents();

        return binding.getRoot();
    }



    private void addEvents() {
        binding.btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View popupView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_filter, null);

                final PopupWindow popupWindow = new PopupWindow(popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        true);

                popupWindow.showAsDropDown(binding.btnFilter, 0, 10);
                TextView txtAll = popupView.findViewById(R.id.txtAll);
                TextView txtPriceLessThan49 = popupView.findViewById(R.id.txtPriceLessThan49);
                TextView txtPrice50To99 = popupView.findViewById(R.id.txtPrice50To99);
                TextView txtPrice100To199 = popupView.findViewById(R.id.txtPrice100To199);
                TextView txtPriceGreaterThan200k = popupView.findViewById(R.id.txtPriceGreaterThan200k);
                txtAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentMinPrice = 0.0;
                        currentMaxPrice = 0.0;
                        loadDataFilter();
                        popupWindow.dismiss();
                    }
                });
                txtPriceLessThan49.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentMinPrice = 0.0;
                        currentMaxPrice = 49000.0;
                        loadDataFilter();
                        popupWindow.dismiss();
                    }
                });
                txtPrice50To99.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentMinPrice = 50000.0;
                        currentMaxPrice = 99000.0;
                        loadDataFilter();
                        popupWindow.dismiss();
                    }
                });
                txtPrice100To199.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentMinPrice = 100000.0;
                        currentMaxPrice = 199000.0;
                        loadDataFilter();
                        popupWindow.dismiss();
                    }
                });
                txtPriceGreaterThan200k.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentMinPrice = 200000.0;
                        currentMaxPrice = null;
                        loadDataFilter();
                        popupWindow.dismiss();
                    }
                });
            }
        });
        binding.btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View popupView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_sort, null);

                final PopupWindow popupWindow = new PopupWindow(popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        true);
                ArrayList<Product> currentProducts = productAdapter.getProducts();
                ArrayList<Product> sortedList = new ArrayList<>(currentProducts);
                popupWindow.showAsDropDown(binding.btnSort, 0, 10);
                TextView AZ = popupView.findViewById(R.id.AZ);
                TextView ZA = popupView.findViewById(R.id.ZA);
                TextView increase = popupView.findViewById(R.id.increase);
                TextView decrease = popupView.findViewById(R.id.decrease);
                AZ.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sortedList.sort((p1, p2) -> p1.getProductName().compareToIgnoreCase(p2.getProductName()));
                        productAdapter.updateData(sortedList);
                        popupWindow.dismiss();
                    }
                });
                ZA.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sortedList.sort((p1, p2) -> p2.getProductName().compareToIgnoreCase(p1.getProductName()));
                        productAdapter.updateData(sortedList);
                        popupWindow.dismiss();
                    }
                });
                increase.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sortedList.sort(Comparator.comparingDouble(Product::getPrice));
                        productAdapter.updateData(sortedList);
                        popupWindow.dismiss();
                    }
                });
                decrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sortedList.sort((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
                        productAdapter.updateData(sortedList);
                        popupWindow.dismiss();
                    }
                });
            }
        });
    }

    private void loadDataFilter() {
        ArrayList<Product> productsToFilter = productAdapter.getRootProducts();
        ArrayList<Product> newProducts = new ArrayList<>();
        if (currentMinPrice == 0.0 && currentMaxPrice == 0.0) {
            productAdapter.updateData(productsToFilter);
        } else if (currentMaxPrice!=null) {
            for (Product p : productsToFilter) {
                if (p.getPrice() >= currentMinPrice && p.getPrice() <= currentMaxPrice) {
                    newProducts.add(p);
                }
            }
            productAdapter.updateData(newProducts);
        } else {
            for (Product p : productsToFilter) {
                if (p.getPrice()  > currentMinPrice) {
                    newProducts.add(p);
                }
            }
            productAdapter.updateData(newProducts);
        }
    }

    @Override
    public void onItemClick(Product p) {
//        Toast.makeText(requireContext(), "Clicked on: " + p.getProductName(), Toast.LENGTH_SHORT).show();
//        Log.d("HomeFragment", "Product clicked: " + p.getProductName() + ", ID: " + p.getProductID());
         requireActivity().getSupportFragmentManager().beginTransaction()
            .replace(R.id.container, DetailFragment.newInstance(p)) // Truyền ID hoặc Product object
            .addToBackStack(null)
            .commit();
    }
}