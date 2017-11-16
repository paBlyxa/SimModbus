package com.we.simModbus.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.we.modbus.ModbusTCPMaster;
import com.we.modbus.model.DataModelTable;
import com.we.modbus.model.ModbusDataModel;

import javafx.concurrent.Task;

public class ReadMultipleRegsTask extends Task<Void> {

	private final static Logger logger = LoggerFactory.getLogger(ReadMultipleRegsTask.class);

	private final int unitId;
	private final int reference;
	private final int length;
	private final int transId;
	private final ModbusTCPMaster modbusTCPMaster;
	private final ModbusDataModel dataModel;

	public ReadMultipleRegsTask(int unitId, int reference, int length, int transId, ModbusTCPMaster modbusTCPMaster,
			ModbusDataModel modbusDataModel) {
		this.unitId = unitId;
		this.length = length;
		this.reference = reference;
		this.transId = transId;
		this.modbusTCPMaster = modbusTCPMaster;
		this.dataModel = modbusDataModel;
		logger.debug("Create new ReadMultipleRegsTask");
	}

	@Override
	protected Void call() throws Exception {
		if (!isCancelled()){
			byte[] readResults = new byte[length * 2];
			modbusTCPMaster.readMultipleRegisters(unitId, reference, length, transId, readResults);
			this.updateMessage("Registers has been read from: " + reference + ", length: " + length);
			dataModel.write(reference, length, DataModelTable.HoldingRegisters, readResults);
			logger.debug("Execution reading regs has been completed");
		} else {
			logger.debug("ReadMultipleRegsTask is cancelled");
		}
		return null;
	}

}
