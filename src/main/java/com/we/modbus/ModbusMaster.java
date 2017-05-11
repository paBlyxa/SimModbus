package com.we.modbus;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.we.modbus.model.ErrorCode;
import com.we.modbus.model.Function;
import com.we.modbus.model.ModbusMessage;

/**
 *
 * @author fakadey
 */
public class ModbusMaster extends Modbus {

	private final static Logger logger = LoggerFactory.getLogger(ModbusMaster.class);

	/**
	 * Конструктор класса. Принимает объект ModbusTransport
	 * 
	 * @Author Pablo
	 * 
	 * @param transport
	 *            Объект ModbusTransport, который должен использоваться этим
	 *            Modbus объектом для приема и посылки сообщений
	 */
	public ModbusMaster(ModbusTransport transport) {
		super(transport);
	}

	/**
	 * Функция для чтения значений из нескольких регистров флагов. Возвращает
	 * True если чтение успешное.
	 * 
	 * @Author Pablo
	 * 
	 * @param reference
	 *            Адрес первого флага для чтения
	 * @param length
	 *            Количество флагов для чтения
	 * @param results
	 *            Ссылка на массив, в который пишется результат
	 * @return Возвращает true, если чтение успешно
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public boolean readCoilStatus(int reference, int length, int[] results)
			throws IllegalArgumentException, IOException {

		return readRegs(0, reference, length, 0, Function.READ_COIL_STATUS, results);
	}

	/**
	 * Функция для чтения значений из нескольких регистров флагов. Возвращает
	 * True если чтение успешное.
	 * 
	 * @Author Pablo
	 * 
	 * @param unitId
	 *            Адрес ведомого устройства
	 * @param reference
	 *            Адрес первого флага для чтения
	 * @param length
	 *            Количество флагов для чтения
	 * @param results
	 *            Ссылка на массив, в который пишется результат
	 * @return Возвращает true, если чтение успешно
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public boolean readCoilStatus(int unitId, int reference, int length, int[] results)
			throws IllegalArgumentException, IOException {

		return readRegs(unitId, reference, length, 0, Function.READ_COIL_STATUS, results);
	}

	/**
	 * Функция для чтения значений из нескольких дискретных входов. Возвращает
	 * True если чтение успешное.
	 * 
	 * @author Pablo
	 * 
	 * @param reference
	 *            Адрес первого дискретного входа для чтения
	 * @param length
	 *            Количество дискретных входов для чтения
	 * @param results
	 *            Ссылка на массив, в который пишется результат
	 * @return Возвращает true, если чтение успешно
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public boolean readDiscreteInputs(int reference, int length, int[] results)
			throws IllegalArgumentException, IOException {

		return readRegs(0, reference, length, 0, Function.READ_DISCRETE_INPUTS, results);
	}

	/**
	 * Функция для чтения значений из нескольких дискретных входов. Возвращает
	 * True если чтение успешное.
	 * 
	 * @author Pablo
	 * 
	 * @param unitId
	 *            Адрес ведомого устройства
	 * @param reference
	 *            Адрес первого дискретного входа для чтения
	 * @param length
	 *            Количество дискретных входов для чтения
	 * @param results
	 *            Ссылка на массив, в который пишется результат
	 * @return Возвращает true, если чтение успешно
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public boolean readDiscreteInputs(int unitId, int reference, int length, int[] results)
			throws IllegalArgumentException, IOException {

		return readRegs(unitId, reference, length, 0, Function.READ_DISCRETE_INPUTS, results);
	}

	/**
	 * Фукнция для чтения нескольких регистров из ведомого. Возвращает True если
	 * чтение успешное
	 * 
	 * @Author Pablo
	 * 
	 * @param reference
	 *            Адрес первого регистра для чтения
	 * @param length
	 *            Количество регистров для чтения
	 * @param results
	 *            Ссылка на массив, в который пишется результат
	 * @return Возращает true если результат успешен
	 * @throws IllegalArgumentException
	 *             Возникает если масив results меньше принятых данных
	 * @throws IOException
	 */
	public boolean readMultipleRegisters(int reference, int length, int[] results)
			throws IllegalArgumentException, IOException {

		return readRegs(0, reference, length, 0, Function.READ_MULTIPLE_REGISTERS, results);
	}

