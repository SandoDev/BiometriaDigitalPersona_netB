package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import model.Database;
import config.PropertiesFile;
import java.sql.ResultSet;
import java.time.Duration;
import java.time.ZonedDateTime;

public class AssistanceDAO {

    Database con = new Database();
    private String timeZone = "America/Bogota";

    public void registerAssistance(Integer inscription) throws SQLException {
        String query = "insert into Assistance "
                + "(`entryTime`, `inscription_idInscription`) "
                + "values(?, ?)";
        Connection c = con.getConnection();
        PreparedStatement stm = c.prepareStatement(query);
        stm.setString(1, this.getLocalDate());
        stm.setInt(2, inscription);
        stm.execute();
        stm.close();
        c.close();
        con.disconnect();
    }
    
    public Boolean currentAssistance(Integer inscription)throws SQLException{
        String query = "SELECT * FROM Assistance where inscription_idInscription="+inscription;
        Connection c = con.getConnection();
        PreparedStatement stm = c.prepareStatement(query);
        ResultSet rs = stm.executeQuery();
        Boolean result = false;
        if (rs.last()){
            String lastAssistanceDB = rs.getString("entryTime");
            
            TimeZone zone = this.getTimeZoneLocal();
            LocalDateTime currentDateTime = LocalDateTime.now(zone.toZoneId());
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S z");
            ZonedDateTime lastAssistance = ZonedDateTime.parse(lastAssistanceDB+" America/Bogota", formatter);
            
            Duration d = Duration.between(currentDateTime,lastAssistance);
            long timeDifference = Math.abs(d.toMinutes());
            result = timeDifference >= 30;
        }
        stm.close();
        c.close();
        con.disconnect();
        
        return result;
    }
    
    private TimeZone getTimeZoneLocal(){
        PropertiesFile properties = new PropertiesFile("database");
        this.timeZone = properties.getProperty("timeZone");
        TimeZone zone = TimeZone.getTimeZone(this.timeZone);
        
        return zone;
    }

    /**
     * Obtiene la fecha local con el formato en string adecuado y basado en el
     * tiempo zonal
     *
     * @return localDateTime -> "2022-03-03 10:00:00"
     */
    private String getLocalDate() {
        TimeZone zone = this.getTimeZoneLocal();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dtf.format(LocalDateTime.now(zone.toZoneId()));
    }
}
