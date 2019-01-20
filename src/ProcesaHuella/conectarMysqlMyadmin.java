/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProcesaHuella;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author Pen-SandO
 */
public class conectarMysqlMyadmin {

    //declaracion de variables para la conexion
    private static Connection conn;
    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String user = "root";
    private static final String password = "";//para mi servidor localhost no configure pass
    private static final String bdd = "huellas";
    private static final String url = "jdbc:mysql://localhost:3306/" + bdd;

    //metodo que retorna la conexion
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
    public void desconectar() {
        conn = null;//se cierra la conexion asignado la variable a null
        if (conn == null) {
            System.out.println("Desconectado de la base de datos: " + bdd);
        }
    }

}
