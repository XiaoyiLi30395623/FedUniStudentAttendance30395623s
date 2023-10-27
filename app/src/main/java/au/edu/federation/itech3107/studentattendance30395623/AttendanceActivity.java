package au.edu.federation.itech3107.studentattendance30395623;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class AttendanceActivity extends AppCompatActivity {

    private Spinner dateSpinner;
    private RecyclerView studentsRecyclerView;
    private StudentAttendanceAdapter adapter;
    private DatabaseHelper dbHelper;
    private int courseId;
    private Cursor studentsByCourseId;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        courseId = getIntent().getIntExtra("COURSE_ID", -1);


        dbHelper = new DatabaseHelper(this);

        dateSpinner = findViewById(R.id.dateSpinner);
        studentsRecyclerView = findViewById(R.id.studentsRecyclerView);
        Button saveAttendanceButton = findViewById(R.id.saveAttendanceButton);

        List<Student> students = getStudentsFromDatabase(courseId);

        adapter = new StudentAttendanceAdapter(this,students);
        studentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        studentsRecyclerView.setAdapter(adapter);

        saveAttendanceButton.setOnClickListener(v -> saveAttendance());


        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        new Thread(new Runnable() {
            @Override
            public void run() {

                Course courseByBean = databaseHelper.getCourseById(courseId);
                Log.e("Get the course ID",courseId+"");
                List<String> listDate = courseByBean.getListDate();
                studentsByCourseId = databaseHelper.getStudentsByCourseId(courseId);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(AttendanceActivity.this,
                                android.R.layout.simple_spinner_item, listDate);
                        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dateSpinner.setAdapter(dateAdapter);
                    }
                });


            }
        }).start();


    }
    @SuppressLint("Range")
    private List<Student> getStudentsFromDatabase(int courseId) {
        List<Student> students = new ArrayList<>();
        Cursor cursor =   dbHelper.getStudentsByCourseId(courseId);
        // Cursor cursor = dbHelper.getAllStudents();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STUDENT_IDS));
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STUDENT_NAME));
            students.add(new Student(id, name));
        }
        cursor.close();

        return students;
    }

    private void saveAttendance() {
        String selectedDate = dateSpinner.getSelectedItem().toString();
        SparseBooleanArray attendance = adapter.getAttendanceState();
        List<Student> students = adapter.getStudents(); // 获取学生列表

        for (Student student : students) {
            int studentId = student.getId();
            boolean isPresent = attendance.get(studentId, false); // This will return 'false' if there's no entry for the studentId
            dbHelper.insertAttendance(selectedDate, studentId, isPresent, courseId);
        }

        Toast.makeText(this, "Attendance Saved", Toast.LENGTH_SHORT).show();
    }

}
