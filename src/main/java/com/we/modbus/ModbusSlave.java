package com.we.modbus;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.we.modbus.model.ErrorCode;
import com.we.modbus.model.Function;
import com.we.modbus.model.ModbusDataModel;
import com.we.modbus.model.ModbusMessage;

public class ModbusSlave extends Modbus {

	private final static Logger logger = LoggerFactory.getLogger(ModbusMaster.class);

	private byte modbusAddressSlave;
	private final int registersCount;
	private final ModbusDataModel dataModel;

	/**
	 * Конструктор класса. Принимает объект ModbusTransport
	 * 
	 * @Author Pablo
	 * 
	 * @param transport
	 *            Объект ModbusTransport, который должен использоваться этим
	 *            Modbus объектом для приема и посылки сообщений
	 * @param registersCount
	 *            Максимальный адрес регистра
	 */
	public ModbusSlave(ModbusTransport transport, ModbusDataModel dataModel) {
		super(transport);
		this.dataModel = dataModel;
		this.registersCount = dataModel.getRegisterCount();
		modbusAddressSlave = 0;
	}

	/**
	 * Конструктор класса. Принимает объект ModbusTransport
	 * 
	 * @Author Pablo
	 * 
	 * @param transport
	 *            Объект ModbusTransport, который должен использоваться этим
	 *            Modbus объектом для приема и посылки сообщений
	 * @param maxAddress
	 *            Максимальный адрес регистра
	 */
	public ModbusSlave(ModbusTransport transport, ModbusDataModel dataModel, byte modbusAddressSlave) {
		this(transport, dataModel);
		this.modbusAddressSlave = modbusAddressSlave;
	}

