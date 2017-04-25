package com.we.simModbus.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;

public class TagInt32 extends Tag {
	
	private IntegerProperty value;
	
	public TagInt32(){
		value = new SimpleIntegerProperty();
	}
	
	@Override
	public int size() {
		return 2;
	}

	@Override
	public Number getValue() {
		return value.getValue();
	}

	@Override
	public void setValue(Number value) {
		this.value.set((int) value);
	}

	@Override
	public ObservableValue<Number> getValueProperty() {
		// TODO Auto-generated method stub
		return value;
	}

}
