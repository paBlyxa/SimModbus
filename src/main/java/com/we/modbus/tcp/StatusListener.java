package com.we.modbus.tcp;

public interface StatusListener {

	/**
	 * Обновление статуса сервера.
	 * @param status текст статуса
	 */
	public void updateStatus(String status);
}
