package com.we.simModbus.service;

import java.util.concurrent.atomic.AtomicInteger;

public class TransactionIdFactoryImpl implements TransactionIdFactory{

	private AtomicInteger transId = new AtomicInteger(0);
	
	@Override
	public int getId() {
		return transId.getAndIncrement();
	}

}
