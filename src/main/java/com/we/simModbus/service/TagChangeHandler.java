package com.we.simModbus.service;

import java.util.concurrent.TimeUnit;

import com.we.simModbus.model.Tag;

public interface TagChangeHandler {

	/**
	 * Метод для добавления переменной в список переменных, 
	 * автоматически изменяющих свое значение с указанной
	 * дискретностью.
	 * 
	 * @param tag
	 * 			Переменная для добавления.
	 * @param delay
	 * 			Период изменения.
	 * @param unit
	 * 			Единица периода изменения.
	 */
	public void addToCyclicChange(Tag tag, long delay, TimeUnit unit);
	
	/**
	 * Метод для удаления переменной из списка переменных,
	 * автоматически изменяющих свое значение.
	 * @param tag
	 * 			Переменная для удаления.
	 */
	public void removeFromCyclicChange(Tag tag);
	
	/**
	 * Возвращает true если переменная добавлена в список переменных,
	 * автоматически изменяющих свое значение.
	 * @param tag
	 * 			Переменная для проверки.
	 * @return
	 */
	public boolean isInCyclicChange(Tag tag);
}
