package com.we.simModbus.model;

import javafx.beans.value.ObservableValue;

public abstract class Tag {

	private String name;
	private Type type;
	private int address;
	
	public Tag() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public int getAddress() {
		return address;
	}

	public void setAddress(int address) {
		this.address = address;
	}

	public abstract int size();
	
	public abstract Number getValue();
	
	public abstract ObservableValue<Number> getValueProperty();
	
	public abstract void setValue(Number value);
}
