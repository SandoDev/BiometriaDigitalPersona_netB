/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.JOptionPane;
import utils.SecureEncryption;

/**
 *
 * @author D2D
 */
public class PropertiesFile {
    
    Properties properties = null;
    String propertyType = null;

    
    public PropertiesFile(String propertyType){
        super();
        this.propertyType = propertyType;
    }
    
    private Properties getProperties(){
        if(this.properties == null){
            this.properties = new Properties();
            try{
                ArrayList<String> decodeProperties = decodeProperties();
                for (String line: decodeProperties){
                    String[] parts = line.split("=");
                    this.properties.setProperty(parts[0].strip(), parts[1].strip());    
                }
            } catch(FileNotFoundException e){
                e.printStackTrace();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
        return properties;
    }
    
    public String getProperty(String nameProperty){
        return getProperties().getProperty(nameProperty);
    }

    private ArrayList<String> decodeProperties() throws IOException {
        String path = "/config/"+this.propertyType+".properties";
        ArrayList<String> lista = new ArrayList<>();
        try (InputStream is = getClass().getResourceAsStream(path)) {
            SecureEncryption enp = new SecureEncryption();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    String decode = enp.decodeStringDASBF(linea);
                    lista.add(decode);
                }
            } catch (IOException e) {
                System.err.format("IOException: %s%n", e);
                JOptionPane.showMessageDialog(null, "error leyendo archivo1: "+e);
            }
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
            JOptionPane.showMessageDialog(null, "error leyendo archivo2: "+e);
        }
        return lista;
    }
}
