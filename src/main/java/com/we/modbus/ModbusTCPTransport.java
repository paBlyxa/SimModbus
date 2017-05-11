package com.we.modbus;

import java.io.*;
import java.net.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.we.modbus.model.ModbusMessage;

/**
 *
 * @author fakadey
 */
public class ModbusTCPTransport implements ModbusTransport {

	private final static Logger logger = LoggerFactory.getLogger(ModbusTCPTransport.class);
	
	/**
	 * Номер TCP порта через который будет работать Modbus TCP
	 */
	public final static int MODBUS_TCP_PORT = 502;

	/**
	 * Длина заголовка TCP пакета
	 */
	public static final int HEADER_LENGTH = 6;

	/**
	 * Максимальный размер данных
	 */
	public static final int DATA_MAX = 255;

	/**
	 * Максимальный размер пакета данных
	 */
	public static final int MAX_TRANSACTION_LENGTH = HEADER_LENGTH + DATA_MAX;

	/**
	 * Идентификатор протокола Modbus TCP
	 */
	public static final short PROTOCOL_IDENTIFIER = (short) 0x0000;

	/**
	 * Socket для установления связи и передачи данных
	 */
	private Socket socket;

	/**
	 * Поток данных для приема
	 */
	private BufferedInputStream in;

	/**
	 * Поток данных для отправления
	 */
	private BufferedOutputStream out;

	// Массивы для хранения заголовков пакетов
	private byte[] receive_header = new byte[HEADER_LENGTH];
	private byte[] send_header = new byte[HEADER_LENGTH];

	// Счетчики принятых и отправленых данных
	private int count = 0;
	private int recv = 0;

	//private int reply_length;

	private int protocol_identifier;

	/**
	 * Конструктор класса. Использует аргумент Socket для инициализации.
	 * 
	 * @param socket
	 * @throws IOException 
	 */
	public ModbusTCPTransport(Socket socket) throws IOException {
		this.socket = socket;
		// Создаем потоки данных для приема и передачи
		init();
	}

	/**
	 * Конуструктор класса. Создает socket на назначенный host и порт
	 * 
	 * @param host
	 *            Хост
	 * @param port
	 *            Порт хоста
	 * @throws IOException 
	 */
	public ModbusTCPTransport(String host, int port) throws IOException {
		// Создаем Socket
		try {
			socket = new Socket(host, port);
			// Создаем потоки данных для приема и передачи
			init();
		} catch (IOException ex) {
			logger.error("Creation socket is failed", ex);
			throw ex;
		}
	}

	/**
	 * Конуструктор класса. Создает socket на назначенный ip адрес и порт
	 * 
	 * @param address
	 *            ip адрес хоста
	 * @param port
	 *            Порт хоста
	 * @throws IOException 
	 */
	public ModbusTCPTransport(InetAddress address, int port) throws IOException {
		// Создаем Socket
		try {
			socket = new Socket(address, port);
			// Создаем потоки данных для приема и передачи
			init();
		} catch (IOException ex) {
			logger.error("Creation socket is failed", ex);
			throw ex;
		}
	}

	/**
	 * Создаем потоки данных для приема и передачи
	 * @throws IOException 
	 */
	private void init() throws IOException {
		try {
			out = new BufferedOutputStream(socket.getOutputStream());
			in = new BufferedInputStream(socket.getInputStream());
		} catch (IOException ex) {
			logger.error("Creation socket is failed", ex);
			throw ex;
		}

		logger.debug("ModbusTCPTransport: Конструктор завершен");
	}

	/**
	 * Метод для отправления посылки.
	 * 
	 * @param msg
	 *            Сообщение для отправления
	 * @return Возвращает true если успешно
	 * @throws IOException 
	 */
	@Override
	public boolean sendFrame(ModbusMessage msg) throws IOException {

		logger.debug("ModbusTCPTransport: Отправление.....");

		// Создаем заголовок
		// Идентификатор транзакции
		send_header[0] = (byte) ((msg.transId >> 8) & 0xFF);
		send_header[1] = (byte) (msg.transId & 0xFF);

		// Идентификатор протокола
		send_header[2] = (byte) ((PROTOCOL_IDENTIFIER >> 8) & 0xFF);
		send_header[3] = (byte) (PROTOCOL_IDENTIFIER & 0xFF);

		// Размер данных после заголовка в байтах
		send_header[4] = (byte) 0x00;
		send_header[5] = (byte) (msg.length & 0xFF);

		try {
			logger.debug("ModbusTCPTransport: Заголовок {}", ByteUtils.toHex(send_header, HEADER_LENGTH));
			
			// Записываем в поток вывода заголовок и данные
			out.write(send_header, 0, HEADER_LENGTH);
			out.write(msg.buff, 0, msg.length);
			// Отправляем данные
			out.flush();

		} catch (IOException ex) {
			logger.error("An error occured when send frame", ex);
			throw ex;
		}

		logger.debug("ModbusTCPTransport: Пакет отправлен");
		return true;
	}

