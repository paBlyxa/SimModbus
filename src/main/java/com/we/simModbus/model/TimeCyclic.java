package com.we.simModbus.model;

import java.util.concurrent.TimeUnit;

public enum TimeCyclic {
	_500MS(500, TimeUnit.MILLISECONDS),
	_1S(1000, TimeUnit.MILLISECONDS),
	_5S(5000, TimeUnit.MILLISECONDS);
	
	TimeCyclic(long delay, TimeUnit unit){
		this.delay = delay;
		this.unit = unit;
	}
	
	private final long delay;
	private final TimeUnit unit;
	
	public long getDelay(){
		return delay;
	}
	
	public TimeUnit getTimeUnit(){
		return unit;
	}
}
