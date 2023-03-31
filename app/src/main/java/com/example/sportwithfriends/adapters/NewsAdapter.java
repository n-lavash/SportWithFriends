package com.example.sportwithfriends.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportwithfriends.R;
import com.example.sportwithfriends.pojo.News;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<News> newsList;

    public NewsAdapter() {
        newsList = new ArrayList<>();
    }

    public List<News> getNewsList() {
        return newsList;
    }

    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = newsList.get(position);
        holder.textViewNewsTitle.setText(news.getTitle());
        holder.textViewNewsContentDescription.setText(news.getContentDescription());
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewNewsTitle;
        private TextView textViewNewsContentDescription;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewNewsTitle = itemView.findViewById(R.id.textViewNewsTitle);
            textViewNewsContentDescription = itemView.findViewById(R.id.textViewNewsContentDescription);
        }
    }
}
