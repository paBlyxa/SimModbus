package com.we.modbus;

import java.io.IOException;
import java.net.InetAddress;
/**
 *
 * @author fakadey
 */
public class ModbusTCPMaster extends ModbusMaster{
    
    /**
     * Конструктор, который использует TCP транспорт. Сокет будет открыт
     * на указанный host и порт.
     * 
     * @Author Pablo
     * 
     * @param host Строковое имя хоста для подключения
     * @param port Номер порта хоста для подключения
     * @throws IOException 
     */
    public ModbusTCPMaster(String host, int port) throws IOException{
        super(new ModbusTCPTransport(host, port));
    }
    
    /**
     * Конструктор, который использует TCP транспорт. Сокет будет открыт
     * на указанный IP адрес и порт.
     * 
     * @Author Pablo
     * 
     * @param address IP аддрес для подключения
     * @param port Номер порта хоста для подключения
     * @throws IOException 
     */
    public ModbusTCPMaster(InetAddress address, int port) throws IOException{
        super(new ModbusTCPTransport(address, port));
    }
}