	/**
	 * Метод для приема пакетов даных. Возвращает количество полученных байт.
	 * Метод блокирует выполение, пока не получит данные или не разорвется
	 * соединение.
	 *
	 * @author Pablo
	 *
	 * @param msg
	 *            Полученные данные
	 * @return Возвращает количество полученных байт, или -1, если прием
	 *         неуспешен
	 */
	@Override
	public int receiveFrame(ModbusMessage msg) {

		logger.debug("ModbusTCPTransport: Прием пакета.....");

		// Флаг, что заголовок проверен
		boolean header_check = false;

		// Размер данных приема и передачи
		int request_body_length;
		
		// Чтение заголовка
		count = 0;
		while (count < HEADER_LENGTH) {
			try {
				recv = in.read(receive_header, count, HEADER_LENGTH - count);
			} catch (IOException ex) {
				logger.error("An error occurred when receiving frame", ex);
				return -1;
			}
			if (recv == -1) {
				// Print Message if in debug mode
				logger.debug("ModbusTCPTransport: Поток приема закрыт, прием вернул -1");
			}
			count += recv;
		}

		logger.debug("ModbusTCPTransport: Заголовок принят {}", ByteUtils.toHex(receive_header, HEADER_LENGTH));

		// Далее нужно проверить заголовок. Если нет то считываем данные.
		// Если нет закрываем socket.
		header_check = true;

		// В Modbus TCP идентификатор транзакции не используется,
		// и просто копируется из запроса в ответ, мы должны
		// его скопировать для дальнейшей проверки.
		msg.transId = (int) ((receive_header[0] << 8) + receive_header[1]);

		// Проверяем идентификатор протокола
		protocol_identifier = (short) ((receive_header[2] << 8) + receive_header[3]);
		if (protocol_identifier != PROTOCOL_IDENTIFIER) {
			logger.warn("ModbusTCPTransport: Некорректный протокол идентификатора: {}", protocol_identifier);
			header_check = false;
		}

		// Старший байт длины данных должен быть 0, т.к.
		// максимальный размер данных 256 байт
		if (receive_header[4] != (byte) 0x00) {
			logger.warn("ModbusTCPTransport: Некорректная длина данных, старший байт: {}", receive_header[4]);
			header_check = false;
		}

		// Проверяем младший байт длины данных, он должен быть не меньше 2
		if (receive_header[5] == (byte) 0x00 || receive_header[5] == (byte) 0x01) {
			logger.warn("ModbusTCPTransport: Некорректная длина данных, младший байт: {}", receive_header[5]);
			header_check = false;
		}

		// Если заголовок с ошибками, закрываем соединение
		if (!header_check) {
			logger.warn("ModbusTCPTransport: Заголовок с ошибками!");
			try {
				socket.close();
			} catch (Exception ex) {
				logger.warn("An error occurred when closing socket", ex);
			}
			return -1;
		}

		// Длина данных
		request_body_length = receive_header[5] & 0xFF;

		logger.debug("ModbusTCPTransport: Длина данных " + request_body_length);

		count = 0;
		while (count < request_body_length) {
			try {
				recv = in.read(msg.buff, count, request_body_length - count);
			} catch (IOException ex) {
				logger.error("An error occured when receive frame", ex);				
				return -1;
			}
			if (recv == -1) {
				logger.debug("ModbusTCPTransport: Поток закрыт, прием вернул -1");
				return -1;
			}
			count += recv;
		}

		// Сохраняем длину полученных данных
		msg.length = request_body_length;

		logger.debug("ModbusTCPTransport: Пакет принят [{}]", ByteUtils.toHex(msg.buff, request_body_length));
		logger.info("Receive: {} {}", ByteUtils.toHex(receive_header, HEADER_LENGTH), ByteUtils.toHex(msg.buff, request_body_length));
		return HEADER_LENGTH + request_body_length;
	}

	public void close() throws IOException {
		logger.debug("Try to close socket");
		socket.close();
	}
}
