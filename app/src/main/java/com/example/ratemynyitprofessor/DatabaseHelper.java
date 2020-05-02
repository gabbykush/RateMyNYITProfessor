package com.example.ratemynyitprofessor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "RateMyNYITProfessor.db";
    public static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
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
}
