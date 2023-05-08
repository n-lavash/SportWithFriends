package com.example.sportwithfriends.screens.fragments.news;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportwithfriends.R;
import com.example.sportwithfriends.adapters.NewsAdapter;
import com.example.sportwithfriends.constants.TypeCollection;
import com.example.sportwithfriends.pojo.News;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

// TODO: добавить в onResume сохранение фрагмента

public class NewsFragment extends Fragment {

    private RecyclerView recyclerViewNews;
    private NewsAdapter newsAdapter;

    private FirebaseFirestore db;

    public NewsFragment() {
    }

    public static NewsFragment newInstance() {
        return new NewsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewNews = requireActivity().findViewById(R.id.recyclerViewNews);
        newsAdapter = new NewsAdapter();
        recyclerViewNews.setAdapter(newsAdapter);
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        db = FirebaseFirestore.getInstance();

        db.collection(TypeCollection.NEWS_COLLECTION)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            List<News> news = value.toObjects(News.class);
                            newsAdapter.setNewsList(news);
                        } else {
                            Log.d("MyLog", "Error get news from db", error);
                        }
                    }
                });
    }
}