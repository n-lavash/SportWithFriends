package com.example.sportwithfriends.screens.fragments.message;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportwithfriends.R;
import com.example.sportwithfriends.adapters.ChatAdapter;
import com.example.sportwithfriends.constants.KeyNameDB;
import com.example.sportwithfriends.constants.TypeCollection;
import com.example.sportwithfriends.constants.TypeFragment;
import com.example.sportwithfriends.pojo.Chat;
import com.example.sportwithfriends.pojo.Message;
import com.example.sportwithfriends.pojo.User;
import com.example.sportwithfriends.screens.MainScreenActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


// TODO: добавить в onResume сохранение фрагмента
public class ChatsFragment extends Fragment {

    private RecyclerView recyclerViewChats;
    private TextView textViewHaveNotChats;
    private ChatAdapter chatAdapter;
    private FirebaseFirestore db;

    public ChatsFragment() {
    }

    public static ChatsFragment newInstance() {
        return new ChatsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewChats = requireActivity().findViewById(R.id.recyclerViewChats);
        textViewHaveNotChats = requireActivity().findViewById(R.id.textViewHaveNotChats);
        chatAdapter = new ChatAdapter();
        db = FirebaseFirestore.getInstance();

        recyclerViewChats.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewChats.setAdapter(chatAdapter);

        setChatAdapter();

        textViewHaveNotChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), MainScreenActivity.class);
                intent.putExtra(TypeFragment.FRAGMENT_TYPE, TypeFragment.FRIENDS);
                requireActivity().finish();
                startActivity(intent);
            }
        });

        chatAdapter.setOnClickOpenChat(new ChatAdapter.OnClickOpenChat() {
            @Override
            public void openChat(int position) {
                String currentID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                String id = "";
                Chat chat = chatAdapter.getChats().get(position);
                for (Message message : chat.getListOfMessages()) {
                    if (!message.getIdRecipient().equals(currentID))
                        id = message.getIdRecipient();
                    else if (!message.getIdAuthor().equals(currentID))
                        id = message.getIdAuthor();
                    break;
                }

                Intent intent = new Intent(requireActivity(), MessageActivity.class);
                intent.putExtra(KeyNameDB.ID_RECIPIENT_MESSAGE, id);
                intent.putExtra(KeyNameDB.USER_NAME, chat.getListOfMessages().get(0).getMessageRecipient());
                startActivity(intent);
            }
        });
    }

    private void setChatAdapter() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            db.collection(TypeCollection.CHAT_COLLECTION)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (value != null) {
                                List<DocumentSnapshot> snapshots = value.getDocuments();
                                List<Chat> chats = new ArrayList<>();

                                for (DocumentSnapshot snapshot : snapshots)
                                    if (snapshot.getId().contains(currentUserId))
                                        chats.add(snapshot.toObject(Chat.class));

                                if (!chats.isEmpty()) {
                                    chatAdapter.setChats(chats);
                                    textViewHaveNotChats.setVisibility(View.GONE);
                                } else {
                                    textViewHaveNotChats.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
        }
    }
}