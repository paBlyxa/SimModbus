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
	 * 
	 * @return возвращает количество принятых байт или -1 если неуспешно.
	 * @throws IOException
	 */
	public int handle() throws IOException;
	
	/**
	 * Метод для закрытия соединения.
	 * @throws IOException
	 */
	public void close() throws IOException;
}
