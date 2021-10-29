package DAO;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Student;
import model.Database;

public class ParticipationDAO {
    
    Database con = new Database();
    
    public ParticipationDAO (){}
        
    public void registerParticipation(Integer inscription) throws SQLException{
        String query = "insert into participation "
                + "(`datetime`, `inscription_idInscription`) "
                + "values(current_timestamp(), ?)";
        Connection c = con.getConnection();
        PreparedStatement stm = c.prepareStatement(query);
        stm.setInt(1, inscription);
        stm.execute();
        stm.close();
        c.close();
    }

}