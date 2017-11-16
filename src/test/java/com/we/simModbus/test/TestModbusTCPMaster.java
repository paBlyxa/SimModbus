package com.we.simModbus.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.we.modbus.ModbusTCPMaster;

public class TestModbusTCPMaster {

	private final static String ipAddress = "127.0.0.1";
	private final static int port = 502;

	@Test
	public void testReadCoilStatus() {
		try {
			ModbusTCPMaster modbusMaster = new ModbusTCPMaster(ipAddress, port);
			for (int i = 1; i < 20; i++) {
				byte[] results = new byte[(i + 7) / 8];
				assertTrue(modbusMaster.readCoilStatus(0, i, results));
				assertNotNull(results);
				System.out.println("Receive coil status from 1 to " + i + " values: " + Arrays.toString(results));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testReadDiscreteInputs() {
		try {
			ModbusTCPMaster modbusMaster = new ModbusTCPMaster(ipAddress, port);
			for (int i = 1; i < 20; i++) {
				byte[] results = new byte[(i + 7) / 8];
				assertTrue(modbusMaster.readDiscreteInputs(0, i, results));
				assertNotNull(results);
				System.out.println("Receive discrete inputs from 1 to " + i + " values: " + Arrays.toString(results));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testReadMultipleRegisters() {
		try {
			ModbusTCPMaster modbusMaster = new ModbusTCPMaster(ipAddress, port);
			for (int i = 1; i < 20; i++) {
				byte[] results = new byte[i * 2];
				assertTrue(modbusMaster.readMultipleRegisters(0, i, results));
				assertNotNull(results);
				System.out
						.println("Receive multiple registers from 1 to " + i + " values: " + Arrays.toString(results));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testReadInputRegisters() {
		try {
			ModbusTCPMaster modbusMaster = new ModbusTCPMaster(ipAddress, port);
			for (int i = 1; i < 20; i++) {
				byte[] results = new byte[i * 2];
				assertTrue(modbusMaster.readInputRegisters(0, i, results));
				assertNotNull(results);
				System.out.println("Receive input registers from 1 to " + i + " values: " + Arrays.toString(results));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testWriteSignleRegister() {
		try {
			ModbusTCPMaster modbusMaster = new ModbusTCPMaster(ipAddress, port);
			for (int i = 1; i < 20; i++) {
				assertTrue(modbusMaster.writeSingleRegister(0, i));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSetResetSingleCoil() {
		try {
			ModbusTCPMaster modbusMaster = new ModbusTCPMaster(ipAddress, port);
			for (int i = 1; i < 20; i++) {
				assertTrue(modbusMaster.setSingleCoil(i));
			}
			for (int i = 1; i < 20; i++) {
				assertTrue(modbusMaster.resetSingleCoil(i));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testForceMultipleCoils() {
		try {
			ModbusTCPMaster modbusMaster = new ModbusTCPMaster(ipAddress, port);
			for (int i = 1; i < 20; i++) {
				byte[] values = new byte[(i + 7) / 8];
				for (int j = 0; j < values.length; j++) {
					values[j] = (byte) (i);
				}
				assertTrue(modbusMaster.forceMultipleCoils(0, i, values));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testWriteMultileRegisters() {
		try {
			ModbusTCPMaster modbusMaster = new ModbusTCPMaster(ipAddress, port);
			for (int i = 1; i < 20; i++) {
				byte[] values = new byte[i * 2];
				for (int j = 0; j < i; j++) {
					values[j*2 + 1] = (byte) (j);
				}
				assertTrue(modbusMaster.writeMultipleRegisters(0, i, values));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testMaskWriteRegister() {
		try {
			ModbusTCPMaster modbusMaster = new ModbusTCPMaster(ipAddress, port);
			byte[] values = new byte[4];
			values[0] = 0;
			values[1] = 0;
			values[2] = 0;
			values[3] = 1;
			for (int i = 0; i < 20; i++) {
				values[1] *= 2;
				assertTrue(modbusMaster.maskWriteRegister(i, values));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