	/**
	 * Фукнция для чтения нескольких регистров из ведомого. Возвращает True если
	 * чтение успешное
	 * 
	 * @Author Pablo
	 * 
	 * @param unitId
	 *            Адрес ведомого устройства
	 * @param reference
	 *            Адрес первого регистра для чтения
	 * @param length
	 *            Количество регистров для чтения
	 * @param transId
	 *            Идентификатор транзакции
	 * @param results
	 *            Ссылка на массив, в который пишется результат
	 * @return Возращает true если результат успешен
	 * @throws IllegalArgumentException
	 *             Возникает если масив results меньше принятых данных или
	 *             некорректные аргументы
	 * @throws IOException
	 */
	public boolean readMultipleRegisters(int unitId, int reference, int length, int transId, int[] results)
			throws IllegalArgumentException, IOException {

		return readRegs(unitId, reference, length, transId, Function.READ_MULTIPLE_REGISTERS, results);
	}

	/**
	 * Фукнция для чтения входных регистров из ведомого. Возвращает True если
	 * чтение успешное
	 * 
	 * @Author Pablo
	 * 
	 * @param reference
	 *            Адрес первого регистра для чтения
	 * @param length
	 *            Количество регистров для чтения
	 * @param results
	 *            Ссылка на массив, в который пишется результат
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public boolean readInputRegisters(int reference, int length, int[] results)
			throws IllegalArgumentException, IOException {

		return readRegs(0, reference, length, 0, Function.READ_INPUT_REGISTERS, results);
	}

	/**
	 * Фукнция для чтения входных регистров из ведомого. Возвращает True если
	 * чтение успешное
	 * 
	 * @Author Pablo
	 * 
	 * @param unitId
	 *            Адрес ведомого устройства
	 * @param reference
	 *            Адрес первого регистра для чтения
	 * @param length
	 *            Количество регистров для чтения
	 * @param transId
	 *            Идентификатор транзакции
	 * @param results
	 *            Ссылка на массив, в который пишется результат
	 * @return Возращает true если результат успешен
	 * @throws IllegalArgumentException
	 *             Возникает если масив results меньше принятых данных или
	 *             некорректные аргументы
	 * @throws IOException
	 */
	public boolean readInputRegisters(int unitId, int reference, int length, int transId, int[] results)
			throws IllegalArgumentException, IOException {

		return readRegs(unitId, reference, length, transId, Function.READ_INPUT_REGISTERS, results);
	}

