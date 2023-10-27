package au.edu.federation.itech3107.studentattendance30395623;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private List<Course> courses;
    private Context context;
    private OnCourseClickListener clickListener;

    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }

    public CourseAdapter(List<Course> courses, Context context, OnCourseClickListener clickListener) {
        this.courses = courses;
        this.context = context;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_item, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.tvCourseName.setText(course.getCourseName());

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteCourse(String.valueOf(course.getCourseId()))) {
                courses.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, courses.size() - position);
            }
        });



        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onCourseClick(course);
            }
        });
    }
    private boolean deleteCourse(String courseId) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        return dbHelper.deleteCourse(courseId);
    }
    @Override
    public int getItemCount() {
        return courses.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseName;
        Button btnDelete;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
