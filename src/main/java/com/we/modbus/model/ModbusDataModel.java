package com.we.modbus.model;

public interface ModbusDataModel {

	/**
	 * Чтение массива байт из памяти по указанному адресу.
	 * @param reference Адрес данных
	 * @param length Количество данных
	 * @param table тип данных
	 * @return Возвращает массив байт если чтение успешно, иначе пустой массив
	 */
	public byte[] read(int reference, int length, DataModelTable table);
	
	/**
	 * Запись массива байт в память по указанному адресу.
	 * @param reference Адрес данных
	 * @param length Количество данных
	 * @param table тип данных
	 * @param values Массив байт значений для записи
	 * @return Возвращает true, если запись успешна
	 */
	public boolean write(int reference, int length, DataModelTable table, byte[] values);
	
	/**
	 * Возвращает количество регистров в памяти.
	 * @return
	 */
	public int getRegisterCount();
}