	/**
	 * Функция для записи в один регистр хранения. Возвращает True если запись
	 * успешна
	 * 
	 * @author Pablo
	 * 
	 * @param reference
	 *            Адрес регистра для записи
	 * @param value
	 *            Значение для записи в регистр
	 * @return возращает True если запись успешна
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public boolean writeSingleRegister(int reference, int value) throws IllegalArgumentException, IOException {
		int[] values = new int[1];
		values[0] = value;
		return writeRegs(0, reference, 1, 0, Function.WRITE_SINGLE_REGISTER, values);
	}

	/**
	 * Функция для записи в один регистр хранения. Возвращает True если запись
	 * успешна
	 * 
	 * @author Pablo
	 * 
	 * @param unitId
	 *            Адрес ведомого
	 * @param reference
	 *            Адрес регистра для записи
	 * @param transId
	 *            Идентификатор транзакции
	 * @param value
	 *            Значение для записи в регистр
	 * @return возращает True если запись успешна
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public boolean writeSingleRegister(int unitId, int reference, int transId, int value)
			throws IllegalArgumentException, IOException {
		int[] values = new int[1];
		values[0] = value;
		return writeRegs(0, reference, 1, 0, Function.WRITE_SINGLE_REGISTER, values);
	}

	/**
	 * Функция для установки одного флага в значение True (1). Возвращет True
	 * если запись успешна.
	 * 
	 * @author Pablo
	 * 
	 * @param reference
	 * 			Адрес флага для записи
	 * @return возвращает true если запись успешна
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public boolean setSingleCoil(int reference) throws IllegalArgumentException, IOException{
		int[] values = {0xFF00};
		return writeRegs(0, reference, 1, 0, Function.WRITE_SINGLE_COIL, values);
	}
	
	/**
	 * Функция для установки одного флага в значение True (1). Возвращет True
	 * если запись успешна.
	 * 
	 * @author Pablo
	 * 
	 * @param unitId
	 *            Адрес ведомого
	 * @param reference
	 * 			Адрес флага для записи
	 * @param transId
	 *            Идентификатор транзакции
	 * @return возвращает true если запись успешна
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public boolean setSingleCoil(int unitId, int reference, int transId) throws IllegalArgumentException, IOException{
		int[] values = {0xFF00};
		return writeRegs(unitId, reference, 1, transId, Function.WRITE_SINGLE_COIL, values);
	}	
	
	/**
	 * Функция для сброса одного флага в значение False (0). Возвращет True
	 * если запись успешна.
	 * 
	 * @author Pablo
	 * 
	 * @param reference
	 * 			Адрес флага для записи
	 * @return возвращает true если запись успешна
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public boolean resetSingleCoil(int reference) throws IllegalArgumentException, IOException{
		int[] values = {0x0000};
		return writeRegs(0, reference, 1, 0, Function.WRITE_SINGLE_COIL, values);
	}
	
	/**
	 * Функция для сброса одного флага в значение False (0). Возвращет True
	 * если запись успешна.
	 * 
	 * @author Pablo
	 * 
	 * @param unitId
	 *            Адрес ведомого
	 * @param reference
	 * 			Адрес флага для записи
	 * @param transId
	 *            Идентификатор транзакции
	 * @return возвращает true если запись успешна
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public boolean resetSingleCoil(int unitId, int reference, int transId) throws IllegalArgumentException, IOException{
		int[] values = {0x0000};
		return writeRegs(unitId, reference, 1, transId, Function.WRITE_SINGLE_COIL, values);
	}	

	/**
	 * Функция для заниси значений в несколько регистров флагов. Возвращает
	 * true если запись успешна.
	 * 
	 * @author Pablo
	 * 
	 * @param reference
	 * 			Адрес первого флага для записи
	 * @param length
	 * 			Количество флагов для записи
	 * @param values
	 * 			Значения для записи
	 * @return возвращает true если запись успешна
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public boolean forceMultipleCoils(int reference, int length, int[] values) throws IllegalArgumentException, IOException {
		return writeRegs(0, reference, length, 0, Function.WRITE_MULTIPLE_COILS, values);
	}
	
	/**
	 * Функция для заниси значений в несколько регистров флагов. Возвращает
	 * true если запись успешна.
	 * 
	 * @author Pablo
	 * 
	 * @param unitId
	 * 			Адрес ведомого
	 * @param reference
	 * 			Адрес первого флага для записи
	 * @param length
	 * 			Количество флагов для записи
	 * @param transId
	 * 			Идентификатор транзакции
	 * @param values
	 * 			Значения для записи
	 * @return возвращает true если запись успешна
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public boolean forceMultipleCoils(int unitId, int reference, int length, int transId, int[] values) throws IllegalArgumentException, IOException {
		return writeRegs(unitId, reference, length, transId, Function.WRITE_MULTIPLE_COILS, values);
	}
	
	/**
	 * Фунцкия для записи нескольких регстров в ведомом. Возвращает true если
	 * запись успешна
	 * 
	 * @Author Pablo
	 * 
	 * @param reference
	 *            Адрес первого регистра для записи
	 * @param length
	 *            Количество регистров для записи
	 * @param values
	 *            Значения для записи
	 * @return Возвращает true если результат успешен
	 * @throws IllegalArgumentException
	 *             Возникает если некорректные аргументы
	 * @throws IOException
	 */
	public boolean writeMultipleRegisters(int reference, int length, int[] values)
			throws IllegalArgumentException, IOException {
		return writeRegs(0, reference, length, 0, Function.WRITE_MULTIPLE_REGISTERS, values);
	}

