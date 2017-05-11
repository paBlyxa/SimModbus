package com.we.modbus.model;

public enum DataModelTable {

	/**
	 * Discretes Input.
	 * This type of data can be provided by an I/O system.
	 * Type of READ-ONLY.
	 * Object type: Single bit.
	 */
	DiscretesInput,
	
	/**
	 * Coils.
	 * This type of data can be alterable by an application program.
	 * Type of READ-WRITE.
	 * Object type: Single bit.
	 */
	Coils,
	
	/**
	 * Input Registers.
	 * This type of data can be provided by an I/O system.
	 * Type of READ-ONLY.
	 * Object type: 16-bit word.
	 */
	InputRegisters,
	
	/**
	 * Holding Registers.
	 * This type of data can be alterable by an application program.
	 * Type of READ-WRITE.
	 * Object type: 16-bit word.
	 */
	HoldingRegisters;
}
