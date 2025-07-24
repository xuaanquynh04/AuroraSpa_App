package com.teamone.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.teamone.auroraspa.R;
import com.teamone.models.BookItem;
import com.teamone.models.CartItem;
import com.teamone.models.Feedback;

import java.util.ArrayList;
import java.util.Locale;

public class FeedbackItemAdapter extends BaseAdapter {
    Context context;
    int item_layout;
    ArrayList<BookItem> bookItems;
    private ArrayList<Float> itemRatings;
    private ArrayList<String> itemContents;
    String customerID;
    public interface OnFeedbackStatusChangeListener {
        void onFeedbackStatusChanged();
    }
    private OnFeedbackStatusChangeListener listener;
    public void setOnFeedbackStatusChangeListener(OnFeedbackStatusChangeListener listener) {
        this.listener = listener;
    }

    public FeedbackItemAdapter(Context context, int item_layout, ArrayList<BookItem> bookItems) {
        this.context = context;
        this.item_layout = item_layout;
        this.bookItems = bookItems;

        this.itemRatings = new ArrayList<>(bookItems.size());
        this.itemContents = new ArrayList<>(bookItems.size());
        for (int i = 0; i < bookItems.size(); i++) {
            this.itemRatings.add(0.0f);
            this.itemContents.add("");
        }
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    @Override
    public int getCount() {
        return bookItems.size();
    }

    @Override
    public Object getItem(int position) {
        return bookItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_feedback_product, null);
            holder.imvServicePhoto = convertView.findViewById(R.id.imvServicePhoto);
            holder.txtServiceName = convertView.findViewById(R.id.txtServiceName);
            holder.txtServicePrice = convertView.findViewById(R.id.txtServicePrice);
            holder.ratingBar = convertView.findViewById(R.id.ratingBar);
            holder.edtFeedbackContent = convertView.findViewById(R.id.edtFeedbackContent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BookItem bookItem = bookItems.get(position);
        CartItem cartItem = null;
        if(bookItem != null){
            cartItem = bookItem.getItem();
        }
        if(cartItem != null){
            holder.txtServiceName.setText(cartItem.getProductName());
            holder.txtServicePrice.setText(String.format(Locale.getDefault(), "%,dđ",
                    cartItem.getFinalPrice().longValue()));
            String productImage = cartItem.getProductImage();
            if(productImage != null && !productImage.isEmpty()){
                Glide.with(context).load(productImage).into(holder.imvServicePhoto);
            } else {
                holder.imvServicePhoto.setImageResource(R.drawable.nail1);
            }

            // Ratingbar progress
            holder.ratingBar.setRating(itemRatings.get(position));
            holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    itemRatings.set(position, rating);
                    if(listener != null){
                        listener.onFeedbackStatusChanged();
                    }
                }
            });

            if (holder.edtFeedbackContent.getTag() instanceof TextWatcher){
                holder.edtFeedbackContent.removeTextChangedListener((TextWatcher)
                        holder.edtFeedbackContent.getTag());
            }
            holder.edtFeedbackContent.setText(itemContents.get(position));

            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    itemContents.set(position, s.toString());
                    if(listener != null){
                        listener.onFeedbackStatusChanged();
                    }
                    Log.d("FeedbackAdapter", "Content changed for position " + position + ": '" + s.toString() + "'");
                }
            };
            holder.edtFeedbackContent.addTextChangedListener(textWatcher);
            holder.edtFeedbackContent.setTag(textWatcher);
        } else {
            Log.e("FeedbackAdapter","CartItem is null for BookItem at position " + position + ". Skipping data binding");
            holder.imvServicePhoto.setImageResource(R.drawable.nail1);
            holder.txtServiceName.setText("Dịch vụ không xác định");
            holder.txtServicePrice.setText("0đ");
            holder.ratingBar.setRating(0.0f);
            holder.edtFeedbackContent.setText("");
            if (holder.edtFeedbackContent.getTag() instanceof TextWatcher){
                holder.edtFeedbackContent.removeTextChangedListener((TextWatcher)
                        holder.edtFeedbackContent.getTag());
            }
        }

        return convertView;
    }

    private class ViewHolder {
        ImageView imvServicePhoto;
        TextView txtServiceName;
        TextView txtServicePrice;
        RatingBar ratingBar;
        EditText edtFeedbackContent;
    }
    public boolean areAllItemsFeedbacked(){
        for (int i = 0; i < bookItems.size();i++){
            float rating = itemRatings.get(i);
            String content = itemContents.get(i).trim();
            if(rating == 0.0f || content.isEmpty()){
                return false;
            }
        }
        return true;
    }

    public ArrayList<Feedback> getCollectedFeedbacks(String customerID){
        ArrayList<Feedback> collectedFeedbacks = new ArrayList<>();
        for (int i = 0; i < bookItems.size(); i++) {
            BookItem bookItem = bookItems.get(i);
            CartItem cartItem = bookItem.getItem();

            if(cartItem == null){
                Log.e("FeedbackAdapter","Cannot create feedback");
                continue;
            }
            int rating = Math.round(itemRatings.get(i));
            String content = itemContents.get(i).trim();
            if(rating > 0){
                Feedback feedback = new Feedback(cartItem.getProductID(), customerID,content, rating, Timestamp.now());
                Log.d("dong 195", feedback.getCustomerID());
                collectedFeedbacks.add(feedback);
            };
        }
        return collectedFeedbacks;
    };
}
