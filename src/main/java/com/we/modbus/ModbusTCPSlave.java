package com.we.modbus;

import java.io.IOException;
import java.net.Socket;

import com.we.modbus.model.ModbusDataModel;
import com.we.modbus.tcp.TCPHandler;

/**
 * 
 * @author fakadey
 *
 */
public class ModbusTCPSlave extends ModbusSlave implements TCPHandler {

	/**
	 * Конструктор для создания ModbusTCPSlave устройства.
	 * @param socket созданное соединение между клиентом и сервером
	 * @param dataModel модель данных
	 * @throws IOException
	 */
	public ModbusTCPSlave(Socket socket, ModbusDataModel dataModel) throws IOException {
		super(new ModbusTCPTransport(socket), dataModel);
	}

	/**
	 * Конструктор для создания ModbusTCPSlave устройства.
	 * @param socket созданное соединение между клиентом и сервером
	 * @param dataModel модель данных
	 * @oaran modbusSlaveAddress Адрес слэйв (Modbus адрес)
	 * @throws IOException
	 */
	public ModbusTCPSlave(Socket socket, ModbusDataModel dataModel, byte modbusSlaveAddress) throws IOException {
		super(new ModbusTCPTransport(socket), dataModel, modbusSlaveAddress);
	}

	/**
	 * Метод для обработки запросов.
	 * 
	 * @return возвращает количество принятых байт или -1 если неуспешно.
	 * @throws IOException
	 */
	@Override
	public int handle() throws IOException{
		return handleRequest();
	}
}
