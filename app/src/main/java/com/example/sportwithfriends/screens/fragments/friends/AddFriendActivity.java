package com.example.sportwithfriends.screens.fragments.friends;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.sportwithfriends.R;
import com.example.sportwithfriends.adapters.FriendAdapter;
import com.example.sportwithfriends.constants.KeyNameDB;
import com.example.sportwithfriends.constants.TypeCollection;
import com.example.sportwithfriends.constants.TypeFragment;
import com.example.sportwithfriends.pojo.User;
import com.example.sportwithfriends.screens.MainScreenActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddFriendActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFindFriends;
    private SearchView searchViewFindFriend;

    private FriendAdapter friendAdapter;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "0";
            String channelName = "Friends";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        recyclerViewFindFriends = findViewById(R.id.recyclerViewFindFriends);
        searchViewFindFriend = findViewById(R.id.searchViewFindFriend);

        friendAdapter = new FriendAdapter(true);

        db = FirebaseFirestore.getInstance();

        recyclerViewFindFriends.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewFindFriends.setAdapter(friendAdapter);

        searchViewFindFriend.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchFriends(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.isEmpty()) {
                    searchViewFindFriend.setSubmitButtonEnabled(false);
                    friendAdapter.setFriends(new ArrayList<>());
                } else
                    searchViewFindFriend.setSubmitButtonEnabled(true);

                return true;
            }
        });

        friendAdapter.setOnClickItemAdapterAddFriend(new FriendAdapter.OnClickItemAdapterAddFriend() {
            @Override
            public void onClickAddFriend(int position) {
                User friend = friendAdapter.getFriends().get(position);
                sendNotificationForUser(friend);
                Toast.makeText(AddFriendActivity.this, friend.getUserName() + " добавлен в друзья!", Toast.LENGTH_SHORT).show();
                backToFriendFragment();
            }
        });
    }

    private void sendNotificationForUser(User friend) {
        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
        Log.d("MyLog", "user name: " + currentUser);
        if (friend != null) {
            db.collection(TypeCollection.USER_TOKEN_COLLECTION)
                    .document(friend.getUserId())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (value != null) {
                                String token = Objects.requireNonNull(value.get(KeyNameDB.TOKEN_NAME)).toString();

                                String title = "Sport with friend";
                                String message = "Пользователь " + friend.getUserName() + " хочет добавить вас в друзья";

                                // Create the message payload
                                Map<String, String> data = new HashMap<>();
                                data.put("title", title);
                                data.put("message", message);

                                // Create the FCM message
                                RemoteMessage.Builder messageBuilder = new RemoteMessage.Builder(token)
                                        .setData(data);

                                // Send the message
                                FirebaseMessaging.getInstance().send(messageBuilder.build());
                            }
                        }
                    });
        }
    }

    private void addNotificationIntoDb(String userUID, String title, String message) {

    }

    private void backToFriendFragment() {
        Intent intent = new Intent(AddFriendActivity.this, MainScreenActivity.class);
        intent.putExtra(TypeFragment.FRAGMENT_TYPE, TypeFragment.FRIENDS);
        startActivity(intent);
    }

    private void searchFriends(String query) {

        final String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        db.collection(TypeCollection.USER_COLLECTION)
                .orderBy(KeyNameDB.USER_NAME)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            List<DocumentSnapshot> documents = value.getDocuments();

                            List<User> searchFriend = new ArrayList<>();

                            for (DocumentSnapshot document : documents) {
                                if (!currentUserId.equals(document.getId())) {
                                    User user = document.toObject(User.class);
                                    if (user != null && user.getUserName().contains(query)) {
                                        user.setUserId(document.getId());
                                        searchFriend.add(user);
                                    }
                                }
                            }
                            friendAdapter.setFriends(searchFriend);
                        }
                    }
                });
    }
}