package com.example.sportwithfriends.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportwithfriends.R;
import com.example.sportwithfriends.pojo.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private List<User> friends;
    private final boolean searchFriends;

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

            User friendFromAdapter = friends.get(position);
            setFriend(friendFromAdapter, holder);
        }
    }

    private void setFriend(User friendFromAdapter, FriendViewHolder holder) {

        // устанавливаем аватар пользователя
        if (friendFromAdapter.getUserAvatarUrl() == null) {
            Picasso.get().load(R.drawable.empty_avatar).into(holder.imageViewAvatarFriend);
        } else {
            Picasso.get().load(friendFromAdapter.getUserAvatarUrl()).into(holder.imageViewAvatarFriend);
        }

        // устанавливаем имя пользователя
        holder.textViewNameFriend.setText(friendFromAdapter.getUserName());

        if (!searchFriends)
            // изображение отправки сообщение
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
                    if (onClickItemAdapterSendMessage != null)
                        onClickItemAdapterSendMessage.onClickSendMessage(getAdapterPosition());
                }
            });
        }
    }
}
