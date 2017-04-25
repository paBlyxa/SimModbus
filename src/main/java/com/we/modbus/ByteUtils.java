package com.we.modbus;

/**
 *
 * @author fakadey
 */
public class ByteUtils {
    
    /**
     * Преобразует один байт в эквивалентную HEX-строку
     * 
     * @Author Pablo
     * 
     * @param b байт для преобразования
     * @return Возращает HEX-строку входного байта
     */
    public static String toHex(byte b){
        
        String temp = Integer.toHexString(b).toUpperCase();
        
        if (temp.length() == 1){
            temp = "0" + temp;
        }
        else if (temp.length() == 2){
            
        }
        else{
            temp = temp.substring(temp.length()-2, temp.length());
        }
        
        return temp;
    }
    
    /**
     * Преобразует один регистр (два байта) в эквивалентную
     * HEX-строку
     * 
     * @author Pablo
     * 
     * @param s Регистр для преобразования
     * @return Возвращает HEX-строку регистра
     */
    public static String toHex(short s){
        String temp = Integer.toHexString(s).toUpperCase();
        
        if (temp.length() == 1){
            temp = "000" + temp;
        }
        else if (temp.length() == 2){
            temp = "00" + temp;
        }
        else if (temp.length() == 3){
            temp = "0" + temp;
        }
        else if (temp.length() == 4){
            
        }
        else{
            temp = temp.substring(temp.length() - 4, temp.length());
        }
        return temp;
    }
    
    
    /**
     * Преобразует массив байт в эквивалентную HEX-строку
     * 
     * @Author Pablo
     * 
     * @param b массив байт
     * @param length Длина массива для преборазования
     * @return Возвращает HEX-строку массива байт
     */
    public static String toHex(byte[] b, int length){
        short len = (short)length;
        short count = 0;
        StringBuilder str = new StringBuilder("");
        
        while (len > 0){
            if (len <= 8){
                str.append(toHex(count));
                str.append(": ");
                while (len > 0){
                    str.append(toHex(b[count]));
                    str.append(' ');
                    count++;
                    len--;
                }
            }
            else{
                str.append(toHex(count));
                str.append(": ");
                for (int i = 0; i < 8; i++){
                    str.append(toHex(b[count]));
                    str.append(' ');
                    count++;
                    len--;
                }
                str.append('\n');
            }
        }
        return str.toString();
    }
}
