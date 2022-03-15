package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import model.Database;
import config.PropertiesFile;

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

    /**
     * Obtiene la fecha local con el formato en string adecuado y basado en el
     * tiempo zonal
     *
     * @return localDateTime -> "2022-03-03 10:00:00"
     */
    private String getLocalDate() {
        PropertiesFile properties = new PropertiesFile("database");
        this.timeZone = properties.getProperty("timeZone");
        TimeZone zone = TimeZone.getTimeZone(this.timeZone);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dtf.format(LocalDateTime.now(zone.toZoneId()));
    }
}
