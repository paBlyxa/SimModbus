package com.we.simModbus.model;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.value.ObservableValue;

public class TagFloat extends Tag {
	
	private FloatProperty value;
	
	public TagFloat(){
		value = new SimpleFloatProperty();
	}
	
	@Override
	public int size() {
		return 4;
	}

	@Override
	public Number getValue() {
		return value.getValue();
	}

	@Override
	public void setValue(Number value) {
		this.value.set((float) value);
	}

	@Override
	public ObservableValue<Number> getValueProperty() {
		return value;
	}
}
