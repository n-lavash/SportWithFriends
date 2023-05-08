package com.example.sportwithfriends.screens.fragments.exercise;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportwithfriends.R;
import com.example.sportwithfriends.adapters.ExerciseAdapter;
import com.example.sportwithfriends.constants.KeyNameDB;
import com.example.sportwithfriends.constants.TypeCollection;
import com.example.sportwithfriends.pojo.Exercise;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// TODO: добавить в onResume сохранение фрагмента

public class ExerciseFragment extends Fragment {

    private RecyclerView recyclerViewExercises;
    private FloatingActionButton buttonAddExercise;
    private ExerciseAdapter exerciseAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public ExerciseFragment() {
    }

    public static ExerciseFragment newInstance() {
        return new ExerciseFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerViewExercises = requireActivity().findViewById(R.id.recyclerViewExercises);
        buttonAddExercise = requireActivity().findViewById(R.id.buttonAddExercise);

        exerciseAdapter = new ExerciseAdapter();

        recyclerViewExercises.setAdapter(exerciseAdapter);

        recyclerViewExercises.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            setExerciseAdapter(currentUser);
        }


        buttonAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), AddExerciseActivity.class);
                startActivity(intent);
            }
        });
    }

    private interface OnExercisesLoadedListener {
        void onExercisesLoaded(List<Exercise> exercises);
    }

    private void setExerciseAdapter(FirebaseUser currentUser) {
        final String userId = currentUser.getUid();

        db.collection(TypeCollection.EXERCISE_TO_USER_COLLECTION)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            List<DocumentSnapshot> snapshots = value.getDocuments();
                            List<String> exerciseId = new ArrayList<>();
                            for (DocumentSnapshot document :
                                    snapshots) {
                                if (Objects.equals(document.get(KeyNameDB.USER_ID), userId))
                                    exerciseId.add(Objects.requireNonNull(document.get(KeyNameDB.EXERCISE_ID)).toString());
                            }
                            if (!exerciseId.isEmpty()) {
                                setUsersExercises(exerciseId);
                            }
                        }
                    }
                });
    }

    private void setUsersExercises(List<String> exerciseId) {
        db.collection(TypeCollection.EXERCISE_COLLECTION)
                .whereIn(FieldPath.documentId(), exerciseId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Exercise> exercises = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Exercise exercise = document.toObject(Exercise.class);
                                exercises.add(exercise);
                            }
                            exerciseAdapter.setExercises(exercises);
                        } else {
                            Log.d("MyLog", "Error getting exercises for current user: ", task.getException());
                        }
                    }
                });
    }

}