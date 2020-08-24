/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author D2D
 */
public class PropertiesFile {
    
    public static Properties properties = null;
    public static String propertyType = null;

    
    public PropertiesFile(String propertyType){
        super();
        this.propertyType = propertyType;
    }
    
    private static Properties getProperties(){
        if(properties == null){
            properties = new Properties();
            try{
                String path = "src/config/"+propertyType+".properties";              
                InputStream in = new FileInputStream(path);
                properties.load(in);
            } catch(FileNotFoundException e){
                e.printStackTrace();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
        return properties;
    }
    
    public static String getProperty(String nameProperty){
        return getProperties().getProperty(nameProperty);
    }

}
