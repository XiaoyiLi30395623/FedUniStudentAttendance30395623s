package au.edu.federation.itech3107.studentattendance30395623;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;


import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Attendance2 extends AppCompatActivity {

    private RecyclerView attendanceListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance2);

        final CompactCalendarView compactCalendarView = findViewById(R.id.compactcalendar_view);


        // Add an event
        Event ev1 = new Event(Color.RED, 1634256000000L, "Event at this time"); // timestamp is in milliseconds
        compactCalendarView.addEvent(ev1);

        // Listen for date changes
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                int year = dateClicked.getYear() + 1900;
                int month = dateClicked.getMonth() + 1;
                int day = dateClicked.getDate();

                String dateString = String.format("%d-%02d-%02d", year, month, day);
                Log.e("TAG", dateString);

                displayAttendance(dateString);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                Log.d("TAG", "Month was scrolled to: " + firstDayOfNewMonth);
            }
        });


       // DatePicker datePicker = findViewById(R.id.datePicker);
        attendanceListView = findViewById(R.id.attendanceListView);

//        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
//                new DatePicker.OnDateChangedListener() {
//                    @Override
//                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                        String selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth; // Format the date
//                        displayAttendance(selectedDate);
//                    }
//                });
    }

    private void displayAttendance(String date) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

//        Cursor cursor = databaseHelper.getAttendanceWithStudentInfo(date);




        Cursor cursor = databaseHelper.getAttendanceByDate(date);

        // Create an adapter to bind data to a list view
//        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
//                android.R.layout.simple_list_item_2,
//                cursor,
//                new String[]{DatabaseHelper.COLUMN_ATTENDANCE_STUDENT_ID, DatabaseHelper.COLUMN_ATTENDANCE_IS_PRESENT},
//                new int[]{android.R.id.text1, android.R.id.text2},
//                0);
        Log.e("TAG", cursor.toString());
         AttendanceAdapter adapter = new AttendanceAdapter(this, cursor);
        attendanceListView.setLayoutManager(new LinearLayoutManager(this));
        attendanceListView.setAdapter(adapter);
    }
}