package com.example.sportwithfriends.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportwithfriends.R;
import com.example.sportwithfriends.pojo.Exercise;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private List<Exercise> exercises;

    public ExerciseAdapter() {
        exercises = new ArrayList<>();
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);

        holder.textViewExerciseTitle.setText(exercise.getTitle());
        holder.textViewExerciseDescription.setText(exercise.getDescription());
        holder.textViewPlaceOfExercise.setText(exercise.getPlace());
        holder.textViewTypeOfExercise.setText(exercise.getType());

        int count = exercise.getCountOfPlayer();
        if (count == 0)
            holder.textViewCountOfExercise.setVisibility(View.INVISIBLE);
        else {
            holder.textViewCountOfExercise.setVisibility(View.VISIBLE);
            holder.textViewCountOfExercise.setText(String.valueOf(count));
        }


        long dateTime = exercise.getDateTime();
        if (dateTime == 0) {
            holder.textViewTimeOfExercise.setVisibility(View.INVISIBLE);
            holder.textViewDateOfExercise.setVisibility(View.INVISIBLE);
        } else {
            // конвертируем миллисекунды в время вида (00:00)
            SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
            Date time = new Date(dateTime);
            holder.textViewTimeOfExercise.setVisibility(View.VISIBLE);
            holder.textViewTimeOfExercise.setText(formatTime.format(time));

            // конвертация миллисекунд в дату (7 марта 2023)
            SimpleDateFormat formatDate = new SimpleDateFormat("d MMMM yyyy");
            Date date = new Date(dateTime);
            holder.textViewDateOfExercise.setVisibility(View.VISIBLE);
            holder.textViewDateOfExercise.setText(formatDate.format(date));
        }

    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewExerciseTitle;
        private TextView textViewExerciseDescription;
        private TextView textViewTimeOfExercise;
        private TextView textViewDateOfExercise;
        private TextView textViewPlaceOfExercise;
        private TextView textViewTypeOfExercise;
        private TextView textViewCountOfExercise;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewExerciseTitle = itemView.findViewById(R.id.textViewExerciseTitle);
            textViewExerciseDescription = itemView.findViewById(R.id.textViewExerciseDescription);
            textViewTimeOfExercise = itemView.findViewById(R.id.textViewTimeOfExercise);
            textViewDateOfExercise = itemView.findViewById(R.id.textViewDateOfExercise);
            textViewPlaceOfExercise = itemView.findViewById(R.id.textViewPlaceOfExercise);
            textViewTypeOfExercise = itemView.findViewById(R.id.textViewTypeOfExercise);
            textViewCountOfExercise = itemView.findViewById(R.id.textViewCountOfExercise);

        }
    }
}
