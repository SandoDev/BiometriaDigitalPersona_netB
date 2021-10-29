package model;

public class Inscription {

    int id;
    int grade;
    CourseGroup courseGroup;
    Student student;

    public Inscription(){}
    
    public Inscription(
        int id,
        int grade,
        CourseGroup courseGroup,
        Student student
    ){
        this.id = id;
        this.grade = grade;
        this.courseGroup = courseGroup;
        this.student = student;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public CourseGroup getCourseGroup() {
        return courseGroup;
    }

    public void setCourseGroup(CourseGroup courseGroup) {
        this.courseGroup = courseGroup;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
    
}
