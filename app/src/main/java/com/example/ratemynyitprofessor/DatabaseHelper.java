package com.example.ratemynyitprofessor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "RateMyNYITProfessor.db";
    public static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.CREATE_COURSE_TABLE);
        db.execSQL(DatabaseContract.CREATE_PROFESSOR_TABLE);
        db.execSQL(DatabaseContract.CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.CourseTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ProfessorTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ReviewTable.TABLE_NAME);
        onCreate(db);
    }

    public boolean addCourse(String CourseID, String CourseTitle){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.CourseTable.COL_COURSEID, CourseID);
        contentValues.put(DatabaseContract.CourseTable.COL_TITLE, CourseTitle);
        long result = db.insert(DatabaseContract.CourseTable.TABLE_NAME, null, contentValues);
        if(result == -1)
            return false;
        return true;
    }

    public boolean addProfessor(String LastName, String FirstName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.ProfessorTable.COL_LAST_NAME, LastName);
        contentValues.put(DatabaseContract.ProfessorTable.COL_FIRST_NAME, FirstName);
        long result = db.insert(DatabaseContract.ProfessorTable.TABLE_NAME, null, contentValues);
        if(result == -1)
            return false;
        return true;
    }

    public boolean addReview(String LastName, String FirstName, String CourseID, String Comment, float Rating, float Difficulty){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.ReviewTable.COL_PROF_LAST_NAME, LastName);
        contentValues.put(DatabaseContract.ReviewTable.COL_PROF_FIRST_NAME, FirstName);
        contentValues.put(DatabaseContract.ReviewTable.COL_COURSEID, CourseID);
        contentValues.put(DatabaseContract.ReviewTable.COL_COMMENT, Comment);
        contentValues.put(DatabaseContract.ReviewTable.COL_RATING, Rating);
        contentValues.put(DatabaseContract.ReviewTable.COL_DIFFICULTY, Difficulty);
        long result = db.insert(DatabaseContract.ReviewTable.TABLE_NAME, null, contentValues);
        if(result == -1)
            return false;
        return true;

    }

    public Cursor getCourseData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + DatabaseContract.CourseTable.TABLE_NAME, null);
        return res;
    }
}
