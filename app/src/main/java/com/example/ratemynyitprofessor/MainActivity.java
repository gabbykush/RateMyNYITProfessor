package com.example.ratemynyitprofessor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new DatabaseHelper(this);
        //addCourse();
        //getCourseData();

        //add initial courses
        myDb.addCourse("CSCI 120", "Programming I");
        myDb.addCourse("CSCI 130", "Computer Organization");
        myDb.addCourse("CSCI 260", "Data Structures");
        myDb.addCourse("CSCI 330", "Operating Systems");
        myDb.addCourse("CSCI 345", "Computer Networks");

        //add initial professors
        myDb.addProfessor("A", "Mr.");
        myDb.addProfessor("B", "Mr.");
        myDb.addProfessor("C", "Mr.");
        myDb.addProfessor("D", "Mr.");
        myDb.addProfessor("E", "Mr.");

        //add initial reviews

        myDb.addReview("A", "Mr.", "CSCI 120", "Test A", 5, 5);
        myDb.addReview("A", "Mr.", "CSCI 120", "Test B", 3, 2);
        myDb.addReview("A", "Mr.", "CSCI 130", "Test C", 5, 5);
        myDb.addReview("B", "Mr.", "CSCI 120", "Test D", 5, 5);
        myDb.addReview("C", "Mr.", "CSCI 260", "Test E", 5, 5);


    }


}
