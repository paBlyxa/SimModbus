package com.we.modbus;

/**
 *
 * @author fakadey
 */
public class ModbusMessage {
    
    /**
     * Массив байт содержащий сообщение.
     */
    public byte[] buff;
    
    /**
     * Количество байт в сообщение
     */
    public int length;
    
    /**
     * Идентификатор транзакции. Используется для Modbus TCP
     */
    public int transId;
    
    /**
     * Конструктор класса, инициализрует массив байт содержащий сообщение.
     *
     */
    public ModbusMessage(){
        buff = new byte[Modbus.MAX_MESSAGE_LENGTH];
        length = 0;
        transId = 0;
    }
    
    @Override
    public String toString(){
        String text = "Иден-р транзакции: " + transId + " Длина сообщ.: "
                + length + " " + ByteUtils.toHex(buff,length);
        return text;
    }
}
