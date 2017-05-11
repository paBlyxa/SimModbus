package com.we.modbus.model;

public enum Function {
	/**
	 * Код (0x01) команды для чтения нескольких регистров флагов
	 */
	READ_COIL_STATUS((byte) 0x01, DataModelTable.Coils),

	/**
	 * Код (0x02) команды для чтения нескольких дискретных входов
	 */
	READ_DISCRETE_INPUTS((byte) 0x02, DataModelTable.DiscretesInput),

	/**
	 * Код (0x03) команды для чтения нескольких регистров
	 */
	READ_MULTIPLE_REGISTERS((byte) 0x03, DataModelTable.HoldingRegisters),

	/**
	 * Код (0x04) команды для чтения входных регистров
	 */
	READ_INPUT_REGISTERS((byte) 0x04, DataModelTable.InputRegisters),

	/**
	 * Код (0x05) команды для записи одного флага
	 */
	WRITE_SINGLE_COIL((byte) 0x05, DataModelTable.Coils),

	/**
	 * Код (0x06) команды для записи одного регистра
	 */
	WRITE_SINGLE_REGISTER((byte) 0x06, DataModelTable.HoldingRegisters),

	/**
	 * Код (0x0F) команды для записи нескольких регистров флагов
	 */
	WRITE_MULTIPLE_COILS((byte) 0x0F, DataModelTable.Coils),

	/**
	 * Код (0x10) команды для записи нескольких регистров хранения
	 */
	WRITE_MULTIPLE_REGISTERS((byte) 0x10, DataModelTable.HoldingRegisters),

	/**
	 * Код (0x16) команды для записи в один регистр с использованием маски "И" и
	 * "ИЛИ"
	 */
	MASK_WRITE_REGISTER((byte) 0x16, DataModelTable.HoldingRegisters);

	private Function(byte code, DataModelTable dataModelTable) {
		this.code = code;
		this.dataModelTable = dataModelTable;
	}

	private final byte code;
	private final DataModelTable dataModelTable;
	
	public byte getCode() {
		return code;
	}
	
	public DataModelTable getDataModelTable(){
		return dataModelTable;
	}
}