	/**
	 * Метод для обработки нового запроса.
	 * 
	 * @return возвращает количество принятых байт или -1 если неуспешно.
	 * @throws IOException
	 */
	public int handleRequest() throws IOException {
		ModbusMessage request = new ModbusMessage();

		// Получаем новый запрос
		transport.receiveFrame(request);

		// Проверка адреса
		if (modbusAddressSlave != 0 && (request.buff[0] != modbusAddressSlave)) {
			logger.warn("ModbusSlave: Некорректный адрес ведомого [{}]", request.buff[0]);
			return -1;
		}

		// Проверям код функции
		Function function = null;
		for (Function fun : Function.values()) {
			if (fun.getCode() == request.buff[1]) {
				function = fun;
				break;
			}
		}
		if (function == null) {
			logger.warn("ModbusSlave: Некорректный код функции [{}]", request.buff[1]);
			sendFaultAnswer(request, ErrorCode.ILLEGAL_FUNCTION);
			return -1;
		}

		// Адрес регистра
		int reference = ((request.buff[2] & 0xFF) << 8) + (request.buff[3] & 0xFF);

		switch (function) {
		case READ_COIL_STATUS:
		case READ_DISCRETE_INPUTS: {
			// Проверка длины запроса
			if (request.length != 6) {
				logger.warn("ModbusSlave: Некооректная длина запроса [{}]", request.length);
				return -1;
			}

			int length = ((request.buff[4] & 0xFF) << 8) + (request.buff[5] & 0xFF);

			// Проверка адреса регистров и количества
			ErrorCode errorCode = checkRefAndLen(reference / 16, (length + 15) / 16, 5);
			if (errorCode != null) {
				sendFaultAnswer(request, errorCode);
				return -1;
			}

			// Чтение данных
			byte[] values = dataModel.read(reference, length, function.getDataModelTable());
			if (values.length == 0) {
				logger.warn("ModbusSlave: При чтении данных из памяти возникла ошибка");
				sendFaultAnswer(request, ErrorCode.SERVER_DEVICE_FAILURE);
				return -1;
			}

			// Формируем ответ
			ModbusMessage response = makeHead(function, 3 + values.length, request.transId);
			// Count bytes
			response.buff[2] = (byte) values.length;
			for (int i = 0; i < values.length; i++) {
				response.buff[3 + i] = values[i];
			}
			// Отправляем ответ
			sendFrame(response);
			break;
		}
		case READ_MULTIPLE_REGISTERS:
		case READ_INPUT_REGISTERS: {
			// Проверка длины запроса
			if (request.length != 6) {
				logger.warn("ModbusSlave: Некооректная длина запроса [{}]", request.length);
				return -1;
			}

			int length = ((request.buff[4] & 0xFF) << 8) + (request.buff[5] & 0xFF);
			// Проверка адреса регистров и количества
			ErrorCode errorCode = checkRefAndLen(reference, length, 5);
			if (errorCode != null) {
				sendFaultAnswer(request, errorCode);
				return -1;
			}

			// Чтение данных
			byte[] values = dataModel.read(reference, length, function.getDataModelTable());
			if (values.length == 0) {
				logger.warn("ModbusSlave: При чтении данных из памяти возникла ошибка");
				sendFaultAnswer(request, ErrorCode.SERVER_DEVICE_FAILURE);
				return -1;
			}

			// Формируем ответ
			ModbusMessage response = makeHead(function, 3 + values.length, request.transId);
			// Count bytes
			response.buff[2] = (byte) values.length;
			for (int i = 0; i < values.length; i++) {
				response.buff[3 + i] = values[i];
			}
			// Отправляем ответ
			sendFrame(response);
			break;
		}
		case WRITE_SINGLE_COIL: {
			// Проверка длины запроса
			if (request.length != 6) {
				logger.warn("ModbusSlave: Некооректная длина запроса [{}]", request.length);
				return -1;
			}

			// Проверка адреса регистров и количества
			ErrorCode errorCode = checkRefAndLen(reference / 16, 1, 5);
			if (errorCode != null) {
				sendFaultAnswer(request, errorCode);
				return -1;
			}

			// Проверка значения для записи
			int value = ((request.buff[4] & 0xFF) << 8) + (request.buff[5] & 0xFF);
			if (value != 0x00 && value != 0xFF00) {
				logger.warn("ModbusSlave: Некорректное значение для записи в флаг [{}]", value);
				sendFaultAnswer(request, ErrorCode.ILLEGAL_DATA_VALUE);
				return -1;
			}

			// Запись данных
			byte[] values = new byte[1];
			values[0] = (byte) (value == 0xFF00 ? 1 : 0);
			boolean result = dataModel.write(reference, 1, function.getDataModelTable(), values);
			if (!result) {
				logger.warn("ModbusSlave: При записи данных в память возникла ошибка");
				sendFaultAnswer(request, ErrorCode.SERVER_DEVICE_FAILURE);
				return -1;
			}

			// Отправляем ответ, повторяющий запрос
			sendFrame(request);
			break;
		}
		case WRITE_SINGLE_REGISTER: {
			// Проверка длины запроса
			if (request.length != 6) {
				logger.warn("ModbusSlave: Некооректная длина запроса [{}]", request.length);
				return -1;
			}

			// Проверка адреса регистров и количества
			ErrorCode errorCode = checkRefAndLen(reference, 1, 5);
			if (errorCode != null) {
				sendFaultAnswer(request, errorCode);
				return -1;
			}

			// Запись данных
			byte[] values = new byte[2];
			values[0] = request.buff[4];
			values[1] = request.buff[5];
			boolean result = dataModel.write(reference, 1, function.getDataModelTable(), values);
			if (!result) {
				logger.warn("ModbusSlave: При записи данных в память возникла ошибка");
				sendFaultAnswer(request, ErrorCode.SERVER_DEVICE_FAILURE);
				return -1;
			}

			// Отправляем ответ, повторяющий запрос
			sendFrame(request);
			break;
		}
		case WRITE_MULTIPLE_COILS: {
			// Считываем количество данных для записи
			int length = ((request.buff[4] & 0xFF) << 8) + (request.buff[5] & 0xFF);

			// Проверка адреса регистров и количества
			ErrorCode errorCode = checkRefAndLen(reference / 16, (length + 15) / 16, 9);
			if (errorCode != null) {
				sendFaultAnswer(request, errorCode);
				return -1;
			}

			// Проверка длины запроса
			if (request.length != (length + 7) / 8 + 7) {
				logger.warn("ModbusSlave: Некооректная длина запроса [{}]", request.length);
				return -1;
			}

			// Проверка количества байт данных
			int countBytes = request.buff[6] & 0xFF;
			if (countBytes != ((length + 7) / 8)) {
				logger.warn("ModbusSlave: Некорректное количество байт данных [{}]", countBytes);
				sendFaultAnswer(request, ErrorCode.ILLEGAL_DATA_VALUE);
				return -1;
			}

			// Считываем данные для записи
			byte[] values = new byte[countBytes];
			System.arraycopy(request.buff, 7, values, 0, countBytes);
			boolean result = dataModel.write(reference, length, function.getDataModelTable(), values);
			if (!result) {
				logger.warn("ModbusSlave: При записи данных в память возникла ошибка");
				sendFaultAnswer(request, ErrorCode.SERVER_DEVICE_FAILURE);
				return -1;
			}

			// Формируем ответ
			ModbusMessage response = makeHead(function, 6, request.transId);
			System.arraycopy(request.buff, 2, response.buff, 2, 4);

			// Отправляем ответ
			sendFrame(response);
			break;
		}
		case WRITE_MULTIPLE_REGISTERS: {
			// Считываем количество данных для записи
			int length = ((request.buff[4] & 0xFF) << 8) + (request.buff[5] & 0xFF);

			// Проверка адреса регистров и количества
			ErrorCode errorCode = checkRefAndLen(reference, length + 15, 9);
			if (errorCode != null) {
				sendFaultAnswer(request, errorCode);
				return -1;
			}

			// Проверка длины запроса
			if (request.length != length * 2 + 7) {
				logger.warn("ModbusSlave: Некооректная длина запроса [{}]", request.length);
				return -1;
			}

			// Проверка количества байт данных
			int countBytes = request.buff[6] & 0xFF;
			if (countBytes != length * 2) {
				logger.warn("ModbusSlave: Некорректное количество байт данных [{}]", countBytes);
				sendFaultAnswer(request, ErrorCode.ILLEGAL_DATA_VALUE);
				return -1;
			}

			// Считываем данные для записи
			byte[] values = new byte[countBytes];
			System.arraycopy(request.buff, 7, values, 0, countBytes);
			boolean result = dataModel.write(reference, length, function.getDataModelTable(), values);
			if (!result) {
				logger.warn("ModbusSlave: При записи данных в память возникла ошибка");
				sendFaultAnswer(request, ErrorCode.SERVER_DEVICE_FAILURE);
				return -1;
			}

			// Формируем ответ
			ModbusMessage response = makeHead(function, 6, request.transId);
			System.arraycopy(request.buff, 2, response.buff, 2, 4);

			// Отправляем ответ
			sendFrame(response);
			break;
		}
		case MASK_WRITE_REGISTER: {
			// Проверка длины запроса
			if (request.length != 8) {
				logger.warn("ModbusSlave: Некооректная длина запроса [{}]", request.length);
				return -1;
			}

			// Проверка адреса регистров и количества
			ErrorCode errorCode = checkRefAndLen(reference, 1, 5);
			if (errorCode != null) {
				sendFaultAnswer(request, errorCode);
				return -1;
			}

			// Чтение данных
			byte[] andMask = new byte[2];
			andMask[0] = request.buff[4];
			andMask[1] = request.buff[5];
			byte[] orMask = new byte[2];
			orMask[0] = request.buff[6];
			orMask[1] = request.buff[7];
			byte[] dataInMemory = dataModel.read(reference, 1, function.getDataModelTable());
			if (dataInMemory.length == 0) {
				logger.warn("ModbusSlave: При чтении данных из памяти возникла ошибка");
				sendFaultAnswer(request, ErrorCode.SERVER_DEVICE_FAILURE);
				return -1;
			}
			// Изменение данных
			dataInMemory[0] = (byte) ((dataInMemory[0] & andMask[0]) | (orMask[0] & (~andMask[0])));
			dataInMemory[1] = (byte) ((dataInMemory[1] & andMask[1]) | (orMask[1] & (~andMask[1])));

			boolean result = dataModel.write(reference, 1, function.getDataModelTable(), dataInMemory);
			if (!result) {
				logger.warn("ModbusSlave: При записи данных в память возникла ошибка");
				sendFaultAnswer(request, ErrorCode.SERVER_DEVICE_FAILURE);
				return -1;
			}

			// Отправляем ответ, повторяющий запрос
			sendFrame(request);
			break;
		}
		default:
			logger.warn("ModbusSlave: Неизвестная или нереализованная функция [{}]", function);
			return -1;
		}

		return request.length;
	}

