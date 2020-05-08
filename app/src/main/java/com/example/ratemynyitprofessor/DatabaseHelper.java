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

        //create tables
        db.execSQL(DatabaseContract.CREATE_COURSE_TABLE);
        db.execSQL(DatabaseContract.CREATE_PROFESSOR_TABLE);
        db.execSQL(DatabaseContract.CREATE_REVIEW_TABLE);
        db.execSQL(DatabaseContract.CREATE_PROFESSOR_COURSE_TABLE);

        //add initial courses
        addCourse("CSCI 120", "Programming I");
        addCourse("CSCI 130", "Computer Organization");
        addCourse("CSCI 260", "Data Structures");
        addCourse("CSCI 330", "Operating Systems");
        addCourse("CSCI 345", "Computer Networks");

        //add initial professors
        addProfessor("A", "Mr.");
        addProfessor("B", "Mr.");
        addProfessor("C", "Mr.");
        addProfessor("D", "Mr.");
        addProfessor("E", "Mr.");

        //add initial reviews
        addReview("A", "Mr.", "CSCI 120", "Test A", 5, 5);
        addReview("A", "Mr.", "CSCI 120", "Test B", 3, 2);
        addReview("A", "Mr.", "CSCI 130", "Test C", 5, 5);
        addReview("B", "Mr.", "CSCI 120", "Test D", 5, 5);
        addReview("C", "Mr.", "CSCI 260", "Test E", 5, 5);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.CourseTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ProfessorTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ReviewTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ProfessorCourseTable.TABLE_NAME);
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
        contentValues.put(DatabaseContract.ProfessorTable.COL_RATING, 0);
        contentValues.put(DatabaseContract.ProfessorTable.COL_LEVEL_OF_DIFFICULTY, 0);
        //contentValues.put(DatabaseContract.ProfessorTable.COL_WOULD_TAKE_AGAIN, 0);
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
        contentValues.put(DatabaseContract.ReviewTable.COL_LIKES, 0);
        contentValues.put(DatabaseContract.ReviewTable.COL_DISLIKES, 0);
        long result = db.insert(DatabaseContract.ReviewTable.TABLE_NAME, null, contentValues);
        if(result == -1)
            return false;

        //update professor table
        Cursor overallRes = db.rawQuery(
                "select avg(Rating), avg(Difficulty) " +
                        "from " + DatabaseContract.ReviewTable.TABLE_NAME  +
                        " where (Professor_Last_Name = " + LastName + ") " +
                        "AND (Professor_First_Name = " + FirstName + ")",
                null);

        if(!updateProfessor(LastName, FirstName, overallRes.getFloat(0), overallRes.getFloat(1)))
            return false;

        //update professor course table
        Cursor ifExists = db.rawQuery("select * from " + DatabaseContract.ProfessorCourseTable.TABLE_NAME + " where Last_Name = ? AND First_Name = ? AND CourseID = ?", new String[] {LastName, FirstName, CourseID});
        Cursor courseRes = db.rawQuery("select avg(Rating), avg(Difficulty) " +
                        "from " + DatabaseContract.ReviewTable.TABLE_NAME +
                        " where (Professor_Last_Name = " + LastName + ") " +
                        "AND (Professor_First_Name = " + FirstName + ") " +
                        "AND (CourseID = " + CourseID + ")",
                null);

        if(ifExists.getCount() > 0) {
            if (!updateProfessorCourse(LastName, FirstName, CourseID, courseRes.getFloat(0), courseRes.getFloat(1)))
                return false;
        }

        else {
            if (!addProfessorCourse(LastName, FirstName, CourseID, courseRes.getFloat(0), courseRes.getFloat(1)))
                return false;
        }

        return true;

    }

    private boolean addProfessorCourse(String LastName, String FirstName, String CourseID, float Rating, float Difficulty){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.ProfessorCourseTable.COL_LAST_NAME, LastName);
        contentValues.put(DatabaseContract.ProfessorCourseTable.COL_FIRST_NAME, FirstName);
        contentValues.put(DatabaseContract.ProfessorCourseTable.COL_COURSEID, CourseID);
        contentValues.put(DatabaseContract.ProfessorCourseTable.COL_RATING, Rating);
        contentValues.put(DatabaseContract.ProfessorCourseTable.COL_LEVEL_OF_DIFFICULTY, Difficulty);
        long result = db.insert(DatabaseContract.ReviewTable.TABLE_NAME, null, contentValues);
        if(result == -1)
            return false;
        return true;
    }

    //update Professor data
    public boolean updateProfessor(String LastName, String FirstName, Float Rating, Float LOD){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.ProfessorTable.COL_LAST_NAME, LastName);
        contentValues.put(DatabaseContract.ProfessorTable.COL_FIRST_NAME, FirstName);
        contentValues.put(DatabaseContract.ProfessorTable.COL_RATING, Rating);
        contentValues.put(DatabaseContract.ProfessorTable.COL_LEVEL_OF_DIFFICULTY, LOD);
        //needs testing
        int res = db.update(DatabaseContract.ProfessorTable.TABLE_NAME, contentValues, "Last_Name = ?, First_Name = ?", new String[] {LastName, FirstName});
        if(res != 1)
            return false;
        return true;
    }

    private boolean updateProfessorCourse(String LastName, String FirstName, String CourseID, float Rating, float Difficulty){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.ProfessorCourseTable.COL_LAST_NAME, LastName);
        contentValues.put(DatabaseContract.ProfessorCourseTable.COL_FIRST_NAME, FirstName);
        contentValues.put(DatabaseContract.ProfessorCourseTable.COL_COURSEID, CourseID);
        contentValues.put(DatabaseContract.ProfessorCourseTable.COL_RATING, Rating);
        contentValues.put(DatabaseContract.ProfessorCourseTable.COL_LEVEL_OF_DIFFICULTY, Difficulty);
        int result = db.update(DatabaseContract.ProfessorCourseTable.TABLE_NAME, contentValues, "Last_Name = ?, First_Name = ?, CourseID = ?", new String[] {LastName, FirstName, CourseID});
        if(result != 1)
            return false;
        return true;
    }

    public Cursor getAllCourseData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + DatabaseContract.CourseTable.TABLE_NAME, null);
        return res;
    }

    public Cursor getAllProfessorData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + DatabaseContract.ProfessorTable.TABLE_NAME, null);
        return res;
    }

    public Cursor getAllReviewData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + DatabaseContract.ReviewTable.TABLE_NAME, null);
        return res;
    }

    public Cursor getAllProfessorCourseData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + DatabaseContract.ProfessorCourseTable.TABLE_NAME, null);
        return res;
    }

    public Cursor getSingleCourse(String CourseID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + DatabaseContract.CourseTable.TABLE_NAME + " where (CourseID = " + CourseID + ")", null);
        return res;
    }

    public  Cursor getSingleProfesserCourse(String LastName, String FirstName, String CourseID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + DatabaseContract.ProfessorCourseTable.TABLE_NAME + " where (Last_Name = ?) AND (First_Name = ?) AND (CourseID = ?)", new String[] {LastName, FirstName, CourseID});
        return res;
    }

    public Cursor getSingleProfessor(String LastName, String FirstName){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + DatabaseContract.ProfessorTable.TABLE_NAME + " where (Last_Name = " + LastName + ") and (First_Name = " + FirstName + ")", null);
        return res;
    }

    public Cursor getSingleReview(int ReviewID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + DatabaseContract.ReviewTable.TABLE_NAME + " where (ID = " + ReviewID + ")", null);
        return res;
    }
}
