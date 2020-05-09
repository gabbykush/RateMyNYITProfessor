package com.example.ratemynyitprofessor;

import android.provider.BaseColumns;

public class DatabaseContract {

    private DatabaseContract() {}

    /////////////////////////////////////////////////////////////////////////////
    //Course Table
    //Table Name: Courses
    //Attributes:
    //  CourseID
    //  Title
    public static final class CourseTable implements BaseColumns {
        public static final String TABLE_NAME = "Courses";
        public static final String COL_COURSEID = "CourseID";
        public static final String COL_TITLE = "Title";
    }

    //Create Courses Table SQL Query
    public static final String CREATE_COURSE_TABLE =
            "create table " + CourseTable.TABLE_NAME+
                    " (" +
                    CourseTable.COL_COURSEID + " TEXT PRIMARY KEY,"+
                    CourseTable.COL_TITLE + " TEXT"+
                    ")";

    ////////////////////////////////////////////////////////////////////////////////////
    //Professor Table (Overall Scores)
    //Table Name: Professors
    //Attributes:
    //  Last_Name
    //  First_Name
    //  Rating (Overall) ~ Depends on Reviews Data
    //  Level_of_Difficulty (Overall) ~ Depends on Reviews Data
    public static final class ProfessorTable implements BaseColumns {
        public static final String TABLE_NAME = "Professors";
        public static final String COL_LAST_NAME = "Last_Name";
        public static final String COL_FIRST_NAME = "First_Name";
        public static final String COL_RATING = "Rating";
        public static final String COL_LEVEL_OF_DIFFICULTY = "Level_of_Difficulty";
    }

    //Create Professors Table SQL Query
    public static final String CREATE_PROFESSOR_TABLE =
            "create table " + ProfessorTable.TABLE_NAME+
                    " (" +
                    ProfessorTable.COL_LAST_NAME + " TEXT NOT NULL,"+
                    ProfessorTable.COL_FIRST_NAME + " TEXT NOT NULL,"+
                    ProfessorTable.COL_RATING + " FLOAT,"+
                    ProfessorTable.COL_LEVEL_OF_DIFFICULTY + " FLOAT,"+
                    "PRIMARY KEY(" + ProfessorTable.COL_LAST_NAME + ", " + ProfessorTable.COL_FIRST_NAME + ")" +
                    ")";

    //////////////////////////////////////////////////////////////////////////
    //Review Table
    //Table Name: Reviews
    //Attributes
    // ID
    // Professor_Last_Name
    // Professor_First_Name
    // CourseID
    // Comment
    // Rating
    // Difficulty
    // Likes
    // Dislikes
    public static final class ReviewTable implements BaseColumns {
        public static final String TABLE_NAME = "Reviews";
        public static final String COL_ID = "ID";
        public static final String COL_PROF_LAST_NAME = "Professor_Last_Name";
        public static final String COL_PROF_FIRST_NAME = "Professor_First_Name";
        public static final String COL_COURSEID = "CourseID";
        public static final String COL_COMMENT = "Comment";
        public static final String COL_RATING = "Rating";
        public static final String COL_DIFFICULTY = "Difficulty";
        public static final String COL_LIKES = "Likes";
        public static final String COL_DISLIKES = "Dislikes";

    }

    //Crate Reviews Table SQL Query
    public static final String CREATE_REVIEW_TABLE =
            "create table " + ReviewTable.TABLE_NAME+
                    " (" +
                    ReviewTable.COL_ID + " INTEGER PRIMARY KEY,"+
                    ReviewTable.COL_PROF_LAST_NAME + " TEXT NOT NULL,"+
                    ReviewTable.COL_PROF_FIRST_NAME + " TEXT NOT NULL,"+
                    ReviewTable.COL_COURSEID + " TEXT NOT NULL,"+
                    ReviewTable.COL_COMMENT + " TEXT,"+
                    ReviewTable.COL_RATING + " FLOAT,"+
                    ReviewTable.COL_DIFFICULTY + " FLOAT,"+
                    ReviewTable.COL_LIKES + " INTEGER,"+
                    ReviewTable.COL_DISLIKES + " INTEGER"+
                    ")";

    //////////////////////////////////////////////////////////////////////////
    //Professor Course Table (Course Specific Scores)
    //Table Name: Professor_Course
    //Attributes:
    // Last_Name
    // First_Name
    // CourseID
    // Rating (Course Specific) ~ Depends on Reviews Data
    // Level_of_Difficulty (Course Specific) ~ Depends on Reviews Data
    public static final class ProfessorCourseTable implements BaseColumns {
        public static final String TABLE_NAME = "Professor_Course";
        public static final String COL_LAST_NAME = "Last_Name";
        public static final String COL_FIRST_NAME = "First_Name";
        public static final String COL_COURSEID = "CourseID";
        public static final String COL_RATING = "Rating";
        public static final String COL_LEVEL_OF_DIFFICULTY = "Level_of_Difficulty";
    }

    //Create Professor_Course Table SQL Query
    public static final String CREATE_PROFESSOR_COURSE_TABLE =
            "create table " + ProfessorCourseTable.TABLE_NAME+
                    " (" +
                    ProfessorCourseTable.COL_LAST_NAME + " TEXT NOT NULL,"+
                    ProfessorCourseTable.COL_FIRST_NAME + " TEXT NOT NULL,"+
                    ProfessorCourseTable.COL_COURSEID + " TEXT NOT NULL,"+
                    ProfessorCourseTable.COL_RATING + " FLOAT,"+
                    ProfessorCourseTable.COL_LEVEL_OF_DIFFICULTY + " FLOAT,"+
                    "PRIMARY KEY (" + ProfessorCourseTable.COL_LAST_NAME + ", " + ProfessorCourseTable.COL_FIRST_NAME + ", " + ProfessorCourseTable.COL_COURSEID + ")" +
                    ")";
}
