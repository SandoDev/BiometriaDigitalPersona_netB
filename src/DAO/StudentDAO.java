package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Student;
import model.Database;

public class StudentDAO {
    
    List<Student> students = new ArrayList<>();
	
    Database con = new Database();
    
    public StudentDAO (int courseGroupId) throws SQLException{
        this.init(courseGroupId);
    }
    
    private void init(int courseGroupId) throws SQLException{
        String sql = "select s.idStudent as id, " +
            "s.name as name, " +
            "s.lastName as lastName, " +
            "s.email as email, " +
            "s.identification as identification, " +
            "s.codeInstitutional as codeInstitutional, " +
            "s.phone as phone " +
                "from Student s " +
                "inner join Inscription i on i.student_idStudent = s.idStudent " +
                "inner join CourseGroup cg on cg.idCourseGroup = i.courseGroup_idCourseGroup " +
                "where cg.idCourseGroup = ?;";
        Connection c = con.getConnection();
        PreparedStatement stm = c.prepareStatement(sql);
        stm.setInt(1, courseGroupId);
        ResultSet rs = stm.executeQuery();
        while (rs.next()) {
            Student student = new Student(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("lastName"),
                rs.getString("email"),
                rs.getString("identification"),
                rs.getString("codeInstitutional"),
                rs.getString("phone")
            );
            this.students.add(student);
        }
        stm.close();
        rs.close();
        c.close();
    }
    
    public List<Student> getAll() {
        return this.students;
    }

}