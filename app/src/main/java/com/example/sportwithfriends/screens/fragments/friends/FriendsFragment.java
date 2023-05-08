package com.example.sportwithfriends.screens.fragments.friends;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportwithfriends.R;
import com.example.sportwithfriends.adapters.FriendAdapter;
import com.example.sportwithfriends.constants.KeyNameDB;
import com.example.sportwithfriends.constants.TypeCollection;
import com.example.sportwithfriends.constants.TypeFragment;
import com.example.sportwithfriends.pojo.Exercise;
import com.example.sportwithfriends.pojo.User;
import com.example.sportwithfriends.screens.fragments.message.MessageActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


// TODO: добавить в onResume сохранение фрагмента
public class FriendsFragment extends Fragment {

    private RecyclerView recyclerViewFriends;
    private TextView textViewNotFindUserFriends;
    private FriendAdapter friendAdapter;

    private FirebaseFirestore db;

    public FriendsFragment() {
    }

    // TODO: в адаптере как-то сделать проверку,
    //  если пользователь уже есть в друзьях у currentUser, то вместо кнопки add показывать кнопку send

    public static FriendsFragment newInstance() {
        return new FriendsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewFriends = requireActivity().findViewById(R.id.recyclerViewFriends);
        textViewNotFindUserFriends = requireActivity().findViewById(R.id.textViewNotFindUserFriends);

        friendAdapter = new FriendAdapter(false);

        db = FirebaseFirestore.getInstance();

        recyclerViewFriends.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewFriends.setAdapter(friendAdapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            setFriendsAdapter(currentUser);
        }

        textViewNotFindUserFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), AddFriendActivity.class);
                startActivity(intent);
            }
        });

        friendAdapter.setOnClickItemAdapterSendMessage(new FriendAdapter.OnClickItemAdapterSendMessage() {
            @Override
            public void onClickSendMessage(int position) {
                User friend = friendAdapter.getFriends().get(position);
                Intent intent = new Intent(requireActivity(), MessageActivity.class);
                intent.putExtra(KeyNameDB.ID_RECIPIENT_MESSAGE, friend.getUserId());
                intent.putExtra(KeyNameDB.USER_NAME, friend.getUserName());
                startActivity(intent);
            }
        });
    }

    private void setFriendsAdapter(FirebaseUser currentUser) {
        final String id = currentUser.getUid();

        db.collection(TypeCollection.FRIEND_COLLECTION)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            List<DocumentSnapshot> snapshots = value.getDocuments();
                            List<String> friendsId = new ArrayList<>();

                            for (DocumentSnapshot document :
                                    snapshots) {
                                if (Objects.equals(document.get(KeyNameDB.USER_ID), id))
                                    friendsId.add(Objects.requireNonNull(document.get(KeyNameDB.ANOTHER_USER_ID)).toString());
                            }

                            if (!friendsId.isEmpty()) {
                                setUsersFriends(friendsId);
                                recyclerViewFriends.setVisibility(View.VISIBLE);
                                textViewNotFindUserFriends.setVisibility(View.GONE);
                            } else {
                                recyclerViewFriends.setVisibility(View.GONE);
                                textViewNotFindUserFriends.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }

    private void setUsersFriends(List<String> friendsId) {
        db.collection(TypeCollection.USER_COLLECTION)
                .whereIn(FieldPath.documentId(), friendsId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<User> friends = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User friend = document.toObject(User.class);
                                friend.setUserId(document.getId());
                                friends.add(friend);
                            }
                            friendAdapter.setFriends(friends);
                        } else {
                            Log.d("MyLog", "Error getting exercises for current user: ", task.getException());
                        }
                    }
                });
    }
}