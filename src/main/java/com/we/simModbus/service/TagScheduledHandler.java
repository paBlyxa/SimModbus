package com.we.simModbus.service;

import com.we.simModbus.model.Tag;
import com.we.simModbus.model.TimeCyclic;

public interface TagScheduledHandler {

	/**
	 * Метод для добавления переменной в список переменных
	 * 
	 * @param tag
	 * 			Переменная для добавления.
	 * @param TimeCyсlic
	 * 			Период изменения.
	 */
	public void addToSchedule(Tag tag, TimeCyclic unit);
	
	/**
	 * Метод для удаления переменной из списка переменных
	 * @param tag
	 * 			Переменная для удаления.
	 */
	public void removeFromSchedule(Tag tag);
	
	/**
	 * Возвращает true если переменная добавлена в список переменных
	 * @param tag
	 * 			Переменная для проверки.
	 * @return
	 */
	public boolean isInSchedule(Tag tag);
}
