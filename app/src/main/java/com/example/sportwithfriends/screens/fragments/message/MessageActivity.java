package com.example.sportwithfriends.screens.fragments.message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.sportwithfriends.R;
import com.example.sportwithfriends.adapters.MessageAdapter;
import com.example.sportwithfriends.constants.KeyNameDB;
import com.example.sportwithfriends.constants.TypeCollection;
import com.example.sportwithfriends.pojo.Chat;
import com.example.sportwithfriends.pojo.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

// TODO: в ActionBar попробовать добавить фото + имя пользователя
public class MessageActivity extends AppCompatActivity {

    private AtomicReference<String> documentId;
    private String idRecipientMessage;
    private String nameRecipientMessage;
    private FirebaseFirestore db;
    private RecyclerView recyclerViewMessages;
    private EditText editTextWriteMessage;
    private ImageView imageViewSendMessage;

    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        editTextWriteMessage = findViewById(R.id.editTextWriteMessage);
        imageViewSendMessage = findViewById(R.id.imageViewSendMessage);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(KeyNameDB.ID_RECIPIENT_MESSAGE) && intent.hasExtra(KeyNameDB.USER_NAME)) {
            idRecipientMessage = intent.getStringExtra(KeyNameDB.ID_RECIPIENT_MESSAGE);
            nameRecipientMessage = intent.getStringExtra(KeyNameDB.USER_NAME);

            db = FirebaseFirestore.getInstance();

            documentId = new AtomicReference<>();

            messageAdapter = new MessageAdapter();

            recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            recyclerViewMessages.setAdapter(messageAdapter);

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                setMessagesAdapter(currentUser);
            }
        }


        imageViewSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void setMessagesAdapter(FirebaseUser currentUser) {
        final String currentId = currentUser.getUid();
        AtomicReference<Boolean> checkExist = new AtomicReference<>(false);

        db.collection(TypeCollection.CHAT_COLLECTION).document(currentId + idRecipientMessage)
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (value != null) {
                                    checkExist.set(true);
                                    documentId.set(currentId + idRecipientMessage);
                                    Chat chat = value.toObject(Chat.class);

                                    if (chat != null) {
                                        List<Message> messages = chat.getListOfMessages();

                                        if (!messages.isEmpty())
                                            messageAdapter.setMessages(messages);
                                    }
                                }
                            }
                        });

        if (!checkExist.get()) {
            db.collection(TypeCollection.CHAT_COLLECTION).document(idRecipientMessage + currentId)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (value != null) {
                                checkExist.set(true);
                                documentId.set(idRecipientMessage + currentId);
                                Chat chat = value.toObject(Chat.class);

                                if (chat != null) {
                                    List<Message> messages = chat.getListOfMessages();

                                    if (!messages.isEmpty())
                                        messageAdapter.setMessages(messages);
                                }
                            }
                        }
                    });
        }
    }

    private void sendMessage() {
        String textMessage = editTextWriteMessage.getText().toString().trim();
        if (!textMessage.isEmpty()) {
            editTextWriteMessage.setText("");

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {

                Message newMessage = new Message(nameRecipientMessage, currentUser.getUid(), idRecipientMessage, textMessage, System.currentTimeMillis());


                DocumentReference docRef = db.collection(TypeCollection.CHAT_COLLECTION)
                        .document(documentId.get());

                db.runTransaction(new Transaction.Function<Void>() {
                    @Nullable
                    @Override
                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(docRef);
                        Chat chat = snapshot.toObject(Chat.class);

                        if (chat == null) {
                            chat = new Chat(nameRecipientMessage, null, null);
                            transaction.set(docRef, chat);
                        }

                        List<Message> messages = (List<Message>) snapshot.get(KeyNameDB.LIST_OF_MESSAGES);

                        if (messages == null)
                            messages = new ArrayList<>();

                        messages.add(newMessage);

                        transaction.update(docRef, KeyNameDB.LIST_OF_MESSAGES, messages);

                        return null;
                    }
                });
            }
        }
    }
}