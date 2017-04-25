package com.we.modbus;

public class ModbusSlave extends Modbus {

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

}
