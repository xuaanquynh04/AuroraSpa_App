package com.teamone.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide;
import com.bumptech.glide.Glide;
import com.teamone.auroraspa.R;
import com.teamone.models.Custom;
import com.teamone.models.Option;
import com.teamone.models.Product;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    Context context;
    int item_layout;
    ArrayList<String> banners;

    public BannerAdapter(Context context, int item_layout, ArrayList<String> banners) {
        this.context = context;
        this.item_layout = item_layout;
        this.banners = banners;
    }

    @NonNull
    @Override
    public BannerAdapter.BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(item_layout, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerAdapter.BannerViewHolder holder, int position) {
        String imgURL = banners.get(position);
        Glide.with(context).load(imgURL).into(holder.imvBanner);
    }

    @Override
    public int getItemCount() {
        return banners.size();
    }

    public void updateData(ArrayList<String> newBannerList) {
        banners.clear();
        banners.addAll(newBannerList);
        notifyDataSetChanged();
    }
    public class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imvBanner;
        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imvBanner = itemView.findViewById(R.id.imvBanner);
        }
    }

}