	/**
	 * Фунцкия для записи нескольких регистров в ведомом. Возвращает true если
	 * запись успешна
	 * 
	 * @Author Pablo
	 * 
	 * @param unitId
	 *            Адрес ведомого
	 * @param reference
	 *            Адрес первого регистра для записи
	 * @param length
	 *            Количество регистров для записи
	 * @param transId
	 *            Идентификатор транзакции
	 * @param values
	 *            Значения для записи
	 * @return Возвращает true если результат успешен
	 * @throws IllegalArgumentException
	 *             Возникает если некорректные аргументы
	 * @throws IOException
	 */
	public boolean writeMultipleRegisters(int unitId, int reference, int length, int transId, int[] values)
			throws IllegalArgumentException, IOException {

		return writeRegs(unitId, reference, length, transId, Function.WRITE_MULTIPLE_REGISTERS, values);
	}

	/**
	 * Функция для записи в один регистр с использованием маски "И" и "ИЛИ". Возвращает true
	 * если запись успешна
	 * 
	 * @author Pablo
	 * 
	 * @param reference
	 * 			Адрес первого регистра для записи
	 * @param values
	 * 			Значения для записи
	 * @return Возвращает true если результат успешен
	 * @throws IllegalArgumentException
	 * 			Возникает если неккоректные аргументы
	 * @throws IOException
	 */
	public boolean maskWriteRegister(int reference, int[] values)
			throws IllegalArgumentException, IOException {
		return writeRegs(0, reference, 1, 0, Function.MASK_WRITE_REGISTER, values);
	}
	
	/**
	 * Функция для записи в один регистр с использованием маски "И" и "ИЛИ". Возвращает true
	 * если запись успешна
	 * 
	 * @author Pablo
	 * 
	 * @param unitId
	 * 			Адрес ведомого
	 * @param reference
	 * 			Адрес первого регистра для записи
	 * @param transId
	 * 			Количество регистров для записи
	 * @param values
	 * 			Значения для записи
	 * @return Возвращает true если результат успешен
	 * @throws IllegalArgumentException
	 * 			Возникает если неккоректные аргументы
	 * @throws IOException
	 */
	public boolean maskWriteRegister(int unitId, int reference, int transId, int[] values)
			throws IllegalArgumentException, IOException {
		return writeRegs(unitId, reference, 1, transId, Function.MASK_WRITE_REGISTER, values);
	}
	
