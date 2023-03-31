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
import com.example.sportwithfriends.constants.TypeFragment;
import com.example.sportwithfriends.pojo.Exercise;
import com.example.sportwithfriends.pojo.User;
import com.example.sportwithfriends.screens.MainScreenActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;
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
                Toast.makeText(AddFriendActivity.this, friend.getName() + " добавлен в друзья!", Toast.LENGTH_SHORT).show();
                backToFriendFragment();
            }
        });
    }

    private void addFriendToUser(User friend) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            final String currentId = currentUser.getUid();

            final DocumentReference docRef = db.collection("users").document(currentId);

            db.runTransaction(new Transaction.Function<Void>() {
                @Nullable
                @Override
                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(docRef);

                    List<User> friends = (List<User>) snapshot.get("friends");
                    if (friends == null)
                        friends = new ArrayList<>();

                    friends.add(friend);

                    transaction.update(docRef, "friends", friends);
                    return null;
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

         db.collection("users")
                 .orderBy("name")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            List<DocumentSnapshot> documents = value.getDocuments();

                            List<User> searchFriend = new ArrayList<>();

                            for (DocumentSnapshot document : documents) {
                                if (!currentUserId.equals(document.getId())) {
                                    User user = document.toObject(User.class);
                                    if (user != null && user.getName().contains(query))
                                        searchFriend.add(user);
                                }
                            }

                            friendAdapter.setFriends(searchFriend);
                        }
                    }
                });
    }
}