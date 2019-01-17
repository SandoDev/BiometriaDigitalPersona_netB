/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BDD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Pen-SandO
 */
public class conexionBDD {

    public String puerto = "3306";//num del puerto
    public String nomservidor = "localhost";//nom del servidor
    public String db = "huellas";//nombre de la base de datos
    public String user = "root";//usuario
    public String pass = "mysqlsando";//password
    public String extraSSL = "?autoReconnect=true&useSSL=false";//extra que se agrega para que no de error

    Connection conn = null;//concxion inicializada

    public Connection conectar() {
        try {
            String ruta = "jdbc:mysql://";
            String servidor = nomservidor + ":" + puerto + "/";
            Class.forName("com.mysql.jdbc.Driver");
            
            //genero la conexion
            conn = DriverManager.getConnection(ruta + servidor + db+extraSSL, user, pass);
            
            if (conn != null) {
                System.out.println("Conección a base de datos listo...");
            } else if (conn == null) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Se produjo el siguiente error1:" + e.getMessage());
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null,
                    "Se produjo el siguiente error2:" + e.getMessage());
        } finally {
            return conn;
        }
    }//para realizar la conexion

    public void desconectar() {
        conn = null;
        System.out.println("Desconexion a base de datos listo...");
    }//para realizar la desconexion
}