	/**
	 * Фунцкия для записи нескольких регистров в ведомом. Возвращает true если
	 * запись успешна.
	 * 
	 * @author Pablo
	 * 
	 * @param unitId
	 *            Адрес ведомого устройства
	 * @param reference
	 *            Адрес первого регистра для записи
	 * @param length
	 *            Количество регистров для записи
	 * @param transId
	 *            Идентификатор транзакции
	 * @param function
	 *            Код функции
	 * @param values
	 *            Значения для записи
	 * @return возвращет true, если запись успешна.
	 * @throws IllegalArgumentException,
	 *             IOException
	 */
	private boolean writeRegs(int unitId, int reference, int length, int transId, Function function, int[] values)
			throws IllegalArgumentException, IOException {

		// Проверяем аргументы
		checkArgs(unitId, reference, length, transId);

		// byte 0 = адрес ведомого
		// byte 1 = код функции
		// byte 2 = Старший байт адреса регистра
		// byte 3 = Младший байт адреса регистра
		// byte 4 = Старший байт количества регистров
		// byte 5 = Младший байт количества регистров
		// byte 6 = Количество следующих байт данных
		// byte 7+2n = Старший байт данных n
		// byte 8+2n = Младший байт данных n
		ModbusMessage request = makeHead(unitId, reference, length, transId, function);
		
		if (function == Function.WRITE_MULTIPLE_REGISTERS) {
			
			// Количество байт данных
			request.buff[6] = (byte) (2 * length);

			// Устанавливаем данные для записи
			for (int i = 0; i < length; i++) {
				request.buff[7 + 2 * i] = (byte) (values[i] >> 8);
				request.buff[8 + 2 * i] = (byte) (values[i]);
			}

			// Длина запроса
			request.length = 7 + 2 * length;

		} else if (function == Function.WRITE_MULTIPLE_COILS) {
			
			// Количество байт данных
			request.buff[6] = (byte) ((length + 7) / 8);
			
			// Устанавливаем данные для записи
			for (int i = 0; i < ((length + 7) / 8); i++) {
				request.buff[7 + i] = (byte) (values[i / 2] >> (i % 2 == 0 ? 8 : 0));
			}
			
			// Длина запроса
			request.length = 7 + ((length + 7) / 8);
			
		} else if (function == Function.MASK_WRITE_REGISTER) {
			// Устанавливаем данные для записи
			request.buff[4] = (byte) (values[0] >> 8);
			request.buff[5] = (byte) (values[0]);
			request.buff[6] = (byte) (values[1] >> 8);
			request.buff[7] = (byte) (values[1]);
			
			// Длина запроса
			request.length = 8;
		} else {
			
			// Устанавливаем данные для записи
			request.buff[4] = (byte) (values[0] >> 8);
			request.buff[5] = (byte) (values[0]);

			// Длина запроса
			request.length = 6;
		}
		
		ModbusMessage response = sendRequest(request);

		// Посылка запроса
		if (response == null) {
			return false;
		}

		// Проверяем совпадает ли идентификатор транзакции
		if (response.transId != transId) {
			logger.warn("ModbusMaster: Некорректный идентификатор транзации [{}] в ответе", response.transId);
			return false;
		}

		// Проверяем адрес ведомого
		if (response.buff[0] != ((byte) (unitId & 0xFF))) {
			logger.warn("ModbusMaster: Некорректный адрес ведомого [{}]", response.buff[0]);
			return false;
		}

		// Проверяем исключения
		if (response.buff[1] == (byte) (function.getCode() + EXCEPTION_MODIFIER)) {
			logger.warn("ModbusMaster: Modbus exception [{}], errorCode [{}] - {}", response.buff[1],
					ByteUtils.toHex(response.buff[2]),
					ErrorCode.get(response.buff[2]).getDescription());
		}

		// Проверяем код функции
		if (response.buff[1] != function.getCode()) {
			logger.warn("ModbusMaster: Некорректный код функции [{}] в ответе", response.buff[1]);
			return false;
		}

		// Ожидаем получить ответ:
		// byte 0 = адрес ведомого
		// byte 1 = код функции
		// byte 2 = старший байт адреса регистра
		// byte 3 = младший байт адреса регистра
		// byte 4 = старший байт количества регистров
		// byte 5 = младший байт количества регистров
		//
		// Ожидаем получить ответ длиной 6 байт для функций кроме 22й (для 22й - 8)
		if (response.length != (function != Function.MASK_WRITE_REGISTER ? 6 : 8)) {
			logger.warn("ModbusMaster: Некорректная длина ответа [{}]", response.length);
			return false;
		}

		// Проверяем адрес регистра
		if (reference != ((response.buff[2] << 8) + (response.buff[3]) & 0xFFFF)) {
			logger.warn("ModbusMaster: Incorrect return reference number [{}]",
					((response.buff[2] << 8) + (response.buff[3]) & 0xFFFF));
			return false;
		}

		if (function != Function.WRITE_SINGLE_REGISTER && function != Function.WRITE_SINGLE_COIL && function != Function.MASK_WRITE_REGISTER) {
			// Проверяем количество регистров
			if (length != ((response.buff[4] << 8) + (response.buff[5]) & 0xFFFF)) {
				logger.warn("ModbusMaster: Incorrect return word count [{}]",
						((response.buff[4] << 8) + (response.buff[5]) & 0xFFFF));
				return false;
			}
		} else if (function == Function.MASK_WRITE_REGISTER){
			// Проверяем полученное значение, должно быть то же, что отправляли
			if (((((response.buff[4] << 8) + response.buff[5]) & 0xFFFF) != values[0]) &&
					((((response.buff[6] << 8) + response.buff[7]) & 0xFFFF) != values[1])) {
				logger.warn("ModbusMaster: Incorrect return value [{}, {}]",
						((response.buff[4] << 8) + (response.buff[5]) & 0xFFFF),
						((response.buff[6] << 8) + (response.buff[7]) & 0xFFFF));
				return false;
			}
		} else {
			// Проверяем полученное значение, должно быть то же, что отправляли
			if ((((response.buff[4] << 8) + response.buff[5]) & 0xFFFF) != values[0]) {
				logger.warn("ModbusMaster: Incorrect return value [{}]",
						((response.buff[4] << 8) + (response.buff[5]) & 0xFFFF));
				return false;
			}
		}

		// Все в порядке возращаем успешное выполнение
		return true;
	}