	/**
	 * Отправляем ответ с ошибкой
	 * 
	 * @param request
	 *            принятый запрос
	 * @param errorCode
	 *            код ошибки
	 * @throws IOException
	 */
	private void sendFaultAnswer(ModbusMessage request, ErrorCode errorCode) throws IOException {
		ModbusMessage response = new ModbusMessage();
		response.transId = request.transId;
		response.length = 3;
		response.buff[0] = modbusAddressSlave;
		response.buff[1] = (byte) (request.buff[1] + EXCEPTION_MODIFIER);
		response.buff[2] = (byte) errorCode.getCode();
		sendFrame(response);

	}

	/**
	 * Функция создает новое сообщение и заполняет заголовок согласно
	 * параметрам.
	 * 
	 * @param function
	 *            Код функции
	 * @param length
	 *            Количество байт в сообщение
	 * @param transId
	 *            Идентификатор транзакции, только для ModbusTCP
	 * @return возвращает новое сообщение
	 */
	private ModbusMessage makeHead(Function function, int length, int transId) {
		ModbusMessage response = new ModbusMessage();
		response.transId = transId;
		response.length = length;
		response.buff[0] = modbusAddressSlave;
		response.buff[1] = function.getCode();
		return response;
	}

	/**
	 * Проверка адреса регистра и количество регистров.
	 * 
	 * @param reference
	 *            Адрес регистра
	 * @param length
	 *            Количество регистров
	 * @param headLength
	 *            Количество байт в заголовке
	 * @return Возвращает код ошибки или null если ошибок нет.
	 */
	private ErrorCode checkRefAndLen(int reference, int length, int headLength) {

		// Проверка максимального количества считываемых регистров
		if (length > ((MAX_MESSAGE_LENGTH - headLength) / 2)) {
			logger.warn("ModbusSlave: Некорректное количество данных в запросе [{}]", length);
			return ErrorCode.ILLEGAL_DATA_VALUE;
		}

		// Проверка адреса и количества
		if ((reference + length) > registersCount) {
			logger.warn("ModbusSlave: Некорректный адрес регистра [{}] или количество [{}]", reference, length);
			return ErrorCode.ILLEGAL_DATA_ADDRESS;
		}
		return null;
	}
}
