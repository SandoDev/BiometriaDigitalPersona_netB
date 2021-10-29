package DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.CourseGroup;
import model.Database;

public class CourseGroupDAO {
	
    Database con = new Database();
    
    public List<CourseGroup> getAll() throws SQLException{
        List<CourseGroup> courses = new ArrayList<>();
        String sql = "select cg.idCourseGroup as id, " +
            "cg.name as name, " +
            "s.description as subject, " +
            "CONCAT(t.name, ' ', t.lastName) as teacher, " +
            "CONCAT(p.year, '-', p.period) as period " +
                "from CourseGroup cg " +
                "inner join Teacher t on t.idTeacher = cg.teacher_idTeacher " +
                "inner join Subject s on s.idSubject = cg.subject_idSubject " +
                "inner join Period p on p.idPeriod = cg.period_idPeriod;";

        Connection c = con.getConnection();
        Statement stm = c.createStatement();
        ResultSet rs = stm.executeQuery(sql);
        while (rs.next()) {
            CourseGroup courseGroup = new CourseGroup(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("subject"),
                rs.getString("teacher"),
                rs.getString("period")
            );
            courses.add(courseGroup);
        }
        stm.close();
        rs.close();
        c.close();
        con.disconnect();
        return courses;
    }

}