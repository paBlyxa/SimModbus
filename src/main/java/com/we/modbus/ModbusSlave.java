package com.we.modbus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModbusSlave extends Modbus {

	private final static Logger logger = LoggerFactory.getLogger(ModbusMaster.class);
	
	private int address;
	
	 /**
     * Конструктор класса. Принимает объект ModbusTransport
     * 
     * @Author Pablo
     * 
     * @param transport Объект ModbusTransport, который должен использоваться
     * этим Modbus объектом для приема и посылки сообщений
     */
	public ModbusSlave(ModbusTransport transport) {
		super(transport);
	}

	/**
	 * Метод для обработки нового запроса.
	 * 
	 * @return возвращает количество принятых байт или
	 * -1 если неуспешно.
	 */
	public int handleRequest(){
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
			sendFaultAnswer(request.buff[1], ErrorCode.UnknownFunctionCode);
			return -1;
		}
		
		switch(function){
		case FORCE_MULTIPLE_COILS:
			break;
		case FORCE_SINGLE_COIL:
			break;
		case MASK_WRITE_REGISTER:
			break;
		case READ_COIL_STATUS:
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
	 * @param functionCode принятый код функции
	 * @param errorCode код ошибки
	 */
	private void sendFaultAnswer(byte functionCode, ErrorCode errorCode){
		// TODO send fault answer
	}
}
