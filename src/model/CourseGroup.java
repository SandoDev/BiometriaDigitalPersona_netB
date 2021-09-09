package model;

import java.util.Arrays;

public class CourseGroup {

    int id = 0;
    String name = "";
    String subject = "";
    String teacher = "";
    String period = "";

    public CourseGroup(
            int id,
            String name,
            String subject,
            String teacher,
            String period
    ) {
        this.id = id;
        this.name = name;
        this.subject = subject;
        this.teacher = teacher;
        this.period = period;
    }
    
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return String.join(" - ", Arrays.asList(
            this.name,
            this.teacher,
            this.period,
            this.subject
        ));
    }

}
