package com.we.modbus.tcp;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author fakadey
 *
 */
public class TCPServer {

	private final static Logger logger = LoggerFactory.getLogger(TCPServer.class);

	private final int port;
	/**
	 * Фабрика для создания экземпляров, которые будут обрабатывать запросы по
	 * протоколу TCP.
	 */
	private final TCPHandlerFactory tcpHandlerFactory;
	private final ExecutorService executor;
	private final StatusListener statusListener;
	private final int maxConnections;
	private Integer numConnections;
	private ServerSocket serverSocket;
	private final List<ServerThread> serverThreadList;
	private volatile boolean cancelled = false;
	
	/**
	 * Конструктор для создания TCP сервера на прослушивание определенного
	 * порта.
	 * 
	 * @param port
	 * @param maxConnections максимальное количество подключенных клиентов
	 * @param tcpHandlerFactory фабрика для создания объектов, обрабатывающих
	 * новые запросы по протоколу TCP
	 */
	public TCPServer(int port, int maxConnections, TCPHandlerFactory tcpHandlerFactory, StatusListener statusListener) {
		this.port = port;
		this.tcpHandlerFactory = tcpHandlerFactory;
		this.statusListener = statusListener;
		this.maxConnections = maxConnections;
		this.executor = Executors.newFixedThreadPool(maxConnections);
		serverThreadList = new ArrayList<ServerThread>();
		numConnections = 0;
		logger.debug("New TCP server has created at port: {}", port);
	}

	/**
	 * Старт сервера - прослушивания новых подключений. Для каждого подключения
	 * создается новый экземпляр MobusSlave устройства.
	 */
	public void startListenConnection() {
		try {
			logger.debug("Start listen new connection");
			updateStatus("Start listen new connections");
			serverSocket = new ServerSocket(port);
			while (!Thread.currentThread().isInterrupted()) {
				Socket socket = serverSocket.accept();
				logger.debug("New connection has accepted");
				synchronized (numConnections) {
					if (numConnections < maxConnections) {
						ServerThread serverThread = new ServerThread(tcpHandlerFactory.createTCPHandler(socket));
						executor.execute(serverThread);
						numConnections++;
						logger.debug("New server thread has started, numConnections = {}", numConnections);
						updateStatus("A new connection has accepted");
					} else {
						logger.warn("Maximum connections reached");
					}
				}
			}
		} catch (IOException e) {
			if (!cancelled){
				logger.warn("An exception occured while accepting connection", e);
			}
		}
		close();
	}

	/**
	 * Остановка сервера и закрытие соединений.
	 */
	public void stop() {
		cancelled = true;
		updateStatus("Server stopped");
		for (ServerThread serverThread : serverThreadList){
			serverThread.close();
		}
		executor.shutdownNow();
		close();
		numConnections = 0;
		logger.debug("TCP server has stoped");
	}
	
	/**
	 * Close server socket.
	 */
	private void close(){
		if (serverSocket != null){
			try {
				serverSocket.close();
			} catch (IOException e) {
				logger.warn("An exception occured while closing server socket", e);
			} finally {
				serverSocket = null;
			}
		}
	}

	/**
	 * Update status of server
	 * @param status 
	 */
	private void updateStatus(String status){
		statusListener.updateStatus(status);
	}
	
	class ServerThread extends Thread {

		private final TCPHandler handler;

		ServerThread(TCPHandler handler) throws IOException {
			this.handler = handler;
		}

		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					int recvCount = handler.handle();
					if (recvCount < 0){
						break;
					}
				} catch (SocketException e){
					logger.debug("An socket exception occured when handle request", e);
					logger.info("Stop this handler, because of socket exception");
					Thread.currentThread().interrupt();
				} catch (IOException e) {
					logger.debug("An error occurred when handle request", e);
				}
			}
			try {
				handler.close();
			} catch (IOException e) {
				logger.debug("An error occured while closing socket", e);
			} finally {
				synchronized(numConnections){
					numConnections--;
					logger.debug("Connection closed, numConnections = {}", numConnections);
				}
			}
		}
		
		/**
		 * Close connection.
		 */
		public void close() {
			try {
				handler.close();
			} catch (IOException e) {
				logger.debug("An error occured while closing socket", e);
			}
		}
	}
}
