package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import model.Database;

public class ParticipationDAO {
    
    Database con = new Database();
        
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
        con.disconnect();
    }

}