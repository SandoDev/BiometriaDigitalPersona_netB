/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProcesaHuella;

import java.sql.Connection;
import java.sql.DriverManager;
import config.PropertiesFile;

/**
 *
 * @author Pen-SandO
 */
public final class conectarMysqlMyadmin {

    //declaracion de variables para la conexion
    private static Connection conn;
    private String driver = "com.mysql.jdbc.Driver";
    private String host = "localhost";
    private String port = "3306";
    private String user = "root";
    private String password = "";//para mi servidor localhost no configure pass
    private String bdd = "bd";
    private String url = "jdbc:mysql://" + host + ":" + port + "/" + bdd;
    private final PropertiesFile properties;

    //metodo que retorna la conexion

    /**
     *
     * @return
     */

    public String getUrl() {
        return url;
    }

    public void configUrl() {
        this.url = "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.bdd;
    }
    
    public conectarMysqlMyadmin(){
        this.properties = new PropertiesFile("database");
        this.host = this.properties.getProperty("server.name");
        this.port = this.properties.getProperty("port");
        this.user = this.properties.getProperty("database.user");
        this.password = this.properties.getProperty("database.pass");
        this.bdd = this.properties.getProperty("database.name");
        this.configUrl();
    }

    public Connection getConnection() {
        conn = null;
        try {
            //se carga el driver necesario para el proceso
            Class.forName(driver);
            //se conecnta finalmete a la base de datos
            conn = DriverManager.getConnection(url, user, password);
            if (conn != null) {
                System.out.println("Conectado a la base de datos: " + bdd);
            }
        } catch (Exception e) {
            System.out.println("Error al conectar: " + e);
        }
        return conn;
    }

    //metodo que desconecta de la base de datos

    /**
     *
     */
    public void desconectar() {
        conn = null;//se cierra la conexion asignado la variable a null
        System.out.println("Desconectado de la base de datos: " + bdd);
    }
}
