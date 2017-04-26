package com.we.modbus;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fakadey
 */
public class Modbus {
    
	private static final Logger logger = LoggerFactory.getLogger(Modbus.class);  
    
    /**
     * Максимальный допустимый адрес ведомого
     */
    public static final int ADDRESS_MAX = 65535;
    
    /**
     * Максимальная длина сообщения
     */
    public static final int MAX_MESSAGE_LENGTH = 256;
    
    /**
     * Максимальное значение для 16 бит-го типа UINT
     */
    public static final int UINT16_MAX = (int) 0xFFFF;
    
    /**
     * Минимальное значения для 16 бит-го типа UINT
     */
    public static final int UINT16_MIN = (int) 0x0000;
    
    /**
     * Максимальное значение для 8 бит-го типа UINT
     */
    public static final int UINT8_MAX = (int) 0xFF;
    
    /**
     * Минимальное значение для 8 бит-го типа UINT
     */
    public static final int UINT8_MIN = (int) 0x00;
    
    /**
     * Модификатор кода функции. Этот модификатор добавляется
     * к коду функции для сигнализации ошибки
     */
    public static final byte EXCEPTION_MODIFIER = (byte) 0x80;
    
    /**
     * Modbus Transport объект через который будет прозиводится
     * весь обмен данными
     */
    protected ModbusTransport transport;
    
    /**
     * Конструктор класса. Инициализирует поле типа
     * ModbusTransport
     * 
     * @author Pablo
     * 
     * @param transport Объект типа ModbusTransport, который
     * используется для отправления и приема посылок
     */
    protected Modbus(ModbusTransport transport){
        this.transport = transport;
    }
    
    /**
     * Метод для отправления сообщения. Возвращаемое значение
     * функции отражает статус отправки.
     * 
     * @Author Pablo
     * 
     * @param msg Модбас сообщения для отправки
     * @return Флаг успешного отправления
     * @throws IOException 
     */
    public boolean sendFrame(ModbusMessage msg) throws IOException{
        logger.debug("Модбас: Отправление данных {}", msg);
        return transport.sendFrame(msg);
    }
    
    /**
     * Метод для приема сообщений. Возвращает количество
     * байт принятого сообщения. Метод блокирует выполнение,
     * пока не будет принято сообщение или не разорвется соединение
     * 
     * @Author Pablo
     * 
     * @param msg Принятое модбас сообщение
     * @return Возвращает количество принятых байт
     */
    public int receiveFrame(ModbusMessage msg){
        int result = transport.receiveFrame(msg);
    	logger.debug("Модбас: Прием данных {}", msg);
    	return result;
    }
    
    /**
     * Метод для закрытия соединения
     * @throws IOException 
     */
    public void close() throws IOException{
    	transport.close();
    }
    
    public enum Function{
    	/**
    	 * Код (0x01) команды для чтения нескольких регистров флагов
    	 */
    	READ_COIL_STATUS((byte) 0x01),

    	/**
    	 * Код (0x02) команды для чтения нескольких дискретных входов
    	 */
    	READ_DISCRETE_INPUTS((byte) 0x02),
    	
        /**
         * Код (0x03) команды для чтения нескольких регистров
         */
        READ_MULTIPLE_REGISTERS((byte) 0x03),
        
        /**
         * Код (0x04) команды для чтения входных регистров
         */
        READ_INPUT_REGISTERS((byte) 0x04),

        /**
         * Код (0x05) команды для записи одного флага
         */
        FORCE_SINGLE_COIL((byte) 0x05),
        
        /**
         * Код (0x06) команды для записи одного регистра
         */
        WRITE_SINGLE_REGISTER((byte) 0x06),
    	
        /**
         * Код (0x0F) команды для записи нескольких регистров флагов
         */
        FORCE_MULTIPLE_COILS((byte) 0x0F),
        
        /**
         * Код (0x10) команды для записи нескольких регистров хранения
         */
        WRITE_MULTIPLE_REGISTERS((byte) 0x10),
        
        /**
         * Код (0x16) команды для записи в один регистр с использованием маски "И" и "ИЛИ"
         */
        MASK_WRITE_REGISTER((byte) 0x16);
        
        
        private Function(byte code){
        	this.code = code;
        }
        
        private byte code;
        
        public byte getCode(){
        	return code;
        }
    }
    
    public enum ErrorCode {
    	/**
    	 * Принятый код функции не может быть обработан (0x01)
    	 */
    	UnknownFunctionCode(1, "Принятый код функции не может быть обработан"),
    	/**
    	 * Адрес данных, указанный в запросе, недоступен (0x02)
    	 */
    	UnavailableRegisterAddress(2, "Адрес данных, указанный в запросе, недоступен"),
    	/**
    	 * Значение, содержащееся в поле данных запроса, является недопустипой величиной (0x03)
    	 */
    	UnavailableValue(3, "Значение, содержащееся в поле данных запроса, является недопустипой величиной"),
    	/**
    	 * Невосстанавливаемая ошибка имела место, пока ведомое устройсво пыталось выполнить действие (0x04)
    	 */
    	SlaveError(4, "Невосстанавливаемая ошибка имела место, пока ведомое устройсво пыталось выполнить действие"),
    	/**
    	 * Ведомое устройство приняло запрос и обрабатывает его, но это требует много времени (0x05)
    	 */
    	RequestTooLong(5, "Ведомое устройство приняло запрос и обрабатывает его, но это требует много времени"),
    	/**
    	 * Ведомое устройсво занято обработкой команды (0x06)
    	 */
    	SlaveBusy(6, "Ведомое устройсво занято обработкой команды"),
    	/**
    	 * Ведомое устройсво не может выполнить программную функцию (0x07)
    	 */
    	UnavailableFunction(7, "Ведомое устройсво не может выполнить программную функцию"),
    	/**
    	 * Ведомое устройство при чтении памяти обнаружило ошибку (0x08)
    	 */
    	MemoryFault(8, "Ведомое устройство при чтении памяти обнаружило ошибку"),
    	/**
    	 * Неизвестная ошибка
    	 */
    	UnknownError(9, "Неизвестная ошибка");
    	
    	private final int code;
    	private final String text;
    	
    	private ErrorCode(int code, String text){
    		this.code = code;
    		this.text = text;
    	}
    	
    	public static ErrorCode get(int code){
    		for (ErrorCode errorCode : ErrorCode.values()){
    			if (errorCode.getCode() == code){
    				return errorCode;
    			}
    		}
    		return UnknownError;
    	}
    	
    	public int getCode(){
    		return code;
    	}
    	
    	public String getDescription(){
    		return text;
    	}
    	
    }
}
