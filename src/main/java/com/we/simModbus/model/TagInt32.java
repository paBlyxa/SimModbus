package com.we.simModbus.model;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;

@XmlType(name = "Int32Type")
public class TagInt32 extends Tag {
	
	private IntegerProperty value;
	
	public TagInt32(){
		value = new SimpleIntegerProperty();
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
		this.value.set((int) value);
	}

	@Override
	@XmlTransient
	public ObservableValue<Number> getValueProperty() {
		// TODO Auto-generated method stub
		return value;
	}

}
