package com.example.activitytracker.database;

import androidx.room.Embedded;
import androidx.room.Relation;

public class RunPaceEffort {

    @Embedded
    public Run run;

    @Relation(parentColumn = "fk_effort_id", entityColumn = "_ID")
    public Effort effort;
}
