package com.example.sportwithfriends.screens.fragments.exercise;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.sportwithfriends.R;
import com.example.sportwithfriends.constants.KeyNameDB;
import com.example.sportwithfriends.constants.TypeCollection;
import com.example.sportwithfriends.constants.TypeFragment;
import com.example.sportwithfriends.pojo.Exercise;
import com.example.sportwithfriends.pojo.User;
import com.example.sportwithfriends.screens.MainScreenActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddExerciseActivity extends AppCompatActivity {

    private EditText editTextExerciseTitle;
    private EditText editTextExerciseDescription;
    private EditText editTextExercisePlace;
    private EditText editTextExerciseCountOfPlayers;
    private Spinner spinnerExerciseType;
    private TextView textViewTimeExercise;
    private TextView textViewDateExercise;
    private Button buttonSaveExercise;
    private Button buttonCancelSaveExercise;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private TimePickerDialog.OnTimeSetListener timeListener;
    private DatePickerDialog.OnDateSetListener dateListener;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);

        editTextExerciseTitle = findViewById(R.id.editTextExerciseTitle);
        editTextExerciseDescription = findViewById(R.id.editTextExerciseDescription);
        editTextExercisePlace = findViewById(R.id.editTextExercisePlace);
        spinnerExerciseType = findViewById(R.id.spinnerExerciseType);
        textViewTimeExercise = findViewById(R.id.textViewTimeExercise);
        textViewDateExercise = findViewById(R.id.textViewDateExercise);
        buttonSaveExercise = findViewById(R.id.buttonSaveExercise);
        buttonCancelSaveExercise = findViewById(R.id.buttonCancelSaveExercise);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        setSpinnerExerciseType();

        calendar = Calendar.getInstance();
        timeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                setInitialTime();
            }
        };

        dateListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setInitialDate();
            }
        };

        buttonSaveExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validData()) {
                    // TODO: добавить выделение незаполненных полей
                    Toast.makeText(AddExerciseActivity.this, "Поля не заполнены", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(AddExerciseActivity.this, MainScreenActivity.class);
                    intent.putExtra(TypeFragment.FRAGMENT_TYPE, TypeFragment.EXERCISES);
                    startActivity(intent);
                }
            }
        });

        buttonCancelSaveExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddExerciseActivity.this, MainScreenActivity.class);
                intent.putExtra(TypeFragment.FRAGMENT_TYPE, TypeFragment.EXERCISES);
                startActivity(intent);
            }
        });
    }

    private void setSpinnerExerciseType() {
        db.collection(TypeCollection.TYPE_OF_EXERCISE_COLLECTION).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> typeList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                typeList.add(Objects.requireNonNull(document.getData().get("title")).toString());
                            }
                            String[] types = typeList.toArray(new String[0]);
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddExerciseActivity.this, android.R.layout.simple_spinner_item, types);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerExerciseType.setAdapter(adapter);
                        } else {
                            Log.w("MyLog", "Error getting types of exercise ", task.getException());
                        }
                    }
                });
    }

    private void setInitialTime() {
        textViewTimeExercise.setText(DateUtils.formatDateTime(this,
                calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
    }

    private void setInitialDate() {
        textViewDateExercise.setText(DateUtils.formatDateTime(this,
                calendar.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }

    public void onClickSetDate(View view) {
        new DatePickerDialog(AddExerciseActivity.this, dateListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    public void onClickSetTime(View view) {
        new TimePickerDialog(AddExerciseActivity.this, timeListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true)
                .show();
    }

    private boolean validData() {
        String title = editTextExerciseTitle.getText().toString().trim();
        String description = editTextExerciseDescription.getText().toString().trim();
        String place = editTextExercisePlace.getText().toString().trim();
        String type = spinnerExerciseType.getSelectedItem().toString();

        long dateTime = 0;
        if (!textViewTimeExercise.getText().toString().trim().isEmpty() && !textViewDateExercise.getText().toString().trim().isEmpty())
            dateTime = calendar.getTimeInMillis();

        long dateOfCreate = System.currentTimeMillis();

        if (!title.isEmpty() && !description.isEmpty() && !place.isEmpty()) {
            addExerciseToDB(title, description, place, type, dateTime, dateOfCreate);
            return true;
        }
        return false;
    }

    private void addExerciseToDB(String title, String description, String place, String type, long dateTime, long dateOfCreate) {
        Map<String, Object> exercise = new HashMap<>();
        exercise.put(KeyNameDB.EXERCISE_TITLE, title);
        exercise.put(KeyNameDB.EXERCISE_DESCRIPTION, description);
        exercise.put(KeyNameDB.EXERCISE_PLACE, place);
        exercise.put(KeyNameDB.TYPE_TITLE, type);
        exercise.put(KeyNameDB.EXERCISE_DATETIME, dateTime);
        exercise.put(KeyNameDB.DATE_OF_CREATE, dateOfCreate);

        db.collection(TypeCollection.EXERCISE_COLLECTION)
                .add(exercise)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Exercise userExercise = new Exercise(documentReference.getId(), title, description, dateTime, place, type, dateOfCreate);
                        setExerciseForCurrentUser(userExercise);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("MyLog", "Error adding exercise to db", e);
                    }
                });
    }

    private void setExerciseForCurrentUser(Exercise userExercise) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Map<String, String> userExercises = new HashMap<>();
            userExercises.put(KeyNameDB.EXERCISE_ID, userExercise.getId());
            userExercises.put(KeyNameDB.USER_ID, user.getUid());

            db.collection(TypeCollection.EXERCISE_TO_USER_COLLECTION)
                    .document()
                    .set(userExercises)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("MyLog", "Error add exercise for current user into db", e);
                        }
                    });
        }
    }

    public void onClickInviteFriends(View view) {
        Intent intent = new Intent(AddExerciseActivity.this, InviteFriendsToExerciseActivity.class);
        startActivity(intent);
        // TODO: добавить intent с ожиданием результата, если возвращается не нулевой список,
        //  то менять view на другой текст и записывать в список приглашенных пользователей
    }
}