package DAO;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Database;
import model.CourseGroup;
import model.Student;
import model.Inscription;

public class InscriptionDAO {
    
    List<Student> students = new ArrayList<>();
    Student student = new Student();
	
    Database con = new Database();
    
    public Inscription getOne(CourseGroup course, Student student) throws SQLException{
        String sql = "select " +
            "i.idInscription as id, " +
            "i.grade as grade " +
                "from inscription i " +
                "where i.courseGroup_idCourseGroup = ? and " +
                "i.student_idStudent = ?" +
                ";";
        Connection c = con.getConnection();
        PreparedStatement stm = c.prepareStatement(sql);
        stm.setInt(1, course.getId());
        stm.setInt(2, student.getId());
        ResultSet rs = stm.executeQuery();

        Inscription inscription = null;
        while (rs.next()) {
            inscription = new Inscription(
                rs.getInt("id"),
                rs.getInt("grade"),
                course,
                student
            );
        }
        stm.close();
        rs.close();
        c.close();
        return inscription;
    }


}