package com.example.sportwithfriends.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sportwithfriends.R;
import com.example.sportwithfriends.constants.KeyNameDB;
import com.example.sportwithfriends.constants.TypeCollection;
import com.example.sportwithfriends.pojo.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class RegisteredActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText editTextEmail;
    private EditText editTextName;
    private EditText editTextPassword;
    private EditText editTextReplayPassword;
    private TextView textViewCorrectPassword;
    private TextView textViewCorrectEmail;
    private Button buttonRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);

        // сделать кастомную actionbar для каждой активити
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Регистрация");
        }

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextName = findViewById(R.id.editTextName);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextReplayPassword = findViewById(R.id.editTextReplayPassword);
        textViewCorrectPassword = findViewById(R.id.textViewCorrectPassword);
        textViewCorrectEmail = findViewById(R.id.textViewCorrectEmail);
        buttonRegistered = findViewById(R.id.buttonRegistered);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // проверка на валидность почты
        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textViewCorrectEmail.setVisibility(View.GONE);

                String email = getEmail();
                if (!checkValidEmail(email)) {
                    textViewCorrectEmail.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // проверка на схожесть паролей
        editTextReplayPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textViewCorrectPassword.setVisibility(View.GONE);

                String password = getPassword();
                if (!checkEqualsPasswordAndReplayPassword(password))
                    textViewCorrectPassword.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        buttonRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });
    }

    private void createNewAccount() {
        String email = getEmail();
        String name = getName();
        String password = getPassword();

        if (checkValidEmail(email) && checkValidPassword(password) && !name.isEmpty()) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // если регистрация прошла успешно, записываем имя пользователя вместе с почтой и паролем
                                FirebaseUser user = mAuth.getCurrentUser();
                                UserProfileChangeRequest userChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();

                                String userUID = "";
                                if (user != null) {
                                    userUID = user.getUid();
                                    user.updateProfile(userChangeRequest)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (!task.isSuccessful())
                                                        Log.d("MyLog", "error update user name for current user");
                                                }
                                            });

                                    // добавляем нового пользователя в базу данных
                                    User newUser = new User(userUID, name, email, null);
                                    addNewUserIntoDB(newUser);
                                    addTokenForUser(newUser);

                                    Intent intent = new Intent(RegisteredActivity.this, MainScreenActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                Toast.makeText(RegisteredActivity.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                                Log.d("MyLog", "" + task.getException());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisteredActivity.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                            Log.d("MyLog", "" + e);
                        }
                    });
        } else {
            Toast.makeText(this, "Все поля должны быть заполнены верно", Toast.LENGTH_SHORT).show();
        }
    }

    private void addNewUserIntoDB(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put(KeyNameDB.USER_NAME, user.getUserName());
        userMap.put(KeyNameDB.USER_EMAIL, user.getUserEmail());
        userMap.put(KeyNameDB.USER_AVATAR_URL, user.getUserAvatarUrl());

        db.collection(TypeCollection.USER_COLLECTION).document(user.getUserId())
                .set(userMap)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MyLog", "Error add user into db", e);
                    }
                });
    }

    private void addTokenForUser(User user) {

        getTokenForUser(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    String token = task.getResult();

                    Map<String, Object> tokenMap = new HashMap<>();
                    tokenMap.put(KeyNameDB.TOKEN_NAME, token);

                    db.collection(TypeCollection.USER_TOKEN_COLLECTION).document(user.getUserId())
                            .set(tokenMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                        Log.d("MyLog", "Add token to db");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("MyLog", "Error add users token into db", e);
                                }
                            });

                } else
                    Log.d("MyLog", "Fetching FCM registration token failed", task.getException());
            }
        });
    }

    private void getTokenForUser(OnCompleteListener<String> listener) {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(listener);
    }

    private boolean checkEqualsPasswordAndReplayPassword(String password) {
        if (!password.isEmpty()) {
            String replayPassword = getReplayPassword();

            return replayPassword.equals(password);
        }
        return false;
    }

    private boolean checkValidPassword(String password) {
        return checkEqualsPasswordAndReplayPassword(password) && password.length() >= 6;
    }

    private boolean checkValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private String getName() {
        return editTextName.getText().toString().trim();
    }

    private String getEmail() {
        return editTextEmail.getText().toString().trim();
    }

    private String getPassword() {
        return editTextPassword.getText().toString().trim();
    }

    private String getReplayPassword() {
        return editTextReplayPassword.getText().toString().trim();
    }

    public void onClickLogIn(View view) {
        Intent intent = new Intent(RegisteredActivity.this, LogInActivity.class);
        startActivity(intent);
    }
}