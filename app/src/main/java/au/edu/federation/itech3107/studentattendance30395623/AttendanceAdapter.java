package au.edu.federation.itech3107.studentattendance30395623;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {
    private Context context;
    private Cursor cursor;
    private DatabaseHelper databaseHelper;

    public AttendanceAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.attendance_item, parent, false);
        return new AttendanceViewHolder(view);
    }

    @SuppressLint("Range")
    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            int studentId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ATTENDANCE_STUDENT_ID));
            Log.e("Get the attendance database from:", String.valueOf(studentId));
            Cursor studentCursor = databaseHelper.getStudent(String.valueOf(studentId));
            if (studentCursor.moveToFirst()) {
                String studentName = studentCursor.getString(studentCursor.getColumnIndex(DatabaseHelper.COLUMN_STUDENT_IDS));
                holder.studentNameTextView.setText(studentName);
                String studentCourseId = studentCursor.getString(studentCursor.getColumnIndex(DatabaseHelper.COLUMN_STUDENT_COURSE_ID));
                String courseName = databaseHelper.getCourseNameById(studentCourseId);
                Log.e("Get the student Course ID in the attendance database:", studentCourseId);
                Log.e("Get the course name in the attendance database:", courseName);
            } else {
                holder.studentNameTextView.setText("Unknown Student");
            }
            studentCursor.close();
            int isPresent = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ATTENDANCE_IS_PRESENT));
            holder.attendanceStatusTextView.setText(isPresent == 1 ? "Attendance" : "NO Attendance");
        }
    }

    static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView studentNameTextView;
        TextView attendanceStatusTextView;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            studentNameTextView = itemView.findViewById(R.id.studentNameTextView);
            attendanceStatusTextView = itemView.findViewById(R.id.attendanceStatusTextView);
        }
    }
}
