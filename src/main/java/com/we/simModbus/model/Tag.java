package com.we.simModbus.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import javafx.beans.value.ObservableValue;

/**
 * Абстрактный класс переменной
 * 
 * @author fakadey
 *
 */
@XmlTransient
public abstract class Tag {

	/**
	 * Название переменной
	 */
	private String name;
	/**
	 * Тип переменной
	 */
	private Type type;
	/**
	 * Адрес переменной
	 */
	private int address;
	
	public Tag() {
	}

	/**
	 * Возвращает название переменной
	 * 
	 * @return название переменной
	 */
	@XmlElement
	public String getName() {
		return name;
	}

	/**
	 * Задает название переменной
	 * @param name название переменной
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Возвращает тип переменной
	 * @return тип переменной
	 */
	@XmlElement
	public Type getType() {
		return type;
	}

	/**
	 * Задает тип переменной
	 * @param type тип переменной
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * Возвращает адрес переменной
	 * @return адрес переменной
	 */
	@XmlElement
	public int getAddress() {
		return address;
	}

	/**
	 * Задает адрес переменной
	 * @param address адрес переменной
	 */
	public void setAddress(int address) {
		this.address = address;
	}

	/**
	 * Возвращает размер переменной в памяти в байтах
	 * @return количество байт
	 */
	public abstract int size();
	
	/**
	 * Возвращает значение переменной
	 * @return значение переменной
	 */
	public abstract Number getValue();
	
	/**
	 * Возвращает наблюдаемое значение переменной
	 * @return
	 */
	public abstract ObservableValue<Number> getValueProperty();
	
	/**
	 * Задает значение переменной
	 * @param value новое значение
	 */
	public abstract void setValue(Number value);
}
