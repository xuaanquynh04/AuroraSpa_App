package com.teamone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;
import com.teamone.auroraspa.R;
import com.teamone.models.CartItem;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Adapter_cart extends RecyclerView.Adapter<Adapter_cart.MyViewHolder> {
    Context context;
    int item_layout;
    ArrayList<CartItem>item_list;
    private boolean[] checkedItems;
    private OnItemCheckedListener listener;

    public Adapter_cart(Context context, int item_layout, ArrayList<CartItem> item_list) {
        this.context = context;
        this.item_layout = item_layout;
        this.item_list = item_list;
        this.checkedItems = new boolean[item_list.size()];
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemview= LayoutInflater.from(context).inflate(item_layout,parent, false);

        return new MyViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CartItem item = item_list.get(position);

        DecimalFormat formatter = new DecimalFormat("#,###");
        String formattedPrice = formatter.format(item.getFinalPrice()).replace(",", ".");
        holder.txtPrice.setText(formattedPrice);
        holder.txtName.setText(item.getProductName());
        Glide.with(context)
                .load(item.getProductImage())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.imvPhoto);
        
        // Set trạng thái checked cho checkbox
        holder.checkBox.setChecked(checkedItems[position]);
        
        // Xử lý sự kiện click checkbox
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            checkedItems[position] = isChecked;
            if (listener != null) {
                listener.onItemChecked(position, isChecked);
            }
        });
        
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.deleteItem(position);
            }
        });
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.editItem(position);
            }
        });
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.lnOption);
    }

    @Override
    public int getItemCount() {
        return item_list.size();
    }
    
    // Phương thức để select all items
    public void selectAllItems(boolean isChecked) {
        for (int i = 0; i < checkedItems.length; i++) {
            checkedItems[i] = isChecked;
        }
        notifyDataSetChanged();
    }
    
    // Phương thức để kiểm tra xem tất cả items có được check không
    public boolean areAllItemsChecked() {
        for (boolean checked : checkedItems) {
            if (!checked) {
                return false;
            }
        }
        return true;
    }
    
    // Phương thức để lấy danh sách các items đã được check
    public ArrayList<CartItem> getCheckedItems() {
        ArrayList<CartItem> checkedItemsList = new ArrayList<>();
        for (int i = 0; i < checkedItems.length; i++) {
            if (checkedItems[i]) {
                checkedItemsList.add(item_list.get(i));
            }
        }
        return checkedItemsList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        LinearLayout lnOption;
        ImageView imvPhoto;
        TextView txtName, txtPrice;
        LinearLayout btnDelete, btnEdit;
        CheckBox checkBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            swipeLayout = itemView.findViewById(R.id.swipeLayout);
            lnOption = itemView.findViewById(R.id.lnOption);
            imvPhoto = itemView.findViewById(R.id.imvPhoto);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

    public void setOnItemCheckedListener(OnItemCheckedListener listener) {
        this.listener = listener;
    }

    public interface OnItemCheckedListener {
        void onItemChecked(int position, boolean isChecked);
        void deleteItem(int position);
        void editItem(int position);
    }

}
