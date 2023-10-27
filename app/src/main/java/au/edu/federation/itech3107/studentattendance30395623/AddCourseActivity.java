package au.edu.federation.itech3107.studentattendance30395623;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class AddCourseActivity extends AppCompatActivity {
    private EditText edtCourseId, edtCourseName;
    private Button btnAddCourse;
    private DatabaseHelper dbHelper;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        EditText edtCourseId = findViewById(R.id.edtCourseId);
        EditText  edtCourseName = findViewById(R.id.edtCourseName);
        TextView tvStartDate = findViewById(R.id.tvStartDate);
        TextView  tvEndDate = findViewById(R.id.tvEndDate);
        Button  btnPickStartDate = findViewById(R.id.btnPickStartDate);
        Button  btnPickEndDate = findViewById(R.id.btnPickEndDate);
        Button  btnSave = findViewById(R.id.btnSave);
        // Set the click event for the start date picker

        btnPickStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(tvStartDate);


                Calendar cal = Calendar.getInstance();
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);
                cal.set(year, month, day);
                cal.add(Calendar.WEEK_OF_YEAR, 12);

                TextView displayEndDate = findViewById(R.id.tvEndDate);
                displayEndDate.setText(cal.get(Calendar.YEAR) + "-" +
                        (cal.get(Calendar.MONTH) + 1) + "-" +
                        cal.get(Calendar.DAY_OF_MONTH));
            }
        });
        // Set the click event for the end date picker
        //btnPickEndDate.setOnClickListener(v -> showDatePicker(tvEndDate));

        dbHelper = new DatabaseHelper(this);

        btnSave.setOnClickListener(v -> {
            String courseId = edtCourseId.getText().toString().trim();
            String courseName = edtCourseName.getText().toString().trim();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            String inputDate = tvStartDate.getText().toString();

            // Check if the date has a single-digit month or day
            String[] parts = inputDate.split("-");
            if (parts.length == 3) {
                if (parts[1].length() == 1) {
                    parts[1] = "0" + parts[1];  // Make up 0 to the month
                }
                if (parts[2].length() == 1) {
                    parts[2] = "0" + parts[2];  // Make up 0 to the date
                }
                inputDate = String.join("-", parts);
            }

            LocalDate currentStartDate = LocalDate.parse(inputDate, formatter);

            List<String> datesList = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                String formattedDate = currentStartDate.format(formatter);
                datesList.add(formattedDate);
                currentStartDate = currentStartDate.plusDays(7);
            }
            String datesString = String.join(",", datesList);
            boolean isInserted = dbHelper.insertCourse(courseId, courseName
                    ,tvStartDate.getText().toString(),tvEndDate.getText().toString(),datesString);
            if (isInserted) {
                Toast.makeText(AddCourseActivity.this, "Course Added Successfully", Toast.LENGTH_SHORT).show();
                edtCourseId.setText("");
                edtCourseName.setText("");
                finish();
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(AddCourseActivity.this, "Error Adding Course", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showDatePicker(TextView targetView) {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);

        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> targetView.setText(year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth),
                year, month, day);
        datePicker.show();


    }
}

