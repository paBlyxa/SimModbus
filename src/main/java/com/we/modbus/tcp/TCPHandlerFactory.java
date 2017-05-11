package com.we.modbus.tcp;

import java.io.IOException;
import java.net.Socket;

public interface TCPHandlerFactory {

	public TCPHandler createTCPHandler(Socket socket) throws IOException;
}
