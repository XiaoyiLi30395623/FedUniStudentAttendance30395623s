package au.edu.federation.itech3107.studentattendance30395623;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "attendance.db";
    private static final int DATABASE_VERSION = 4;

    // Teacher table
    public static final String TABLE_TEACHER = "teachers";
    public static final String COLUMN_TEACHER_ID = "_id";
    public static final String COLUMN_TEACHER_USERNAME = "username";
    public static final String COLUMN_TEACHER_PASSWORD = "password";

    // Course
    public static final String TABLE_COURSE = "courses";
    public static final String COLUMN_COURSE_ID = "course_id";
    public static final String COLUMN_COURSE_NAME = "course_name";
    public static final String COLUMN_COURSE_START_DATE = "start_date";
    public static final String COLUMN_COURSE_END_DATE = "end_date";

    public static final String COLUMN_COURSE_LIST_DATE = "list_date";

    // Student table
    public static final String TABLE_STUDENT = "student_course";
    public static final String COLUMN_STUDENT_ID = "_id";
    public static final String COLUMN_STUDENT_IDS = "student_id";
    public static final String COLUMN_STUDENT_NAME = "student_name";
    public static final String COLUMN_STUDENT_COURSE_ID = "course_id";

    // Attendance sheets
    public static final String TABLE_ATTENDANCE = "attendance";
    public static final String COLUMN_ATTENDANCE_ID = "_id"; // Add a new column
    public static final String COLUMN_ATTENDANCE_DATE = "attendance_date";
    public static final String COLUMN_ATTENDANCE_STUDENT_ID = "student_id";
    public static final String COLUMN_ATTENDANCE_IS_PRESENT = "is_present";
    public static final String COLUMN_ATTENDANCE_COURSE_ID = "course_id";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTeacherTable = "CREATE TABLE " + TABLE_TEACHER + "("
                + COLUMN_TEACHER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TEACHER_USERNAME + " TEXT,"
                + COLUMN_TEACHER_PASSWORD + " TEXT)";

        String createCourseTable = "CREATE TABLE " + TABLE_COURSE + "("
                + COLUMN_COURSE_ID + " TEXT PRIMARY KEY,"
                + COLUMN_COURSE_NAME + " TEXT,"
                + COLUMN_COURSE_START_DATE + " TEXT,"
                + COLUMN_COURSE_END_DATE + " TEXT,"
                + COLUMN_COURSE_LIST_DATE + " TEXT "+ ")";


        String createStudentTable = "CREATE TABLE " + TABLE_STUDENT + "("
                + COLUMN_STUDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_STUDENT_IDS + " INTEGER UNIQUE,"
                + COLUMN_STUDENT_NAME + " TEXT,"
                + COLUMN_STUDENT_COURSE_ID + " INTEGER,"
                + "FOREIGN KEY (" + COLUMN_STUDENT_COURSE_ID + ") REFERENCES " + TABLE_COURSE + "(" + COLUMN_COURSE_ID + ") ON DELETE CASCADE)";


        String createAttendanceTable = "CREATE TABLE " + TABLE_ATTENDANCE + "("
                + COLUMN_ATTENDANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ATTENDANCE_DATE + " TEXT,"
                + COLUMN_ATTENDANCE_STUDENT_ID + " INTEGER,"
                + COLUMN_ATTENDANCE_IS_PRESENT + " INTEGER,"
                + COLUMN_ATTENDANCE_COURSE_ID + " INTEGER,"
                + "FOREIGN KEY (" + COLUMN_ATTENDANCE_STUDENT_ID + ") REFERENCES student_course(student_id) ON DELETE CASCADE,"
                + "FOREIGN KEY (" + COLUMN_ATTENDANCE_COURSE_ID + ") REFERENCES " + TABLE_COURSE + "(" + COLUMN_COURSE_ID + ") ON DELETE CASCADE)";

        db.execSQL(createTeacherTable);
        db.execSQL(createCourseTable);
        db.execSQL(createStudentTable);
        db.execSQL(createAttendanceTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        // Create tables again
        onCreate(db);
    }


    // Add a new teacher
    public boolean insertTeacher(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TEACHER_USERNAME, username);
        contentValues.put(COLUMN_TEACHER_PASSWORD, PasswordUtil.hashPassword(password));
        long result = db.insert(TABLE_TEACHER, null, contentValues);
        db.close();
        return result != -1;
    }


    @SuppressLint("Range")
    public String getCourseNameById(String courseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_COURSE,
                new String[]{COLUMN_COURSE_NAME},
                COLUMN_COURSE_ID + "=?",
                new String[]{courseId},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String courseName = cursor.getString(cursor.getColumnIndex(COLUMN_COURSE_NAME));
            cursor.close();
            db.close();
            return courseName;
        }

        db.close();
        return null;
    }



    // Add a new course
    public boolean insertCourse(String courseId, String courseName,String startDate,String endDate,String listData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_COURSE_ID, courseId);
        contentValues.put(COLUMN_COURSE_NAME, courseName);
        contentValues.put(COLUMN_COURSE_START_DATE, startDate);
        contentValues.put(COLUMN_COURSE_END_DATE, endDate);
        contentValues.put(COLUMN_COURSE_LIST_DATE, listData.toString());
        long result = -1;
        try {
            result = db.insertOrThrow(TABLE_COURSE, null, contentValues);
        } catch (SQLException e) {
            Log.e("DBHelper", "Error inserting into course table", e);
        }

        db.close();
        return result != -1;
    }



    // Delete a course
    public boolean deleteCourse(String courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Convert int courseId to String
        int result = db.delete(TABLE_COURSE, COLUMN_COURSE_ID + "=?", new String[]{String.valueOf(courseId)});
        db.close();
        return result > 0;
    }


    @SuppressLint("Range")
    public Course getCourseById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM courses WHERE course_id = ?", new String[]{String.valueOf(id)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int courseId = cursor.getInt(cursor.getColumnIndex(COLUMN_COURSE_ID));
                String courseName = cursor.getString(cursor.getColumnIndex(COLUMN_COURSE_NAME));

                // Gets the listDate string and converts it <String>to List
                String listDateString = cursor.getString(cursor.getColumnIndex(COLUMN_COURSE_LIST_DATE));
                List<String> listDate = new ArrayList<>(Arrays.asList(listDateString.split(",")));

                cursor.close();
                return new Course(courseId, courseName, listDate); // Modify the constructor to fit the actual definition of the Course class
            }
            cursor.close();
        }
        return null;
    }



    public Cursor getAllCourses() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_COURSE, null);
    }

    //Gets the start date of the course and sorts it by start date
    @SuppressLint("Range")
    public List<String> getCourseStartDatesLimited() {
        List<String> startDateList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_COURSE, new String[]{COLUMN_COURSE_START_DATE},
                null, null, null, null, COLUMN_COURSE_START_DATE + " ASC", "12");

        while (cursor.moveToNext()) {
            String startDate = cursor.getString(cursor.getColumnIndex(COLUMN_COURSE_START_DATE));
            startDateList.add(startDate);
        }
        cursor.close();
        return startDateList;
    }


    public Cursor getStudentsByCourseId(int courseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT _id, student_id, student_name, course_id FROM " + TABLE_STUDENT + " WHERE course_id = ?";
        return db.rawQuery(selectQuery, new String[]{String.valueOf(courseId)});
    }
    // Add a new student
    public long insertStudent(String studentId ,String name, long courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_IDS, studentId);
        values.put(COLUMN_STUDENT_NAME, name);
        values.put(COLUMN_STUDENT_COURSE_ID, courseId);
        long id = db.insertOrThrow(TABLE_STUDENT, null, values);
        db.close();
        return id;
    }

    // Query students
    @SuppressLint("Range")
    public Cursor getStudent(String ids) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_STUDENT + " WHERE " + COLUMN_STUDENT_IDS + " = ?";
        Cursor studentCursor = db.rawQuery(selectQuery, new String[]{ids});

        if (studentCursor.moveToFirst()) {
            Log.d("DBHelper", "Found student: " + studentCursor.getString(studentCursor.getColumnIndex(COLUMN_STUDENT_NAME)));
        } else {
            Log.d("DBHelper", "Student not found with ID: " + ids);
        }

        return studentCursor;
    }



    // Delete a student
    public void deleteStudent(long studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STUDENT, COLUMN_STUDENT_ID + " = ?", new String[]{String.valueOf(studentId)});
        db.close();
    }

    // Get all students
    public Cursor getAllStudents() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_STUDENT, null);
    }

    //Look up student information by student name
    public Cursor getStudentByName(SQLiteDatabase db, String studentName) {
        String selection = COLUMN_STUDENT_NAME + " = ?";
        String[] selectionArgs = { studentName };

        return db.query(
                TABLE_STUDENT,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }





    //Add attendance records
    public boolean insertAttendance(String date, int studentId, boolean isPresent,int course_id) {
        Log.e("get insertAttendance :",date+"   studentId--"+studentId+"course_id---"+course_id);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ATTENDANCE_DATE, date);
        contentValues.put(COLUMN_ATTENDANCE_STUDENT_ID, studentId);
        contentValues.put(COLUMN_ATTENDANCE_IS_PRESENT, isPresent ? 1 : 0);
        contentValues.put(COLUMN_ATTENDANCE_COURSE_ID,course_id);
        long result = db.insert(TABLE_ATTENDANCE, null, contentValues);
        db.close();
        return result != -1; // Returns whether the insertion was successful
    }





    //Delete attendance records
    public boolean deleteAttendance(String date, int studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_ATTENDANCE,
                COLUMN_ATTENDANCE_DATE + "=? AND " + COLUMN_ATTENDANCE_STUDENT_ID + "=?",
                new String[]{date, String.valueOf(studentId)});
        db.close();
        return rowsDeleted > 0; // Returns whether a record has been deleted
    }

    //Check attendance
    public Cursor getAttendanceByDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_ATTENDANCE, null,
                COLUMN_ATTENDANCE_DATE + "=?",
                new String[]{date}, null, null, null);
    }


    ///Verify your instructor's sign-in information
    public boolean validateTeacher(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TEACHER, new String[]{COLUMN_TEACHER_PASSWORD},
                COLUMN_TEACHER_USERNAME + "=?", new String[]{username}, null, null, null);

        int passwordIndex = cursor.getColumnIndex(COLUMN_TEACHER_PASSWORD);
        if (passwordIndex != -1 && cursor.moveToFirst()) {
            String storedPasswordHash = cursor.getString(passwordIndex);
            cursor.close();
            return storedPasswordHash.equals(PasswordUtil.hashPassword(password)); // Check hash
        } else {
            return false;
        }

    }


    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Enable foreign key support
        db.execSQL("PRAGMA foreign_keys=ON;");
    }
}
