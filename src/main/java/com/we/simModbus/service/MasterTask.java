package com.we.simModbus.service;

import java.io.IOException;

import com.we.modbus.ModbusTCPMaster;

import javafx.concurrent.Task;

public class MasterTask extends Task<ModbusTCPMaster> {

	private ModbusTCPMaster modbusTCPMaster;
	private String address;
	private int port;
	
	public MasterTask(String address, int port){
		this.address = address;
		this.port = port;
	}
	
	@Override
	protected ModbusTCPMaster call() throws Exception {
		
		// Update title
		updateTitle("Modbus TCP Master Task");
		
		// Update message
		updateMessage("Try to connect to the server: " + address + ":" + port);
		try {
			modbusTCPMaster = new ModbusTCPMaster(address, port);
			updateMessage("Connection to the server is established");
			return modbusTCPMaster;
		} catch (IOException e){
			this.updateMessage(e.getMessage());
			throw e;
		}
	}

}