	/**
	 * Функция для проверки входных аргументов
	 * 
	 * @param unitId
	 *            Адрес ведомого устройства
	 * @param reference
	 *            Адрес первого регистра для чтения
	 * @param length
	 *            Количество регистров для чтения
	 * @param transId
	 *            Идентификатор транзакции
	 * @throws IllegalArgumentException
	 *             Возникает если аргументы некорректны
	 */
	private void checkArgs(int unitId, int reference, int length, int transId) throws IllegalArgumentException {

		if (unitId > UINT8_MAX || unitId < UINT8_MIN) {
			throw new IllegalArgumentException("Адрес ведомого вне диапазона 8 бит UINT");
		}

		if (reference > UINT16_MAX || reference < UINT16_MIN) {
			throw new IllegalArgumentException("Адрес регистра вне диапазона 16 бит UINT");
		}

		if (length > UINT16_MAX || length < (UINT16_MIN + 1)) {
			throw new IllegalArgumentException("Количество регистров вне диапазона 16 бит UINT");
		}

		if (transId > UINT16_MAX || transId < UINT16_MIN) {
			throw new IllegalArgumentException("Номер транзации вне диапазона 16 бит UINT");
		}
	}

	/**
	 * Функция создает заголовк сообщения
	 * 
	 * @Author Pablo
	 * 
	 * @param unitId
	 *            Адрес ведомого устройства
	 * @param reference
	 *            Адрес первого регистра для чтения
	 * @param length
	 *            Количество регистров для чтения
	 * @param transId
	 *            Идентификатор транзакции
	 * @param codeFunction
	 *            Код функции Modbus
	 */
	private ModbusMessage makeHead(int unitId, int reference, int length, int transId, Function function) {

		ModbusMessage request = new ModbusMessage();
		// byte 0 = адрес ведомого
		// byte 1 = код функции
		// byte 2 = старший байт адреса регистра
		// byte 3 = младший байт адреса регистра
		// byte 4 = старший байт количества регистров
		// byte 5 = младший байт количества регистров
		request.buff[0] = (byte) ((unitId) & 0xFF);
		request.buff[1] = function.getCode();
		request.buff[2] = (byte) ((reference >> 8) & 0xFF);
		request.buff[3] = (byte) ((reference) & 0xFF);
		if (function != Function.WRITE_SINGLE_REGISTER && function != Function.WRITE_SINGLE_COIL && function != Function.MASK_WRITE_REGISTER) {
			request.buff[4] = (byte) ((length >> 8) & 0xFF);
			request.buff[5] = (byte) ((length) & 0xFF);

			// Количество байт в запросе
			request.buff[6] = 6;
		}
		// Идентификатор транзакции
		request.transId = transId;
		return request;
	}

