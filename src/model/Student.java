package model;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.lang.System.out;
import static java.lang.System.err;

import java.io.ByteArrayInputStream;
import java.sql.Connection;

public class Student {

    private ByteArrayInputStream fingerPrintData;
    private Integer fingerPrintSize;
    
    int id;
    String name;
    String lastName;
    String email;
    String identification;
    String codeInstitutional;
    String phone;

    Database con = new Database();
    
    public Student(){}
    
    public Student(
        int id,
        String name,
        String lastName,
        String email,
        String identification,
        String codeInstitutional,
        String phone
    ){
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.identification = identification;
        this.codeInstitutional = codeInstitutional;
        this.phone = phone;
    }

    public ByteArrayInputStream getFingerprintData() {
        out.println(this.fingerPrintData);
        return this.fingerPrintData;
    }

    public Integer getFingerprintSize() {
        return this.fingerPrintSize;
    }

    public void setFingerprintData(ByteArrayInputStream fingerPrintData) {
        this.fingerPrintData = fingerPrintData;
    }

    public void setFingerprintSize(Integer fingerPrintSize) {
        this.fingerPrintSize = fingerPrintSize;
    }

    public boolean saveFingerprint(String doc) {
        try {
            Connection c = con.getConnection(); // Set up connection with DB

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
            con.disconnect();
        }
    }
    
    @Override
    public String toString(){
        return this.name + " " + this.lastName;
    }

}
