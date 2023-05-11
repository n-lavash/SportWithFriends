package com.example.sportwithfriends.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sportwithfriends.R;
import com.example.sportwithfriends.constants.KeyNameDB;
import com.example.sportwithfriends.constants.TypeCollection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LogInActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogIn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // TODO: сделать кастомную actionbar для каждой активити
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Вход");
        }

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogIn = findViewById(R.id.buttonLogIn);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (validData(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // проверка работы токена
                                checkGeneratedTokenAndTokenFromDb();

                                // Вход выполнен, переход на основную активность
                                Intent intent = new Intent(LogInActivity.this, MainScreenActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(LogInActivity.this, "Ошибка входа", Toast.LENGTH_SHORT).show();
                                Log.w("MyLog", "signInWithEmail:failure ", task.getException());
                            }
                        }
                    });
        } else {
            // TODO: сделать вывод в textView и отметить красным
            Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validData(String email, String password) {
        return !email.isEmpty() && !password.isEmpty();
    }

    public void onClickLogIn(View view) {
        Intent intent = new Intent(LogInActivity.this, RegisteredActivity.class);
        startActivity(intent);
    }

    private void getGenerationToken(OnCompleteListener<String> listener) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(listener);
    }

    private void getTokenFromDb(OnCompleteListener<DocumentSnapshot> listener) {
        db.collection(TypeCollection.USER_TOKEN_COLLECTION).document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .get()
                .addOnCompleteListener(listener);
    }

    private void checkGeneratedTokenAndTokenFromDb() {
        getGenerationToken(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    final String generatedToken = task.getResult();
                    getTokenFromDb(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String tokenFromDb = document.getString(KeyNameDB.TOKEN_NAME);
                                    if (tokenFromDb != null && !tokenFromDb.isEmpty()) {
                                        if (!tokenFromDb.equals(generatedToken)) {
                                            updateNewTokenIntoDb(generatedToken);
                                        }
                                    }
                                }
                            } else
                                Log.d("MyLog", "Error getting token from db", task.getException());
                        }
                    });
                } else
                    Log.d("MyLog", "Error generated token", task.getException());
            }
        });
    }

    private void updateNewTokenIntoDb(String generatedToken) {
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put(KeyNameDB.TOKEN_NAME, generatedToken);

        db.collection(TypeCollection.USER_TOKEN_COLLECTION).document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .set(tokenMap)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MyLog", "Error add users token into db", e);
                    }
                });
    }
}