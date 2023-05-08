package com.example.sportwithfriends.screens.fragments.friends;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

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
                }
                else
                    searchViewFindFriend.setSubmitButtonEnabled(true);

                return true;
            }
        });

        friendAdapter.setOnClickItemAdapterAddFriend(new FriendAdapter.OnClickItemAdapterAddFriend() {
            @Override
            public void onClickAddFriend(int position) {
                User friend = friendAdapter.getFriends().get(position);
                addFriendToUser(friend);
                Toast.makeText(AddFriendActivity.this, friend.getUserName() + " добавлен в друзья!", Toast.LENGTH_SHORT).show();
                backToFriendFragment();
            }
        });
    }

    private void addFriendToUser(User friend) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Map<String, String> friends = new HashMap<>();
            friends.put(KeyNameDB.USER_ID, currentUser.getUid());
            friends.put(KeyNameDB.ANOTHER_USER_ID, friend.getUserId());

            db.collection(TypeCollection.FRIEND_COLLECTION)
                    .document()
                    .set(friends)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("MyLog", "Error add friend and current user into db", e);
                        }
                    });
        }
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