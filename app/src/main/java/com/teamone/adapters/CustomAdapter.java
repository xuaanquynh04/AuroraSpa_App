package com.teamone.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.teamone.auroraspa.R;
import com.teamone.models.Custom;
import com.teamone.models.Option;
import com.teamone.models.Product;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder>{
    ArrayList<Custom> customs;
    Context context;
    Product p;
    DecimalFormat formatter = new DecimalFormat("#,###");
    ArrayList<Option> selectedOptions;
    Double totalItemPrice = 0.0;
    Set<String> errorGroups;
    ArrayList<Option> selectedOptionsFromCart;

    public CustomAdapter(ArrayList<Custom> customs, Context context, Product p, @Nullable ArrayList<Option> selectedOptionsFromCart) {
        this.customs = customs;
        this.context = context;
        this.p = p;
        this.selectedOptions = new ArrayList<>();
        this.totalItemPrice = p.getPrice();
        this.errorGroups = new HashSet<>();
        this.selectedOptionsFromCart = selectedOptionsFromCart;
        if (selectedOptionsFromCart != null) {
            this.selectedOptions.addAll(selectedOptionsFromCart);
            for (Option o : selectedOptions) {
                this.totalItemPrice += o.getAddPrice();
            }
        }
        for (Option o3 : selectedOptions) {
            Log.d("test 58", o3.getName());
        }
        Log.d("test ở customadapter 59", this.totalItemPrice.toString());


    }
    private OnTotalPriceChangeListener totalPriceChangeListener;

    public void setOnTotalPriceChangeListener(OnTotalPriceChangeListener listener) {
        this.totalPriceChangeListener = listener;
    }
    public interface OnTotalPriceChangeListener {
        void onTotalPriceChanged(double totalItemPrice);
    }
    private void calculateTotal() {
        totalItemPrice = p.getPrice();
        for (Option o : selectedOptions) {
            totalItemPrice += o.getAddPrice();
        }
        if (totalPriceChangeListener != null) {
            totalPriceChangeListener.onTotalPriceChanged(totalItemPrice);
        }
    }
    @NonNull
    @Override
    public CustomAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_option, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.CustomViewHolder holder, int position) {
        Custom c = customs.get(position);
        Log.d("test ở customadapter", c.getName());
        holder.txtGroupName.setText(c.getName());
        holder.container.removeAllViews();
        if (c.isRequired()) {
            holder.txtRequired.setVisibility(View.VISIBLE);
            if (errorGroups.contains(c.getName())) {
                holder.txtGroupName.setTextColor(ContextCompat.getColor(context, R.color.red));
            }
        }
        if (c.getOptionType().equals("radio")) {
            for (Option o : c.getOptions()){
                View optionView = LayoutInflater.from(context).inflate(R.layout.item_radio, holder.container, false);
                RadioButton rb = optionView.findViewById(R.id.radioButton);
                TextView addPrice = optionView.findViewById(R.id.addPrice);
                rb.setText(o.getName());
                String price = formatter.format(o.getAddPrice()).replace(",", ".");
                SpannableString italicPrice = new SpannableString("+" + price);
                italicPrice.setSpan(new StyleSpan(Typeface.ITALIC), 0, price.length(), 0);
                addPrice.setText("+" + price);
                rb.setChecked(selectedOptions.contains(o));
                rb.setOnClickListener((buttonView) -> {
                    Log.d("rb status", String.valueOf(rb.isChecked()));
                    if (rb.isChecked() && selectedOptions.contains(o)) {
                        rb.setChecked(false);
                        for (Option otherOptionInGroup : c.getOptions()) {
                            selectedOptions.remove(otherOptionInGroup);
                        }
                    } else {
                        for (Option otherOptionInGroup : c.getOptions()) {
                            selectedOptions.remove(otherOptionInGroup);
                        }
                        for (int i = 0; i < holder.container.getChildCount(); i++) {
                            View otherView = holder.container.getChildAt(i);
                            RadioButton otherRb = otherView.findViewById(R.id.radioButton);
                            if (otherRb!=null) {
                                otherRb.setChecked(false);
                            }
                        }
                        selectedOptions.add(o);
                        rb.setChecked(true);
                        if (errorGroups.contains(c.getName())) {
                            errorGroups.remove(c.getName());
                            holder.txtGroupName.setTextColor(ContextCompat.getColor(context, R.color.black));
                            for (String i : errorGroups) {
                                Log.d("errr group", i);
                            }
                            notifyDataSetChanged();
                        }
                    }
                    for (Option o1 : selectedOptions) {
                        Log.d("selected", o1.getName());
                    }
//                    areRequiredOptionsSelected();
                    calculateTotal();
                });
                holder.container.addView(optionView);
            }
        } else if (c.getOptionType().equals("checkbox")){
            for (Option o : c.getOptions()) {
                View optionView = LayoutInflater.from(context).inflate(R.layout.item_checkbox, holder.container, false);
                CheckBox cb = optionView.findViewById(R.id.checkbox);
                TextView addPrice = optionView.findViewById(R.id.addPrice);
                cb.setText(o.getName());
                String price = formatter.format(o.getAddPrice()).replace(",", ".");
                SpannableString italiPrice = new SpannableString("+" + price);
                italiPrice.setSpan(new StyleSpan(Typeface.ITALIC), 0, price.length(), 0);
                addPrice.setText("+" +price);
                cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        selectedOptions.add(o);
                        for (Option o1 : selectedOptions) {
                            Log.d("selected options", o1.getName());
                        }
                    } else {
                        selectedOptions.remove(o);
                        for (Option o1 : selectedOptions) {
                            Log.d("selected options", o1.getName());
                        }
                    }
//                    areRequiredOptionsSelected();
                    calculateTotal();
                });
                holder.container.addView(optionView);
            }
        }

    }




    public ArrayList<Option> getSelectedOptions() {
        return selectedOptions;
    }

    public Double getTotalItemPrice() {
        return totalItemPrice;
    }

    @Override
    public int getItemCount() {
        return customs.size();
    }
    public void updateData(ArrayList<Custom> newCustomList) {
        customs.clear();
        customs.addAll(newCustomList);
        notifyDataSetChanged();
    }
    public boolean areRequiredOptionsSelected() {
        errorGroups.clear();
        for (Custom c : customs) {
            if (c.isRequired()) {
                boolean found = false;
                for (Option o : c.getOptions()) {
                    if (selectedOptions.contains(o)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                   errorGroups.add(c.getName());
                }
            }
        }
        notifyDataSetChanged();
        return errorGroups.isEmpty();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView txtGroupName;
        TextView txtRequired;
        LinearLayout container;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            txtGroupName = itemView.findViewById(R.id.txtGroupName);
            container = itemView.findViewById(R.id.container);
            txtRequired = itemView.findViewById(R.id.txtRequired);
        }

    }
}
