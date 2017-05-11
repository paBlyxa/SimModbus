package com.we.modbus.tcp;

import java.io.IOException;

/**
 * 
 * @author fakadey
 *
 */
public interface TCPHandler {
	
	/**
	 * Метод для обработки запросов.
	 * @throws IOException
	 */
	public void handle() throws IOException;
	
	/**
	 * Метод для закрытия соединения.
	 * @throws IOExceptio
	 */
	public void close() throws IOException;
}
