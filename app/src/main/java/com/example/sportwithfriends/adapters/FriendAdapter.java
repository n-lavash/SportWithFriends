package com.example.sportwithfriends.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportwithfriends.R;
import com.example.sportwithfriends.pojo.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private List<User> friends;
    private boolean searchFriends;

    private OnClickItemAdapterSendMessage onClickItemAdapterSendMessage;

    public interface OnClickItemAdapterSendMessage {
        void onClickSendMessage(int position);
    }

    private OnClickItemAdapterAddFriend onClickItemAdapterAddFriend;

    public interface OnClickItemAdapterAddFriend {
        void onClickAddFriend(int position);
    }

    public void setOnClickItemAdapterSendMessage(OnClickItemAdapterSendMessage onClickItemAdapterSendMessage) {
        this.onClickItemAdapterSendMessage = onClickItemAdapterSendMessage;
    }

    public void setOnClickItemAdapterAddFriend(OnClickItemAdapterAddFriend onClickItemAdapterAddFriend) {
        this.onClickItemAdapterAddFriend = onClickItemAdapterAddFriend;
    }

    public FriendAdapter(boolean searchFriends) {
        friends = new ArrayList<>();
        this.searchFriends = searchFriends;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {

            final String currentUserId = currentUser.getUid();

            final User friendFromAdapter = friends.get(position);
            setFriend(friendFromAdapter, holder);

//            db.collection("users")
//                    .document(currentUserId)
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//                            User currentUser = task.getResult().toObject(User.class);
//
//                            if (currentUser != null) {
//                                if (searchFriends) {
//                                    // получаем список всех пользователей
//                                    // и записываем его
//                                } else {
//                                    List<User> currentUserFriends = currentUser.getFriends();
//
//                                    if (currentUserFriends != null) {
//                                        setFriend(friendFromAdapter, holder, currentUserFriends);
//                                    }
//                                }
//                            }
//                        }
//                    });

//            db.collection("users")
//                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                        @Override
//                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                            if (value != null) {
//
//                                List<DocumentSnapshot> documents = value.getDocuments();
//                                for (DocumentSnapshot document : documents)
//                                    if (document.getId().equals(currentUserId)) {
//                                        User currentUser = document.toObject(User.class);
//
//                                        if (currentUser != null) {
//                                            List<User> currentUserFriends = currentUser.getFriends();
//                                            if (searchFriends) {
//
//                                                List<User> users = value.toObjects(User.class);
//
//                                                // удаляем пользователя из списка, если это currentUser
//                                                users.removeIf(user -> !user.getEmail().equals(currentUser.getEmail()));
//
//                                                setFriend(friendFromAdapter, holder);
//
//                                            } else {
//
//                                                if (currentUserFriends != null)
//                                                    setFriend(friendFromAdapter, holder);
//                                            }
//                                        }
//                                    }
//                            }
//                        }
//                    });
        }
    }

    private void setFriend(User friendFromAdapter, FriendViewHolder holder) {

        // устанавливаем аватар пользователя
        if (friendFromAdapter.getAvatarUrl() == null) {
            Picasso.get().load(R.drawable.empty_avatar).into(holder.imageViewAvatarFriend);
        } else {
            Picasso.get().load(friendFromAdapter.getAvatarUrl()).into(holder.imageViewAvatarFriend);
        }

        // устанавливаем имя пользователя
        holder.textViewNameFriend.setText(friendFromAdapter.getName());

        if (!searchFriends)
            Picasso.get().load(R.drawable.icon_send_friend_message).into(holder.imageViewAction);
            // TODO: добавить изображение delete from friends
        else
            // изображение добавить в друзья
            Picasso.get().load(R.drawable.icon_add_friend).into(holder.imageViewAction);

    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    class FriendViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewAvatarFriend;
        private ImageView imageViewAction;
        private TextView textViewNameFriend;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewAvatarFriend = itemView.findViewById(R.id.imageViewAvatarFriend);
            imageViewAction = itemView.findViewById(R.id.imageViewActionWithFriend);
            textViewNameFriend = itemView.findViewById(R.id.textViewNameFriend);

            imageViewAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickItemAdapterAddFriend != null)
                        onClickItemAdapterAddFriend.onClickAddFriend(getAdapterPosition());
                }
            });

            imageViewAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickItemAdapterSendMessage != null)
                        onClickItemAdapterSendMessage.onClickSendMessage(getAdapterPosition());
                }
            });
        }
    }
}
