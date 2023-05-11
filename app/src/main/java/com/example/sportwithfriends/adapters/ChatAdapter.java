package com.example.sportwithfriends.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportwithfriends.R;
import com.example.sportwithfriends.pojo.Chat;
import com.example.sportwithfriends.pojo.Message;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Chat> chats;
    private OnClickOpenChat onClickOpenChat;

    public interface OnClickOpenChat {
        void openChat(int position);
    }

    public ChatAdapter() {
        this.chats = new ArrayList<>();
    }

    public List<Chat> getChats() {
        return chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
        notifyDataSetChanged();
    }

    public OnClickOpenChat getOnClickOpenChat() {
        return onClickOpenChat;
    }

    public void setOnClickOpenChat(OnClickOpenChat onClickOpenChat) {
        this.onClickOpenChat = onClickOpenChat;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chats.get(position);

        holder.textViewNameAuthor.setText(chat.getMessageNameRecipient());

        if (chat.getMessageAvatarUrlRecipient() == null)
            Picasso.get().load(R.drawable.empty_avatar).into(holder.imageViewAvatarAuthor);
        else
            Picasso.get().load(chat.getMessageAvatarUrlRecipient()).into(holder.imageViewAvatarAuthor);

        Message message = chat.getListOfMessages().get(chat.getListOfMessages().size() - 1);
        String textMessage = message.getMessageText();
        if (textMessage.length() > 20)
            textMessage = textMessage.substring(0, 20) + "...";
        holder.textViewLastMessage.setText(textMessage);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewNameAuthor;
        private TextView textViewLastMessage;
        private ImageView imageViewAvatarAuthor;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewNameAuthor = itemView.findViewById(R.id.textViewNameAuthor);
            textViewLastMessage = itemView.findViewById(R.id.textViewLastMessage);
            imageViewAvatarAuthor = itemView.findViewById(R.id.imageViewAvatarAuthor);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickOpenChat != null) {
                        onClickOpenChat.openChat(getAdapterPosition());
                    }
                }
            });
        }
    }
}
