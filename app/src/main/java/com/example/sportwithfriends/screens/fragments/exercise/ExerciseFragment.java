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
import com.example.sportwithfriends.pojo.Exercise;
import com.example.sportwithfriends.pojo.News;
import com.example.sportwithfriends.pojo.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
        exerciseAdapter.notifyDataSetChanged();

        recyclerViewExercises.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            setAdapterIntoRecyclerView(currentUser);
        }


        buttonAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), AddExerciseActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setAdapterIntoRecyclerView(FirebaseUser currentUser) {
        final String id = currentUser.getUid();
        db.collection("users")
                .document(id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (value != null) {
                            User user = value.toObject(User.class);
                            if (user != null) {
                                List<Exercise> exercises = user.getExercises();

                                if (exercises != null) {
                                    exercises.sort(new Comparator<Exercise>() {
                                        @Override
                                        public int compare(Exercise o1, Exercise o2) {
                                            return Long.compare(o2.getDateOfCreate(), o1.getDateOfCreate());
                                        }
                                    });
                                    exerciseAdapter.setExercises(exercises);
                                    Log.d("MyLog", "set list of exercise in adapter");
                                }
                            }
                        }
                    }
                });
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            User user = task.getResult().toObject(User.class);
//
//                            if (user != null) {
//                                List<Exercise> exercises = user.getExercises();
//                                if (exercises != null) {
//                                    exerciseAdapter.setExercises(exercises);
//                                    Log.d("MyLog", "set adapter");
//                                }
//                            }
//                        } else {
//                            Log.d("MyLog", "error get exercises by user from db", task.getException());
//                        }
//                    }
//                });

//        db.collection("exercises")
//                .orderBy("dateOfCreate")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            List<Exercise> exercises = task.getResult().toObjects(Exercise.class);
//                        }
//                    }
//                });
    }
}