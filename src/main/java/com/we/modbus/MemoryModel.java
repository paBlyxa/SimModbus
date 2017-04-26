package com.we.modbus;

public interface MemoryModel {

	public byte[] read(int reference, int sizeInBits, int type);
	
	public void write(int reference, int length, int sizeInBits, byte[] values);
}
