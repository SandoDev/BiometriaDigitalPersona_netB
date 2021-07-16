package models;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.lang.System.out;
import static java.lang.System.err;

import java.io.ByteArrayInputStream;

public class Student {

    private ByteArrayInputStream fingerPrintData;
    private Integer fingerPrintSize;

    Database con = new Database();

    public ByteArrayInputStream getFingerPrintData() {
        out.println(this.fingerPrintData);
        return this.fingerPrintData;
    }

    public Integer getFingerPrintSize() {
        return this.fingerPrintSize;
    }

    public void setFingerPrintData(ByteArrayInputStream fingerPrintData) {
        this.fingerPrintData = fingerPrintData;
    }

    public void setFingerPrintSize(Integer fingerPrintSize) {
        this.fingerPrintSize = fingerPrintSize;
    }

    public boolean saveFingerPrint(String doc) {
        try {
            var c = con.getConnection(); // Set up connection with DB

            String query = "update student set fingerprint=? where identification=" + doc;
            PreparedStatement preparedStatement = c.prepareStatement(query);
            preparedStatement.setBinaryStream(1, this.fingerPrintData, this.fingerPrintSize);
            preparedStatement.executeUpdate();
            out.println("Statement executed");

            preparedStatement.close();
            return true;
        } catch (SQLException ex) {
            err.println("An error ocurred saving data in bd: " + ex.getMessage());
            return false;
        } finally {
            con.desconectar();
        }
    }

}