	/**
	 * Фукнция для чтения регистров из ведомого. Возвращает True если чтение
	 * успешное
	 * 
	 * @Author Pablo
	 * 
	 * @param unitId
	 *            Адрес ведомого устройства
	 * @param reference
	 *            Адрес первого регистра для чтения
	 * @param length
	 *            Количество регистров для чтения
	 * @param transId
	 *            Идентификатор транзакции
	 * @param codeFunction
	 *            Код функции для чтения регистров
	 * @param results
	 *            Ссылка на массив, в который пишется результат
	 * @return Возращает true если результат успешен
	 * @throws IllegalArgumentException
	 *             Возникает если масив results меньше принятых данных или
	 *             некорректные аргументы
	 * @throws IOException
	 */
	private boolean readRegs(int unitId, int reference, int length, int transId, Function function, int[] results)
			throws IllegalArgumentException, IOException {

		// Проверяем входные аргументы
		checkArgs(unitId, reference, length, transId);

		ModbusMessage request = makeHead(unitId, reference, length, transId, function);

		request.length = 6;

		ModbusMessage response = sendRequest(request);
		
		if (response == null) {
			return false;
		}

		// Проверяем совпадает ли идентификатор транзакции
		if (response.transId != transId) {
			logger.warn("ModbusMaster: Некорректный идентификатор транзации [{}] в ответе", response.transId);
			return false;
		}

		// Проверяем адрес ведомого
		if (response.buff[0] != ((byte) (unitId & 0xFF))) {
			logger.warn("ModbusMaster: Некорректный адрес ведомого [{}]", response.buff[0]);
			return false;
		}

		// Проверяем исключения
		if (response.buff[1] == ((byte) (function.getCode() + EXCEPTION_MODIFIER))) {
			logger.warn("ModbusMaster: Modbus exception [{}], Code error [{}] - {}", ByteUtils.toHex(response.buff[1]),
					ByteUtils.toHex(response.buff[2]), ErrorCode.get(response.buff[2]).getDescription());
			return false;
		}

		// Проверяем код функции
		if (response.buff[1] != function.getCode()) {
			logger.warn("ModbusMaster: Некорректный код функции [{}] в ответе", response.buff[1]);
			return false;
		}

		// Проверяем количество байт данных
		if (response.length != (3 + (response.buff[2] & 0xFF))) {
			logger.warn("ModbusMaster: Некорректное количество байт в ответе [{}] и в данных [{}]", response.length,
					(3 + (response.buff[2] & 0xFF)));
			return false;
		}
		if (function == Function.READ_COIL_STATUS || function == Function.READ_DISCRETE_INPUTS) {
			if (response.length != (3 + ((length + 7) / 8))) {
				logger.warn(
						"ModbusMaster: Некорректное количество байт в ответе [{}] - не соответсвует количеству запрошенных флагов [{}]",
						response.length, length);
				return false;
			}
		} else {
			if (response.length != (3 + (2 * length))) {
				logger.warn(
						"ModbusMaster: Некорректное количество байт в ответе [{}] - не соответсвует количеству запрошенных регистров [{}]",
						response.length, length);
				return false;
			}
		}
		if (function == Function.READ_COIL_STATUS || function == Function.READ_DISCRETE_INPUTS) {
			// Копируем полученные данные в result
			for (int i = 0; i < (length + 7) / 8; i++) {
				results[i / 2] += (response.buff[3 + i] & 0xFF) << (i % 2 == 0 ? 8 : 0);
			}
		} else {
			// Копируем полученные данные в result
			for (int i = 0; i < length; i++) {
				results[i] = ((response.buff[3 + 2 * i] & 0xFF) << 8) + (response.buff[4 + 2 * i] & 0xFF);
			}
		}
		return true;
	}

	/**
	 * Функция отправляет запрос Modbus
	 * 
	 * @param request
	 *            Modbus запрос
	 * @return Возвращает ответ, если запрос отправлен успешно и получен ответ
	 * @throws IOException
	 */
	private ModbusMessage sendRequest(ModbusMessage request) throws IOException {

		if (!sendFrame(request)) {
			logger.debug("ModbusMaster: Посылка неуспешна");
			return null;
		}

		ModbusMessage response = new ModbusMessage();
		
		if (receiveFrame(response) < 1) {
			logger.debug("ModbusMaster: Прием неуспешен");
			return null;
		}

		// Проверяем ответ

		// Ответ должен быть не меньше 3 байт
		if (response.length < 3) {
			logger.warn("ModbusMaster: Некорректный размер [{}] принятого сообщения", response.length);
			return null;
		}
		return response;
	}
}
