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
		if (value instanceof Integer){
			this.value.set(Float.intBitsToFloat((int) value));
		} else {
			this.value.set((Float) value);
		}
	}

	@Override
	public ObservableValue<Number> getValueProperty() {
		return value;
	}
}
