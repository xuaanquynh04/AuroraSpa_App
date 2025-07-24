package com.teamone.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.teamone.auroraspa.R;
import com.teamone.models.Product;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ProductSearchAdapter extends RecyclerView.Adapter<ProductSearchAdapter.ProductViewHolder>{
    ArrayList<Product> products;
    Context context;
    int item_layout;
    ArrayList<Product> rootProducts = new ArrayList<>();
    private OnItemClickListener listener;

    public ProductSearchAdapter(ArrayList<Product> products, Context context, int item_layout, OnItemClickListener listener) {
        this.products = products;
        this.context = context;
        this.item_layout = item_layout;
        this.listener = listener;
    }
    @NonNull
    @Override
    public ProductSearchAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(item_layout, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductSearchAdapter.ProductViewHolder holder, int position) {
        Product p = products.get(position);
        String productImage = p.getProductImage();
        if (productImage != null && !productImage.isEmpty()) {
            Glide.with(context).load(productImage).placeholder(R.drawable.ic_launcher_foreground).error(R.drawable.ic_launcher_background).into(holder.imvThumb);
        } else
            holder.imvThumb.setImageResource(R.drawable.nail1);
        DecimalFormat formatter = new DecimalFormat("#,###");
        holder.txtName.setText(p.getProductName());
        holder.txtRealPrice.setText(formatter.format(p.getPrice()).replace(",", "."));
        holder.txtOldPrice.setText(formatter.format(p.getOldPrice()).replace(",", "."));
        holder.txtOldPrice.setPaintFlags(holder.txtOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.txtSale.setText((String.format("%.0f", p.getSaleOff())) + "%");
        holder.txtRate.setText(String.valueOf(p.getRate()));
        holder.txtUse.setText((String.format("%.0f", p.getProductUse())) + " lượt sử dụng");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(p);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public ArrayList<Product> getRootProducts() {
        return rootProducts;
    }

    public void setRootProducts(ArrayList<Product> rootProducts) {
        this.rootProducts = rootProducts;
    }

    public void updateData(ArrayList<Product> newProductList) {
        products.clear();
        products.addAll(newProductList);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Product p);
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imvThumb;
        TextView txtName;
        TextView txtRealPrice;
        TextView txtOldPrice;
        TextView txtSale;
        TextView txtRate;
        TextView txtUse;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imvThumb = itemView.findViewById(R.id.imvThumb);
            txtName = itemView.findViewById(R.id.txtName);
            txtRealPrice = itemView.findViewById(R.id.txtRealPrice);
            txtOldPrice = itemView.findViewById(R.id.txtOldPrice);
            txtSale = itemView.findViewById(R.id.txtSale);
            txtRate = itemView.findViewById(R.id.txtRate);
            txtUse = itemView.findViewById(R.id.txtUse);
        }
    }
}
