package au.edu.federation.itech3107.studentattendance30395623;

import java.util.List;

public class Course {
    public int getCourseId() {
        return courseId;
    }


    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    private int courseId;
    private String courseName;
    private List<String> listDate;  // Add this line

    public Course(int courseId, String courseName, List<String> listDate) {  // Update the constructor
        this.courseId = courseId;
        this.courseName = courseName;
        this.listDate = listDate;  // Add this line
    }


    public String getCourseName() {
        return courseName;
    }

    public List<String> getListDate() {  // 新增 getter 方法
        return listDate;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", courseName='" + courseName + '\'' +
                ", listDate=" + listDate +
                '}';
    }
}
