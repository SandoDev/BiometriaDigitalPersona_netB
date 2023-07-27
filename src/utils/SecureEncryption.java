/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author casd7
 */
public class SecureEncryption {
    
    /*
    Mapa del alfabeto basado en desplazamiento
    A = F
    B = G
    ...
    */
    private Map<Character, Character> mapABC(int shif,Boolean lowercase){
        String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        if (lowercase){
            abc = abc.toLowerCase();
        }
        char[] abcChar;
        abcChar = abc.toCharArray();
        Map<Character, Character> map;
        map = new HashMap<>();
        int count = shif;
        for (Character letter : abcChar){
            map.put(letter, abcChar[count%26]);
            count = count + 1;
        }
        return map;
    }
    
    /*
    Mapa de los numeros basado en desplazamiento
    0 = 4
    1 = 5
    ...
    */
    private Map<Character, Character> mapInt(int shif){
        String nums = "0123456789";
        char[] numsChar;
        numsChar = nums.toCharArray();
        Map<Character, Character> map;
        map = new HashMap<>();
        int count = shif;
        for (Character num : numsChar){
            map.put(num, numsChar[count%10]);
            count = count + 1;
        }
        return map;
    }

    /*
    Funcion principal de codificacion, de 3 etapas
    "sando" = "W2gsIDYsIEssIHosIEUsIEwsIDIsID1d"
    */
    public String encodeStringDASBF(String original) {
        SecureEncryption enp = new SecureEncryption();
        Base64.Encoder encoder = Base64.getEncoder();

        //corrimiento de 5 y 4
        Map<Character, Character> mapABC = enp.mapABC(5, false);     
        Map<Character, Character> mapABCmin = enp.mapABC(12,true);     
        Map<Character, Character> mapNums = enp.mapInt(4);
        
        // stage 1
        byte[] encodedBytes = encoder.encode(original.getBytes());
        
        // stage 2
        char[] encodeChar = new String(encodedBytes).toCharArray();
        int index = 0;
        for (Character enChar:encodeChar){
            //String toUpperCase = enChar.toString().toUpperCase();
            //char charUpper = toUpperCase.charAt(0);
            int numericValue = (int) enChar;
            if ((numericValue >= 48 ) && (numericValue <= 57)){
                // Es numerico
                encodeChar[index] = mapNums.get(enChar);
            }else if ( (numericValue >= 65 ) && (numericValue <= 90)){
                // Es alfabetico Mayuscula
                encodeChar[index] = mapABC.get(enChar);
            }else if ( (numericValue >= 97 ) && (numericValue <= 122)){
                encodeChar[index] = mapABCmin.get(enChar);
            }
            index = index + 1;
        }
        
        // stage 3
        byte[] encodedBytes2 = encoder.encode(Arrays.toString(encodeChar).getBytes());
        
        return new String(encodedBytes2);
    }
    
    /*
    Funcion de decodificacion de 3 etapas
    "W2gsIDYsIEssIHosIEUsIEwsIDIsID1d" = "sando"
    */
    public String decodeStringDASBF(String encode){
        SecureEncryption enp = new SecureEncryption();
        Base64.Decoder decoder = Base64.getDecoder();

        //corrimiento opuesto de 21 y 6
        Map<Character, Character> mapABC = enp.mapABC(21,false);   
        Map<Character, Character> mapABCmin = enp.mapABC(14,true);   
        Map<Character, Character> mapNums = enp.mapInt(6);
        
        // Stage 1
        byte[] decodeBytes = decoder.decode(encode.getBytes());
        String decodeString = new String(decodeBytes);
        String decodeSubstring = decodeString.substring(1, decodeString.length()-1);
        String decodeReplace = decodeSubstring.replace(", ", "");
        
        // Stage 2
        char[] decodeChar = decodeReplace.toCharArray();
        int index = 0;
        for (Character deChar:decodeChar){
            //String toUpperCase = deChar.toString().toUpperCase();
            //char charUpper = toUpperCase.charAt(0);
            int numericValue = (int) deChar;
            if ((numericValue >= 48 ) && (numericValue <= 57)){
                // Es numerico
                decodeChar[index] = mapNums.get(deChar);
            }else if ( (numericValue >= 65 ) && (numericValue <= 90)){
                // Es alfabetico
                decodeChar[index] = mapABC.get(deChar);
            }else if ( (numericValue >= 97 ) && (numericValue <= 122)){
                decodeChar[index] = mapABCmin.get(deChar);
            }
            index = index + 1;
        }
        
        String charString = Arrays.toString(decodeChar);
        String charSubstring = charString.substring(1, charString.length()-1);
        String charReplace = charSubstring.replace(", ", "");
        
        // Stage 3
        byte[] decodeBytes2 = decoder.decode(charReplace.getBytes());
        
        return new String(decodeBytes2);
    }
}
