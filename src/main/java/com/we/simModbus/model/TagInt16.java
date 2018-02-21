package com.we.simModbus.model;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;

@XmlType(name = "Int16Type")
public class TagInt16 extends Tag{

	private IntegerProperty value;
	
	public TagInt16(){
		value = new SimpleIntegerProperty();
	}
	
	@Override
	public int size() {
		return 2;
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
		return value;
	}

}
