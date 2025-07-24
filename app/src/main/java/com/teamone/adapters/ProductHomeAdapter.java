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

public class ProductHomeAdapter extends RecyclerView.Adapter<ProductHomeAdapter.ProductViewHolder>{
    ArrayList<Product> products;
    Context context;
    int item_layout;
    int itemWidth = 0;
    private OnItemClickListener listener;

    public ProductHomeAdapter(ArrayList<Product> products, Context context, int item_layout, int itemWidth) {
        this.products = products;
        this.context = context;
        this.item_layout = item_layout;
        this.itemWidth = itemWidth;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductHomeAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(item_layout, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = this.itemWidth;
        view.setLayoutParams(layoutParams);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHomeAdapter.ProductViewHolder holder, int position) {
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

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder{
        ImageView imvThumb;
        TextView txtName;
        TextView txtRealPrice;
        TextView txtOldPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imvThumb = itemView.findViewById(R.id.imvThumb);
            txtName = itemView.findViewById(R.id.txtName);
            txtRealPrice = itemView.findViewById(R.id.txtRealPrice);
            txtOldPrice = itemView.findViewById(R.id.txtOldPrice);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(products.get(position));
                }
            });
        }
    }
    public void updateData(ArrayList<Product> newProductList) {
        products.clear();
        products.addAll(newProductList);
        notifyDataSetChanged();
    }
    public interface OnItemClickListener {
        void onItemClick(Product p);
    }


}
