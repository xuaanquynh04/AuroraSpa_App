package com.teamone.auroraspa;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.teamone.adapters.ProductSearchAdapter;
import com.teamone.adapters.SuggestSearchAdapter;
import com.teamone.auroraspa.databinding.ActivityMainBinding;
import com.teamone.interfaces.OpenCustomize;
import com.teamone.models.Custom;
import com.teamone.models.Product;
import com.teamone.viewmodels.ShareData;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OpenCustomize {
    ActivityMainBinding binding;
    ProductSearchAdapter adapter;
    FirebaseFirestore db;
    ShareData shareData;
    ArrayList<String> productNames;
    SuggestSearchAdapter suggestAdapter;
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.navBar2.setOpenCustomize(this);
        binding.navBar3.setOpenCustomize(this);
        binding.navBar2.setOpenTryNow(() -> {
            // Mở fragment Thử ngay
            TryNailFragment fragment = new TryNailFragment();
            Bundle bundle = new Bundle();
            Product currentProduct = shareData.getCurrentProduct().getValue();
            Log.d("main try", currentProduct.getTryImage());
            bundle.putString("TRY_IMAGE", currentProduct.getTryImage());
            fragment.setArguments(bundle);
            this.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        binding.navBar1.setOnNavBar1ItemSelected(new nav_bar1.OnNavBar1ItemSelected() {
            @Override
            public void onTrangChuClicked() {
                replaceFragWithBackstack(new HomeFragment());
            }

            @Override
            public void onLichHenClicked() {
                if (isUserLoggedIn()) {
                    replaceFragWithBackstack(new BookHisFragment());
                } else {
                    Toast.makeText(MainActivity.this, "Vui lòng đăng nhập để xem lịch hẹn", Toast.LENGTH_SHORT).show();
                    replaceFragWithBackstack(new SignInFragment());
                }
            }

            @Override
            public void onTaiKhoanClicked() {
                if (isUserLoggedIn()) {
                    replaceFragWithBackstack(new MyAccFragment());
                } else {
                    Toast.makeText(MainActivity.this, "Vui lòng đăng nhập để xem thông tin tài khoản", Toast.LENGTH_SHORT).show();
                    replaceFragWithBackstack(new SignInFragment());
                }
            }
        });

        db = FirebaseFirestore.getInstance();
        shareData = new ViewModelProvider(this).get(ShareData.class);
        loadProducts();
        loadCustoms();
        addEvents();
        Log.d("truoc adapter", "onCreate: ");
        createSuggestAdapter();
        replaceFrag(new HomeFragment());
    }
    private boolean isUserLoggedIn() {
        FirebaseUser customer = FirebaseAuth.getInstance().getCurrentUser();
        return customer != null;
    }

    private void loadCustoms() {
        shareData.setIsLoadingCustoms(true);
        db.collection("custom").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Custom> customs = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Custom c = doc.toObject(Custom.class);
                        customs.add(c);
                    }
                    shareData.setCustoms(customs);
                    Log.d("Main - customs loaded", String.valueOf(customs.size()));
                } else
                    Log.e("Main - error", String.valueOf(task.getException()));
                shareData.setIsLoadingCustoms(false);
            }
        });
    }

    private void loadProducts() {
        shareData.setIsLoadingProducts(true);
        db.collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Product> products = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Product p = doc.toObject(Product.class);
                        products.add(p);
                    }
                    shareData.set_newProducts(products);
                    Log.e("Main = prod - loaded", String.valueOf(products.size()));
                } else
                    Log.e("Main - prod - failed", String.valueOf(task.getException()));
                shareData.setIsLoadingProducts(false);
            }
        });
    }
    private void createSuggestAdapter() {
        // Initialize with empty list first
        productNames = new ArrayList<>();
        suggestAdapter = new SuggestSearchAdapter(this, productNames);
        binding.headerNormal.txtInput.setAdapter(suggestAdapter);
        binding.headerNormal.txtInput.setThreshold(1);
        
        // Observe LiveData and update adapter when data changes
        shareData.get_newProducts().observe(this, products -> {
            productNames.clear();
            for (Product p : products) {
                productNames.add(p.getProductName());
            }
            suggestAdapter.notifyDataSetChanged();
            Log.d("SuggestAdapter", "Updated with " + productNames.size() + " products");
        });
        binding.headerNormal.txtInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedName = (String) parent.getItemAtPosition(position);
                for (Product p : shareData.get_newProducts().getValue()) {
                    if (p.getProductName().equals(selectedName)) {
                        DetailFragment detailFragment = DetailFragment.newInstance(p);
                        MainActivity.this.getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, detailFragment)
                                .addToBackStack(null)
                                .commit();
                        break;
                    }
                }
            }
        });
    }

    public void replaceFrag(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
    public void replaceFragClearBackStack(Fragment fragment) {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
    public void replaceFragWithBackstack(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }
    public void showOnlyNavBar(int barNumber) {
        binding.navBar1.setVisibility(View.GONE);
        binding.navBar2.setVisibility(View.GONE);
        binding.navBar3.setVisibility(View.GONE);

        switch (barNumber) {
            case 1:
                binding.navBar1.setVisibility(View.VISIBLE);
                break;
            case 2:
                binding.navBar2.setVisibility(View.VISIBLE);
                break;
            case 3:
                binding.navBar3.setVisibility(View.VISIBLE);
                break;
        }
    }
    public void showHeaderCart(String title) {
        binding.headerCart.getRoot().setVisibility(View.VISIBLE);
        binding.headerCart.btnCart.setVisibility(View.VISIBLE);
        binding.headerNormal.getRoot().setVisibility(View.GONE);
        binding.headerCart.txtTitle.setText(title);
    }
    public void showHeaderNormal() {
        binding.headerCart.getRoot().setVisibility(View.GONE);
        binding.headerNormal.getRoot().setVisibility(View.VISIBLE);
    }

    private void addEvents() {
//        binding.headerNormal.btnSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "Search", Toast.LENGTH_SHORT).show();
//            }
//        });
        binding.headerNormal.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.this.getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    MainActivity.this.getSupportFragmentManager().popBackStack();
                } else {
                    MainActivity.this.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, new HomeFragment())
                            .commit();
                }
            }
        });
        binding.headerCart.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.this.getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    MainActivity.this.getSupportFragmentManager().popBackStack();
                } else {
                    MainActivity.this.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, new HomeFragment())
                            .commit();
                }
            }
        });

        binding.headerNormal.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        binding.headerNormal.btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserLoggedIn()) {
                    replaceFragWithBackstack(new CartFragment());
                } else {
                    Toast.makeText(MainActivity.this, "Vui lòng đăng nhập để xem thông tin tài khoản", Toast.LENGTH_SHORT).show();
                    replaceFragWithBackstack(new SignInFragment());
                }
            }
        });
        binding.headerCart.btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserLoggedIn()) {
                    replaceFragWithBackstack(new CartFragment());
                } else {
                    Toast.makeText(MainActivity.this, "Vui lòng đăng nhập để xem thông tin tài khoản", Toast.LENGTH_SHORT).show();
                    replaceFragWithBackstack(new SignInFragment());
                }
            }
        });
        binding.headerNormal.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                ListFragment listFrag = ListFragment.newInstance(binding.headerNormal.txtInput.getText().toString());
                fragmentTransaction.replace(R.id.container, listFrag);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

    }

    @Override
    public void onAddToCartClicked() {
        Product currentProduct = shareData.getCurrentProduct().getValue();
        if (currentProduct != null) {
            CustomizeFragment bottomSheet = CustomizeFragment.newInstance(currentProduct, "addtocart", null);
            getSupportFragmentManager().executePendingTransactions();
            bottomSheet.setDismissListener(() -> {
                binding.navBar2.clearSelection();
                binding.navBar3.clearSelection();
            });
            bottomSheet.show(getSupportFragmentManager(), "CustomizeFragment");
        }
    }

    @Override
    public void onBookNowClicked() {
        Product currentProduct = shareData.getCurrentProduct().getValue();
        if (currentProduct != null) {
            CustomizeFragment bottomSheet = CustomizeFragment.newInstance(currentProduct, "booknow", null);
            getSupportFragmentManager().executePendingTransactions();
            bottomSheet.setDismissListener(() -> {
                binding.navBar2.clearSelection();
                binding.navBar3.clearSelection();
            });
            bottomSheet.show(getSupportFragmentManager(), "CustomizeFragment");
        }
    }
}