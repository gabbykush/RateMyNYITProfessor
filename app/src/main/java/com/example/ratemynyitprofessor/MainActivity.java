package com.example.ratemynyitprofessor;

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
    EditText editCourseID, editCourseTitle;
    Button btnAddCourse, btnGetCourseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new DatabaseHelper(this);

        editCourseID = (EditText)findViewById(R.id.editText_courseid);
        editCourseTitle = (EditText)findViewById(R.id.editText_coursetitle);
        btnAddCourse = (Button)findViewById(R.id.button_add_course);
        btnGetCourseData = (Button)findViewById(R.id.button_showcoursedata);
        addCourse();
        getCourseData();

    }

    public void addCourse(){
        btnAddCourse.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isInserted = myDb.addCourse(editCourseID.getText().toString(),
                                editCourseTitle.getText().toString());
                        if(isInserted == true)
                            Toast.makeText(MainActivity.this, "Data Inserted", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this, "Data not inserted", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    public void getCourseData(){
        btnGetCourseData.setOnClickListener(
            new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Cursor res = myDb.getCourseData();
                    if(res.getCount() == 0){
                        showMessage("Error", "No data found.");
                        return;
                    }
                    StringBuffer buffer = new StringBuffer();
                    while (res.moveToNext()){
                        buffer.append("CourseID: " + res.getString(0) + "\n");
                        buffer.append("Course Title: " + res.getString(1) + "\n\n");
                    }

                    showMessage("Course Data", buffer.toString());
                }
            }
        );
    }

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
