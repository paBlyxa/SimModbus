package com.we.modbus;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModbusSlave extends Modbus {

	private final static Logger logger = LoggerFactory.getLogger(ModbusMaster.class);
	
	private int address;
	private final int maxAddress;
	private final MemoryModel memoryModel;
	
	 /**
     * Конструктор класса. Принимает объект ModbusTransport
     * 
     * @Author Pablo
     * 
     * @param transport Объект ModbusTransport, который должен использоваться
     * этим Modbus объектом для приема и посылки сообщений
     * @param maxAddress Максимальный адрес регистра
     */
	public ModbusSlave(ModbusTransport transport, MemoryModel memoryModel, int maxAddress) {
		super(transport);
		this.memoryModel = memoryModel;
		this.maxAddress = maxAddress;
	}

	/**
	 * Метод для обработки нового запроса.
	 * 
	 * @return возвращает количество принятых байт или
	 * -1 если неуспешно.
	 * @throws IOException 
	 */
	public int handleRequest() throws IOException{
		ModbusMessage request = new ModbusMessage();
		
		// Получаем новый запрос
		transport.receiveFrame(request);
		
		// Проверка адреса
		if (address != 0 && ((request.buff[0] & 0xFF) != address)){
			logger.warn("ModbusSlave: Некорректный адрес ведомого [{}]", request.buff[0]);
			return -1;
		}
		
		// Проверям код функции
		Function function = null;
		for (Function fun : Function.values()){
			if (fun.getCode() == request.buff[1]){
				function = fun;
				break;
			}
		}	
		if (function == null){
			logger.warn("ModbusSlave: Некорректный код функции [{}]", request.buff[1]);
			sendFaultAnswer(request, ErrorCode.UnknownFunctionCode);
			return -1;
		}
		
		// Адрес регистра
		int regAddress = ((request.buff[2] & 0xFF) << 8) + (request.buff[3] & 0xFF);
		
		switch(function){
		case READ_COIL_STATUS:
			if (regAddress / 16 > maxAddress){
				logger.warn("ModbusSlave: Некорректный адрес флага [{}]", regAddress);
				sendFaultAnswer(request, ErrorCode.UnavailableRegisterAddress);
				return -1;
			}
			
			break;
		case FORCE_MULTIPLE_COILS:
			break;
		case FORCE_SINGLE_COIL:
			break;
		case MASK_WRITE_REGISTER:
			break;
		case READ_DISCRETE_INPUTS:
			break;
		case READ_INPUT_REGISTERS:
			break;
		case READ_MULTIPLE_REGISTERS:
			break;
		case WRITE_MULTIPLE_REGISTERS:
			break;
		case WRITE_SINGLE_REGISTER:
			break;
		default:
			logger.warn("ModbusSlave: Неизвестная или нереализованная функция [{}]", function);
			return -1;
		}
		
		return request.length;
	}
	
	/**
	 * Отправляем ответ с ошибкой
	 * 
	 * @param request принятый запрос
	 * @param errorCode код ошибки
	 * @throws IOException 
	 */
	private void sendFaultAnswer(ModbusMessage request, ErrorCode errorCode) throws IOException{
		ModbusMessage response = new ModbusMessage();
		response.transId = request.transId;
		response.length = 3;
		response.buff[0] = request.buff[0];
		response.buff[1] = (byte) (request.buff[1] + EXCEPTION_MODIFIER);
		response.buff[2] = (byte) errorCode.getCode();
		sendFrame(response);
		
	}
}
