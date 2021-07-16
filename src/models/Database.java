/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import static java.lang.System.out;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.swing.JOptionPane;

import config.PropertiesFile;

/**
 *
 * @author Pen-SandO
 */
public final class Database {

    // declaracion de variables para la conexion
    private Connection conn;
    private String host = "localhost";
    private String port = "3306";
    private String user = "root";
    private String password = "";
    private String bdd = "bd";
    private String url = "jdbc:mysql://" + host + ":" + port + "/" + bdd;

    public Database() {
        PropertiesFile properties = new PropertiesFile("database");
        this.host = properties.getProperty("server.name");
        this.port = properties.getProperty("port");
        this.user = properties.getProperty("database.user");
        this.password = properties.getProperty("database.pass");
        this.bdd = properties.getProperty("database.name");
        this.configUrl();
    }

    /**
     *
     * @return String
     */
    public String getUrl() {
        return url;
    }

    public void configUrl() {
        this.url = "jdbc:mysql://" +
                    this.host + ":" + this.port + "/" +
                    this.bdd
                    ;
    }

    public Connection getConnection() {
        try {
            // Conection to database
            this.conn = DriverManager.getConnection(url, this.user, this.password);
            out.println("Connected to database: " + this.bdd);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occured in connection: "+e);
        }
        return this.conn;
    }

    /**
     * Disconnect database
     */
    public void desconectar() {
        this.conn = null;
        out.println("Disconnected from database: " + this.bdd);
    }
}
