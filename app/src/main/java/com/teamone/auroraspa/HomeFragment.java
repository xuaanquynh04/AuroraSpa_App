package com.teamone.auroraspa;

import android.icu.text.DateTimePatternGenerator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.teamone.adapters.BannerAdapter;
import com.teamone.adapters.FeedbackHomeAdapter;
import com.teamone.adapters.NewsAdapter;
import com.teamone.adapters.ProductHomeAdapter;
import com.teamone.auroraspa.databinding.FragmentHomeBinding;
import com.teamone.models.Feedback;
import com.teamone.models.News;
import com.teamone.models.Product;
import com.teamone.viewmodels.ShareData;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    BannerAdapter bannerAdapter;
    ArrayList<String> banners;
    FirebaseFirestore db;
    ShareData shareData;
    ProductHomeAdapter productAdapter;
    ArrayList<Product> products;
    ArrayList<Feedback> fbs;
    FeedbackHomeAdapter feedbackAdapter;
    FragmentHomeBinding binding;
    NewsAdapter newsAdapter;
    ArrayList<News> news;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).showOnlyNavBar(1);
        ((MainActivity) requireActivity()).binding.navBar1.clearSelection();
        ((MainActivity) requireActivity()).binding.navBar1.setTrangChuSelected();
        ((MainActivity) requireActivity()).showHeaderNormal();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        banners = new ArrayList<>();
        bannerAdapter = new BannerAdapter(requireActivity(), R.layout.item_banner_home, banners);
        //Load banners
        db.collection("banners").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<String> newBanners = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String imgURL = document.getString("imgURL");
                    if (imgURL!=null)
                        newBanners.add(imgURL);
                }
                bannerAdapter.updateData(newBanners);
                Log.d("load data home", "success");
            } else
                Log.d("load data home", "fail");
        });
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding.vpBanners.setAdapter(bannerAdapter);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager prodManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvProducts.setLayoutManager(prodManager);
        LinearLayoutManager fbManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvFeedbacks.setLayoutManager(fbManager);
        binding.rvNews.setLayoutManager(new GridLayoutManager(getContext(), 2));
        DisplayMetrics metrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int numItems = 3;
        int numItemsFb = 2;
        // Calculate item width with hardcoded spacing to create balanced gaps
        final float density = getResources().getDisplayMetrics().density;
        final int spacingInPx = (int) (4 * density); // 4dp for padding/margin
        int totalHorizontalSpacing = (2 * spacingInPx) + (numItems * 2 * spacingInPx); // RV's padding + all items' margin
        int itemWidth = (screenWidth - totalHorizontalSpacing) / numItems;
        int itemFbWidth = (screenWidth - totalHorizontalSpacing) / numItemsFb;
        products = new ArrayList<>();
        productAdapter = new ProductHomeAdapter(products, requireActivity(), R.layout.item_product_home, itemWidth);
        productAdapter.setOnItemClickListener(product -> {
            DetailFragment detailFragment = DetailFragment.newInstance(product);
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, detailFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        binding.rvProducts.setAdapter(productAdapter);
        fbs =  new ArrayList<>();
        feedbackAdapter = new FeedbackHomeAdapter(requireActivity(), R.layout.item_comment_home, fbs, itemFbWidth);
        binding.rvFeedbacks.setAdapter(feedbackAdapter);
        shareData = new ViewModelProvider(requireActivity()).get(ShareData.class);
        shareData.getIsLoadingProducts().observe(getViewLifecycleOwner(), isLoading ->{
            if (isLoading) {
                Log.d("Home fragmet", "product is loading");
            }
            else {
                Log.d("Home fragment", "loaded");
            }
        });
        shareData.get_newProducts().observe(getViewLifecycleOwner(), products1 -> {
            ArrayList<Product> newProducts = new ArrayList<>();
            ArrayList<Feedback> newFbs = new ArrayList<>();
            if (products1 != null) {
                Log.d("Home fragment", "Observer received " + products1.size() + " products.");
                for (Product p : products1) {
                    if (p.getNewProduct()) {
                        newProducts.add(p);
                        if (p.getFeedbacks() != null) {
                            for (Feedback f : p.getFeedbacks()) {
                                newFbs.add(f);
                                Log.d("Feedback", f.getCustomerID());
                            }
                        }
                    }
                }
                Log.d("Home fragment", "Filtered " + newProducts.size() + " new products.");
            } else {
                Log.d("Home fragment", "Observer received null product list.");
            }
            news = new ArrayList<>();
            newsAdapter = new NewsAdapter(requireActivity(), news, R.layout.item_news_home);
            binding.rvNews.setAdapter(newsAdapter);
            db.collection("news").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("load news", "success");
                    ArrayList<News> updatedNews = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        News n = document.toObject(News.class);
                        updatedNews.add(n);
                        Log.d("News", n.getTitle());
                    }
                    newsAdapter.updateData(updatedNews);
                }
            });
            feedbackAdapter.updateData(newFbs);
            productAdapter.updateData(newProducts);
            addEvents();
        });

    }

    private void addEvents() {
        binding.btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceListFrag("ALL");
            }
        });
        binding.btnNail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceListFrag("NAIL");
            }
        });
        binding.btnWash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceListFrag("WASH");
            }
        });
        binding.btnWax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceListFrag("WAX");
            }
        });
        binding.txtAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { replaceListFrag("ALL");}
        });
        binding.btnTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFrag("try");
            }
        });
        binding.btnRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFrag("rec");
            }
        });
    }

    private void replaceFrag(String s) {
        if (s.equals("try")) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            TryNailFragment tryNailFragment = TryNailFragment.newInstance("", "");
            fragmentTransaction.replace(R.id.container, tryNailFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        if (s.equals("rec")) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            RecNailFragment recNailFragment = RecNailFragment.newInstance("", "");
            fragmentTransaction.replace(R.id.container, recNailFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

    }

    public void replaceListFrag(String listName) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ListFragment listFrag = ListFragment.newInstance(listName);
        fragmentTransaction.replace(R.id.container, listFrag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}