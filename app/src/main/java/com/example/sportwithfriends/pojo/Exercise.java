package com.example.sportwithfriends.pojo;

public class Exercise {
    private String id;
    private String exerciseTitle;
    private String exerciseDescription;
    private long exerciseDateTime;
    private String exercisePlace;
    private String typeTitle;
    private long exerciseDateOfCreate;

    public Exercise(String id, String exerciseTitle, String exerciseDescription, long exerciseDateTime, String exercisePlace, String typeTitle, long exerciseDateOfCreate) {
        this.id = id;
        this.exerciseTitle = exerciseTitle;
        this.exerciseDescription = exerciseDescription;
        this.exerciseDateTime = exerciseDateTime;
        this.exercisePlace = exercisePlace;
        this.typeTitle = typeTitle;
        this.exerciseDateOfCreate = exerciseDateOfCreate;
    }

    public Exercise() {
    }

    public String getExerciseTitle() {
        return exerciseTitle;
    }

    public void setExerciseTitle(String exerciseTitle) {
        this.exerciseTitle = exerciseTitle;
    }

    public String getExerciseDescription() {
        return exerciseDescription;
    }

    public void setExerciseDescription(String exerciseDescription) {
        this.exerciseDescription = exerciseDescription;
    }

    public long getExerciseDateTime() {
        return exerciseDateTime;
    }

    public void setExerciseDateTime(long exerciseDateTime) {
        this.exerciseDateTime = exerciseDateTime;
    }

    public String getExercisePlace() {
        return exercisePlace;
    }

    public void setExercisePlace(String exercisePlace) {
        this.exercisePlace = exercisePlace;
    }

    public long getExerciseDateOfCreate() {
        return exerciseDateOfCreate;
    }

    public void setExerciseDateOfCreate(long exerciseDateOfCreate) {
        this.exerciseDateOfCreate = exerciseDateOfCreate;
    }

    public String getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
