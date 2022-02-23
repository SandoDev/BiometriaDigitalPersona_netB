package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import model.Database;

public class AssistanceDAO {
    
    Database con = new Database();
        
    public void registerAssistance(Integer inscription) throws SQLException{
        String query = "insert into Assistance "
                + "(`entryTime`, `inscription_idInscription`) "
                + "values(?, ?)";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Connection c = con.getConnection();
        PreparedStatement stm = c.prepareStatement(query);
        stm.setString(1, dtf.format(LocalDateTime.now()));
        stm.setInt(2, inscription);
        stm.execute();
        stm.close();
        c.close();
        con.disconnect();
    }

}