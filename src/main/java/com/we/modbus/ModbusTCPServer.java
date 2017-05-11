package com.we.modbus;

import java.io.IOException;
import java.net.Socket;

import com.we.modbus.model.ModbusDataModel;
import com.we.modbus.tcp.StatusListener;
import com.we.modbus.tcp.TCPHandler;
import com.we.modbus.tcp.TCPHandlerFactory;
import com.we.modbus.tcp.TCPServer;

/**
 * 
 * @author fakadey
 *
 */
public class ModbusTCPServer {

	private final TCPServer tcpServer;
	private final ModbusDataModel modbusDataModel;
	
	/**
	 * Конструктор для создания Modbus TCP Server-а
	 * @param port номер порта
	 * @param maxConnections максимальное количество клиентов
	 * @param modbusDataModel модель данных
	 */
	public ModbusTCPServer(int port, int maxConnections, ModbusDataModel modbusDataModel, StatusListener statusListener){
		tcpServer = new TCPServer(port, maxConnections, new ModbusTCPSlaveFactory(), statusListener);
		this.modbusDataModel = modbusDataModel;
	}
	
	/**
	 * Метод для старта сервера.
	 */
	public void start(){
		tcpServer.startListenConnection();
	}
	
	/**
	 * Метод для остановки сервера.
	 */
	public void stop(){
		tcpServer.stop();
	}
	
	/**
	 * Фабрика для создания экземпляров ModbusTCPSlave.
	 * @author fakadey
	 *
	 */
	class ModbusTCPSlaveFactory implements TCPHandlerFactory{

		@Override
		public TCPHandler createTCPHandler(Socket socket) throws IOException {
			// TODO Auto-generated method stub
			return new ModbusTCPSlave(socket, modbusDataModel);
		}
		
	}
}
