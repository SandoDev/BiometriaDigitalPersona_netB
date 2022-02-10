package DAO;

import static java.lang.System.err;
import static java.lang.System.out;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Student;
import model.Database;

public class StudentDAO {
	
    Database con = new Database();
    
    public List<Student> getByCourseGroup(int courseGroupId) throws SQLException{
        List<Student> students = new ArrayList<>();
        Connection c = con.getConnection();
        String sql = "select s.idStudent as id, " +
            "s.name as name, " +
            "s.lastName as lastName, " +
            "s.email as email, " +
            "s.identification as identification, " +
            "s.codeInstitutional as codeInstitutional, " +
            "s.phone as phone, " +
            "s.fingerprint as fingerprint " +
                "from Student s " +
                "inner join Inscription i on i.student_idStudent = s.idStudent " +
                "inner join CourseGroup cg on cg.idCourseGroup = i.courseGroup_idCourseGroup " +
                "where cg.idCourseGroup = ?;";
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
                rs.getString("phone"),
                rs.getBytes("fingerprint")
            );
            students.add(student);
        }
        stm.close();
        rs.close();
        c.close();
        this.con.disconnect();
        return students;
    }
    
    public int saveFingerprint(Student student) throws SQLException {
        Connection c = con.getConnection();
        String query = "update Student set fingerprint=? where identification=" + student.getIdentification();
        PreparedStatement stm = c.prepareStatement(query);
        stm.setBinaryStream(1, student.getFingerprintData(), student.getFingerprintSize());
        int rowCount = stm.executeUpdate();
        
        stm.close();
        c.close();
        this.con.disconnect();
        return rowCount;
    }
}