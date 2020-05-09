package com.example.ratemynyitprofessor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    ///////////////////////////////////////////////////////////////////
    // Initialize Database Info
    public static final String DATABASE_NAME = "RateMyNYITProfessor.db";
    public static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    ///////////////////////////////////////////////////////////////////
    // On first-time startup of App, create Database
    @Override
    public void onCreate(SQLiteDatabase db) {

        //create tables
        db.execSQL(DatabaseContract.CREATE_COURSE_TABLE);
        db.execSQL(DatabaseContract.CREATE_PROFESSOR_TABLE);
        db.execSQL(DatabaseContract.CREATE_REVIEW_TABLE);
        db.execSQL(DatabaseContract.CREATE_PROFESSOR_COURSE_TABLE);

    }

    ///////////////////////////////////////////////////////////////////
    // On new versions of the database, delete database and re-create
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.CourseTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ProfessorTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ReviewTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ProfessorCourseTable.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.CourseTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ProfessorTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ReviewTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ProfessorCourseTable.TABLE_NAME);
        onCreate(db);
    }

    ////////////////////////////////////////////////////////////////////
    // addCourse:   creates a row for the Courses table and adds to table
    //
    // Arguments:   String CourseID : ID code of a course
    //              String CourseTitle : title given to course
    //
    // Returns:     true - if row is successfully created and added to table
    //              false - if error
    public boolean addCourse(String CourseID, String CourseTitle){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.CourseTable.COL_COURSEID, CourseID);
        contentValues.put(DatabaseContract.CourseTable.COL_TITLE, CourseTitle);

        long result = db.insert(DatabaseContract.CourseTable.TABLE_NAME, null, contentValues);
        if(result == -1) {
            return false;
        }
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // addProfessor :   creates a row for the Professors table and adds to table
    //
    // Arguments :      String LastName : Last name of the professor added
    //                  String FirstName : First name of the professor added
    //
    // Returns :        true - if row is successfully created and added to table
    //                  false - if error
    public boolean addProfessor(String LastName, String FirstName){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.ProfessorTable.COL_LAST_NAME, LastName);
        contentValues.put(DatabaseContract.ProfessorTable.COL_FIRST_NAME, FirstName);
        contentValues.put(DatabaseContract.ProfessorTable.COL_RATING, 0);
        contentValues.put(DatabaseContract.ProfessorTable.COL_LEVEL_OF_DIFFICULTY, 0);

        long result = db.insert(DatabaseContract.ProfessorTable.TABLE_NAME, null, contentValues);
        if(result == -1)
            return false;
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // addReview :   creates a row for the Reviews table and adds to table
    //               updates Professors and Professor_Course tables
    //
    // Arguments :      String LastName : Last name of the professor added
    //                  String FirstName : First name of the professor added
    //                  String CourseID : ID code for a course
    //                  String Comment : the comment that the reviewer wrote
    //                  float Rating : the quality rating the reviewer gave (out of 5)
    //                  float Difficulty : the level of difficulty that the reviewer gave (out of 5)
    //
    // Returns :        true - if row is successfully created and added to table
    //                          and if Professors and Professor_Course table are updated
    //                  false - if error
    public boolean addReview(String LastName, String FirstName, String CourseID,
                             String Comment, float Rating, float Difficulty){
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

        ////////////////////////////////////
        //get rating and difficulty averages from Reviews table
        Cursor overallRes = db.rawQuery(
                "select avg(Rating), avg(Difficulty) " +
                        "from " + DatabaseContract.ReviewTable.TABLE_NAME +
                        " where Professor_Last_Name = '" + LastName + "'" +
                        " and Professor_First_Name = '" + FirstName + "'",
                null);
        overallRes.moveToNext();

        //update Professors table
        if(!updateProfessor(LastName, FirstName, overallRes.getFloat(0), overallRes.getFloat(1)))
            return false;


        ////////////////////////////////////
        //check if entry in Professor_Course exists
        Cursor ifExists = db.rawQuery(
                "select * " +
                        "from " + DatabaseContract.ProfessorCourseTable.TABLE_NAME +
                        " where Last_Name = ? AND First_Name = ? AND CourseID = ?",
                new String[] {LastName, FirstName, CourseID});

        //get rating and difficulty course-specific averages from Reviews table
        Cursor courseRes = db.rawQuery("select avg(Rating), avg(Difficulty) " +
                        "from " + DatabaseContract.ReviewTable.TABLE_NAME +
                        " where Professor_Last_Name = '" + LastName + "' " +
                        "and Professor_First_Name = '" + FirstName + "' " +
                        "and CourseID = '" + CourseID + "'",
                null);
        courseRes.moveToNext();

        //if entry exists, update entry in table
        //else create new entry and add to table
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

    ////////////////////////////////////////////////////////////////////////////////
    // addProfessorCourse :   creates a row for the Professor_Course
    //                         table and adds to table
    //
    // Arguments :      String LastName : Last name of the professor added
    //                  String FirstName : First name of the professor added
    //                  String CourseID : ID code for a course
    //                  float Rating : the quality rating the reviewer gave (out of 5)
    //                  float Difficulty : the level of difficulty that the reviewer gave (out of 5)
    //
    // Returns :        true - if row is successfully created and added to table
    //                  false - if error
    private boolean addProfessorCourse(String LastName, String FirstName, String CourseID, float Rating, float Difficulty){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.ProfessorCourseTable.COL_LAST_NAME, LastName);
        contentValues.put(DatabaseContract.ProfessorCourseTable.COL_FIRST_NAME, FirstName);
        contentValues.put(DatabaseContract.ProfessorCourseTable.COL_COURSEID, CourseID);
        contentValues.put(DatabaseContract.ProfessorCourseTable.COL_RATING, Rating);
        contentValues.put(DatabaseContract.ProfessorCourseTable.COL_LEVEL_OF_DIFFICULTY, Difficulty);

        long result = db.insert(DatabaseContract.ProfessorCourseTable.TABLE_NAME, null, contentValues);
        if(result == -1)
            return false;
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // updateProfessor :   update an entry in the table with new values
    //
    // Arguments :      String LastName : Last name of the professor added
    //                  String FirstName : First name of the professor added
    //                  float Rating : the quality rating the reviewer gave (out of 5)
    //                  float Difficulty : the level of difficulty that the reviewer gave (out of 5)
    //
    // Returns :        true - if row is successfully updated
    //                  false - if error
    public boolean updateProfessor(String LastName, String FirstName, Float Rating, Float LOD){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.ProfessorTable.COL_LAST_NAME, LastName);
        contentValues.put(DatabaseContract.ProfessorTable.COL_FIRST_NAME, FirstName);
        contentValues.put(DatabaseContract.ProfessorTable.COL_RATING, Rating);
        contentValues.put(DatabaseContract.ProfessorTable.COL_LEVEL_OF_DIFFICULTY, LOD);

        int res = db.update(DatabaseContract.ProfessorTable.TABLE_NAME, contentValues,
                "Last_Name = ? and First_Name = ?",
                new String[] {LastName, FirstName});
        if(res != 1)
            return false;
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // updateProfessorCourse :   update an entry in the table with new values
    //
    // Arguments :      String LastName : Last name of the professor added
    //                  String FirstName : First name of the professor added
    //                  String CourseID : the ID code of the course
    //                  float Rating : the quality rating the reviewer gave (out of 5)
    //                  float Difficulty : the level of difficulty that the reviewer gave (out of 5)
    //
    // Returns :        true - if row is successfully updated
    //                  false - if error
    private boolean updateProfessorCourse(String LastName, String FirstName, String CourseID, float Rating, float Difficulty){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.ProfessorCourseTable.COL_LAST_NAME, LastName);
        contentValues.put(DatabaseContract.ProfessorCourseTable.COL_FIRST_NAME, FirstName);
        contentValues.put(DatabaseContract.ProfessorCourseTable.COL_COURSEID, CourseID);
        contentValues.put(DatabaseContract.ProfessorCourseTable.COL_RATING, Rating);
        contentValues.put(DatabaseContract.ProfessorCourseTable.COL_LEVEL_OF_DIFFICULTY, Difficulty);

        int result = db.update(DatabaseContract.ProfessorCourseTable.TABLE_NAME, contentValues,
                "Last_Name = ? and First_Name = ? and CourseID = ?",
                new String[] {LastName, FirstName, CourseID});
        if(result != 1)
            return false;
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // getAllCourseData :   get the data from all the rows in the Courses table
    //
    // Arguments :          none
    //
    // Return :             Cursor object with all data from the table

    //                      cursorname.getString(index) to get an attribute value in
    //                          currently selected row (use DatabaseContract class to
    //                          check index order
    //                      cursorname.moveToNext() to go to the next row of data

    public Cursor getAllCourseData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + DatabaseContract.CourseTable.TABLE_NAME, null);
        return res;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // getAllProfessorData :   get the data from all the rows in the Professors table
    //
    // Arguments :          none
    //
    // Return :             Cursor object with all data from the table

    //                      cursorname.getString(index) to get an attribute value in
    //                          currently selected row (use DatabaseContract class to
    //                          check index order
    //                      cursorname.moveToNext() to go to the next row of data

    public Cursor getAllProfessorData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + DatabaseContract.ProfessorTable.TABLE_NAME, null);
        return res;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // getAllReviewsData :   get the data from all the rows in the Reviews table
    //
    // Arguments :          none
    //
    // Return :             Cursor object with all data from the table

    //                      cursorname.getString(index) to get an attribute value in
    //                          currently selected row (use DatabaseContract class to
    //                          check index order
    //                      cursorname.moveToNext() to go to the next row of data

    public Cursor getAllReviewData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + DatabaseContract.ReviewTable.TABLE_NAME, null);
        return res;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // getAllProfessorCourseData :   get the data from all the rows in the
    //                                  Professor_Course table
    //
    // Arguments :          none
    //
    // Return :             Cursor object with all data from the table

    //                      cursorname.getString(index) to get an attribute value in
    //                          currently selected row (use DatabaseContract class to
    //                          check index order
    //                      cursorname.moveToNext() to go to the next row of data

    public Cursor getAllProfessorCourseData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + DatabaseContract.ProfessorCourseTable.TABLE_NAME, null);
        return res;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // getSingleCourse :   get the data from a specified row in the Courses table
    //
    // Arguments :          String CourseID - identifier for a row
    //
    // Return :             Cursor object with all data from the table

    //                      cursorname.getString(index) to get an attribute value in
    //                          currently selected row (use DatabaseContract class to
    //                          check index order

    public Cursor getSingleCourse(String CourseID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + DatabaseContract.CourseTable.TABLE_NAME + " where (CourseID = " + CourseID + ")", null);
        return res;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // getSingleProfessorCourse :   get the data from a specified row in
    //                                 the Professor_Course table
    //
    // Arguments :          String LastName - identifier for a row
    //                      String FirstName - identifier for a row
    //                      String CourseID - identifier for a row
    //
    // Return :             Cursor object with all data from the table

    //                      cursorname.getString(index) to get an attribute value in
    //                          currently selected row (use DatabaseContract class to
    //                          check index order


    public  Cursor getSingleProfesserCourse(String LastName, String FirstName, String CourseID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + DatabaseContract.ProfessorCourseTable.TABLE_NAME + " where (Last_Name = ?) AND (First_Name = ?) AND (CourseID = ?)", new String[] {LastName, FirstName, CourseID});
        return res;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // getSingleProfessor :   get the data from a specified row in the Professors table
    //
    // Arguments :          String LastName - identifier for a row
    //                      String FirstName - identifier for a row
    //
    // Return :             Cursor object with all data from the table

    //                      cursorname.getString(index) to get an attribute value in
    //                          currently selected row (use DatabaseContract class to
    //                          check index order

    public Cursor getSingleProfessor(String LastName, String FirstName){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + DatabaseContract.ProfessorTable.TABLE_NAME + " where (Last_Name = " + LastName + ") and (First_Name = " + FirstName + ")", null);
        return res;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // getSingleReview :   get the data from a specified row in the Reviews table
    //
    // Arguments :          int ReviewID - identifier for a row
    //                         **recommend storing the ID from getAllReviews
    //                              in some variable**
    //
    // Return :             Cursor object with all data from the table

    //                      cursorname.getString(index) to get an attribute value in
    //                          currently selected row (use DatabaseContract class to
    //                          check index order

    public Cursor getSingleReview(int ReviewID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + DatabaseContract.ReviewTable.TABLE_NAME + " where (ID = " + ReviewID + ")", null);
        return res;
    }
}
