package com.example.sportwithfriends.screens.fragments.exercise;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.time.LocalDateTime;
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
        editTextExerciseCountOfPlayers = findViewById(R.id.editTextExerciseCountOfPlayers);
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
        db.collection("types").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

        int countOfPlayers = 0;
        if (!editTextExerciseCountOfPlayers.getText().toString().trim().isEmpty())
            countOfPlayers = Integer.parseInt(editTextExerciseCountOfPlayers.getText().toString().trim());

        long dateTime = 0;
        if (!textViewTimeExercise.getText().toString().trim().isEmpty() && !textViewDateExercise.getText().toString().trim().isEmpty())
            dateTime = calendar.getTimeInMillis();

        long dateOfCreate = System.currentTimeMillis();

        if (!title.isEmpty() && !description.isEmpty() && !place.isEmpty()) {
            addExerciseToDB(title, description, place, type, countOfPlayers, dateTime, dateOfCreate);
            return true;

        }
        return false;
    }

    private void addExerciseToDB(String title, String description, String place, String type, int countOfPlayers, long dateTime, long dateOfCreate) {
        Map<String, Object> exercise = new HashMap<>();
        exercise.put("title", title);
        exercise.put("description", description);
        exercise.put("place", place);
        exercise.put("type", type);
        exercise.put("countOfPlayers", countOfPlayers);
        exercise.put("dateTime", dateTime);
        exercise.put("dateOfCreate", dateOfCreate);

        db.collection("exercises")
                .add(exercise)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Exercise userExercise = new Exercise(title, description, dateTime, place, type, countOfPlayers, dateOfCreate);
                        setCurrentUser(userExercise);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("MyLog", "Error adding exercise to db", e);
                    }
                });
    }

    private void setCurrentUser(Exercise userExercise) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String id = user.getUid();

            final DocumentReference docRef = db.collection("users").document(id);

            db.runTransaction(new Transaction.Function<Void>() {
                @Nullable
                @Override
                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(docRef);

                        List<Exercise> exercises = (List<Exercise>) snapshot.get("exercises");
                    if (exercises == null)
                        exercises = new ArrayList<>();

                    exercises.add(userExercise);

                    transaction.update(docRef, "exercises", exercises);
                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d("MyLog", "success update user exercises in db");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("MyLog", "error update user exercises in db", e);
                }
            });
        }
    }

    private void updateUserInDB(User user, String idUser) {

        if (!idUser.isEmpty()) {
            db.collection("users").document(idUser).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d("MyLog", "success update user");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("MyLog", "fail update user", e);
                }
            });
        }

    }
}