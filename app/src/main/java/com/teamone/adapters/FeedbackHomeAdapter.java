package com.teamone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.teamone.auroraspa.R;
import com.teamone.models.Feedback;
import com.teamone.models.Product;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class FeedbackHomeAdapter extends RecyclerView.Adapter<FeedbackHomeAdapter.FeedbackViewHolder>{
    Context context;
    int item_layout;
    ArrayList<Feedback> fbs;
    int itemWidth = 0;

    public FeedbackHomeAdapter(Context context, int item_layout, ArrayList<Feedback> fbs, int itemWidth) {
        this.context = context;
        this.item_layout = item_layout;
        this.fbs = fbs;
        this.itemWidth = itemWidth;
    }

    @NonNull
    @Override
    public FeedbackHomeAdapter.FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(item_layout, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = this.itemWidth;
        view.setLayoutParams(layoutParams);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackHomeAdapter.FeedbackViewHolder holder, int position) {
        Feedback fb = fbs.get(position);
        holder.txtUsername.setText(fb.getCustomerID());
        holder.txtComment.setText(fb.getContent());
        Date date = fb.getDate().toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.txtDate.setText(sdf.format(date));
        holder.rbRate.setRating(fb.getRating());
    }

    @Override
    public int getItemCount() {
        return fbs.size();
    }

    public class FeedbackViewHolder extends RecyclerView.ViewHolder{
        TextView txtUsername;
        RatingBar rbRate;
        TextView txtComment;
        TextView txtDate;
        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            rbRate = itemView.findViewById(R.id.rbRate);
            txtComment = itemView.findViewById(R.id.txtComment);
            txtDate = itemView.findViewById(R.id.txtDate);

        }
    }
    public void updateData(ArrayList<Feedback> newFbList) {
        fbs.clear();
        fbs.addAll(newFbList);
        notifyDataSetChanged();
    }
}
