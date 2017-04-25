package com.we.simModbus.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TagForm {

	private StringProperty name;
	private ObjectProperty<Type> type;
	private IntegerProperty address;
	private StringProperty value;
	
	public TagForm() {
		name = new SimpleStringProperty();
		type = new SimpleObjectProperty<Type>();
		address = new SimpleIntegerProperty();
		value = new SimpleStringProperty();
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public StringProperty getNameProperty() {
		return name;
	}

	public Type getType() {
		return type.get();
	}

	public void setType(Type type) {
		this.type.set(type);
	}

	public ObjectProperty<Type> getTypeProperty() {
		return type;
	}

	public int getAddress() {
		return address.get();
	}

	public void setAddress(int address) {
		this.address.set(address);
	}

	public IntegerProperty getAddressProperty() {
		return address;
	}

	public String getValue() {
		return value.get();
	}

	public void setValue(String value) {
		this.value.set(value);
	}
	
	public StringProperty getValueProperty() {
		return value;
	}
}
