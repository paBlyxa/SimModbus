package com.we.simModbus.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;

public class TagBool extends Tag{

	private IntegerProperty value;

	public TagBool(){
		value = new SimpleIntegerProperty();
	}
	
	@Override
	public int size() {
		return 1;
	}

	@Override
	public Number getValue() {
		return value.getValue();
	}

	@Override
	public ObservableValue<Number> getValueProperty() {
		return value;
	}

	@Override
	public void setValue(Number value) {
		this.value.set((int) value);
		
	}

}
