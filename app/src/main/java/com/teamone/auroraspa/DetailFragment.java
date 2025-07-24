package com.teamone.auroraspa;

import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.teamone.adapters.FeedbackHomeAdapter;
import com.teamone.auroraspa.databinding.FragmentDetailBinding;
import com.teamone.auroraspa.databinding.FragmentListBinding;
import com.teamone.models.Feedback;
import com.teamone.models.Product;
import com.teamone.viewmodels.ShareData;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PROD_ID = "product";

    // TODO: Rename and change types of parameters
    private String productID;
    FragmentDetailBinding binding;
    ShareData shareData;
    Product p;
    ArrayList<Product> products;
    FeedbackHomeAdapter feedbackAdapter;
    ArrayList<Feedback> fbs;

    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(Product p) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(PROD_ID, p);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            p = (Product) getArguments().getSerializable(PROD_ID);
            fbs = p.getFeedbacks();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).showHeaderNormal();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) requireActivity()).showOnlyNavBar(1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailBinding.inflate(inflater, container, false);
        shareData = new ViewModelProvider(requireActivity()).get(ShareData.class);
        shareData.setCurrentProduct(p);
        binding.txtName.setText(p.getProductName());
        binding.txtID.setText("Mã dịch vụ: " + p.getProductID());
        DecimalFormat formatter = new DecimalFormat("#,###");
        DecimalFormat formatter2 = new DecimalFormat("#");
        binding.txtRealPrice.setText(formatter.format(p.getPrice()).replace(",", "."));
        binding.txtOldPrice.setText(formatter.format(p.getOldPrice()).replace(",", "."));
        binding.txtOldPrice.setPaintFlags(binding.txtOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        binding.txtUse.setText(formatter2.format(p.getProductUse()) + " lượt sử dụng");
        binding.txtSale.setText((String.format("%.0f", p.getSaleOff())) + "%");
        String productImage = p.getProductImage();
        if (productImage != null && !productImage.isEmpty()) {
            Glide.with(requireActivity()).load(productImage).placeholder(R.drawable.ic_launcher_foreground).error(R.drawable.ic_launcher_background).into(binding.imvThumb);
        } else
            binding.imvThumb.setImageResource(R.drawable.nail1);
        binding.txtDes.setText(p.getDescription());
        LinearLayoutManager fbManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.rvFeedbacks.setLayoutManager(fbManager);
        DisplayMetrics metrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int itemWidth = screenWidth - 10*2;
        feedbackAdapter = new FeedbackHomeAdapter(requireActivity(), R.layout.item_comment_home, fbs, itemWidth);
        binding.rvFeedbacks.setAdapter(feedbackAdapter);
        switchNavbar();
        addEvents();
        return binding.getRoot();
    }

    private void switchNavbar() {
        String type = p.getProductTypeID(); // hoặc p.getProductType() tùy vào class Product của bạn
        if (type != null) {
            if (type.equalsIgnoreCase("NAIL")) {
                ((MainActivity) requireActivity()).showOnlyNavBar(2); // hiển thị nav_bar2
            } else if (type.equalsIgnoreCase("WASH") || type.equalsIgnoreCase("WAX")) {
                ((MainActivity) requireActivity()).showOnlyNavBar(3); // hiển thị nav_bar3
            }
        }
    }

    private void addEvents() {
        binding.btnShowDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.txtDes.getVisibility() == View.GONE) {
                    binding.txtDes.setVisibility(View.VISIBLE);
                } else
                    binding.txtDes.setVisibility(View.GONE);
            }
        });
        binding.btnShowFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.rvFeedbacks.getVisibility() == View.GONE) {
                    binding.rvFeedbacks.setVisibility(View.VISIBLE);
                } else
                    binding.rvFeedbacks.setVisibility(View.GONE);
            }
        });
    }
}