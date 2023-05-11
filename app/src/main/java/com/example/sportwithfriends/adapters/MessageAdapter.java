package com.example.sportwithfriends.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportwithfriends.R;
import com.example.sportwithfriends.pojo.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final int CURRENT_AUTHOR_MESSAGE = 0;
    private final int ANOTHER_AUTHOR_MESSAGE = 1;
    private List<Message> messages;

    public MessageAdapter() {
        this.messages = new ArrayList<>();
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == CURRENT_AUTHOR_MESSAGE)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_current_author, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_other_author, parent, false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.textViewNameAuthor.setText(message.getMessageRecipient());
        holder.textViewMessage.setText(message.getMessageText());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        String idAuthor = message.getIdAuthor();
        String idCurrentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        if (idAuthor.equals(idCurrentUser))
         return CURRENT_AUTHOR_MESSAGE;
        else
            return ANOTHER_AUTHOR_MESSAGE;
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewNameAuthor;
        private TextView textViewMessage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewNameAuthor = itemView.findViewById(R.id.textViewNameAuthor);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
        }
    }
}
