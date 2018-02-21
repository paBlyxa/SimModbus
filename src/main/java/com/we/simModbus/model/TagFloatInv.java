package com.we.simModbus.model;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.value.ObservableValue;

@XmlType(name = "FloatInvType")
public class TagFloatInv extends Tag {

	private FloatProperty value;
	
	public TagFloatInv(){
		value = new SimpleFloatProperty();
	}
	
	@Override
	public int size() {
		return 4;
	}

	@Override
	@XmlTransient
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
	@XmlTransient
	public ObservableValue<Number> getValueProperty() {
		return value;
	}

}
