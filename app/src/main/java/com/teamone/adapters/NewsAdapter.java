package com.teamone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.teamone.auroraspa.R;
import com.teamone.models.News;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    Context context;
    ArrayList<News> newsArrayList;
    int item_layout;

    public NewsAdapter(Context context, ArrayList<News> newsArrayList, int item_layout) {
        this.context = context;
        this.newsArrayList = newsArrayList;
        this.item_layout = item_layout;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(item_layout, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News n = newsArrayList.get(position);
        holder.txtTitle.setText(n.getTitle());

        String newsImage = n.getImage();
        if (newsImage != null && !newsImage.isEmpty()) {
            Glide.with(context).load(newsImage).placeholder(R.drawable.ic_launcher_foreground).error(R.drawable.ic_launcher_background).into(holder.imvThumb);
        } else
            holder.imvThumb.setImageResource(R.drawable.nail1);
    }

    @Override
    public int getItemCount() {
        return newsArrayList.size();
    }

    public void updateData(ArrayList<News> newNews) {
        newsArrayList.clear();
        newsArrayList.addAll(newNews);
        notifyDataSetChanged();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView imvThumb;
        TextView txtTitle;
        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            imvThumb = itemView.findViewById(R.id.imvThumb);
            txtTitle = itemView.findViewById(R.id.txtTitle);
        }
    }
}
