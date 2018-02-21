package com.we.simModbus.model;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;

@XmlType(name = "BoolType")
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
	@XmlTransient
	public Number getValue() {
		return value.getValue();
	}

	@Override
	@XmlTransient
	public ObservableValue<Number> getValueProperty() {
		return value;
	}

	@Override
	public void setValue(Number value) {
		this.value.set((Integer) value);
		
	}

}
