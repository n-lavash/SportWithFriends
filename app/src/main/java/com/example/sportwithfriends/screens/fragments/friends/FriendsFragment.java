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
import com.example.sportwithfriends.constants.TypeFragment;
import com.example.sportwithfriends.pojo.User;
import com.example.sportwithfriends.screens.fragments.message.MessageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.List;


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
            setAdapterIntoRecyclerView(currentUser);
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
                Intent intent = new Intent(requireActivity(), MessageActivity.class);
                intent.putExtra(TypeFragment.FRAGMENT_TYPE, TypeFragment.CHATS);
                startActivity(intent);
            }
        });
    }

    private void setAdapterIntoRecyclerView(FirebaseUser currentUser) {
        final String id = currentUser.getUid();
        db.collection("users")
                .document(id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (value != null) {
                            User user = value.toObject(User.class);
                            if (user != null) {
                                List<User> friends = user.getFriends();

                                if (friends != null) {
                                    recyclerViewFriends.setVisibility(View.VISIBLE);
                                    textViewNotFindUserFriends.setVisibility(View.GONE);
                                    friendAdapter.setFriends(friends);
                                    Log.d("MyLog", "set list of friends in adapter");
                                } else {
                                    recyclerViewFriends.setVisibility(View.GONE);
                                    textViewNotFindUserFriends.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                });
    }
}