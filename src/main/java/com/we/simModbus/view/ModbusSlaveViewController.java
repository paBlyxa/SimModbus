package com.we.simModbus.view;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ModbusSlaveViewController extends ModbusViewController {

	private final static Logger logger = LoggerFactory.getLogger(ModbusMasterViewController.class);

	@FXML
	private TextField regCount;
	
	private Task<Void> taskConnect;
	private final ExecutorService executor;
	
	public ModbusSlaveViewController() {
		executor = Executors.newSingleThreadExecutor();
	}
	
	/**
	 * Инициализация класса-контроллера. Этот метод вызывается автоматически
	 * после того, как fxml-файл будет загружен.
	 */
	@Override
	public void initializeChild() {
		regCount.setText("100");
	}
	
	/**
	 * Действие когда нажали кнопку "Подключение"
	 */
	@Override
	public void connect() {

		// Проверяем порт и количество регистров
		String strPort = getPort();
		if (strPort == null || strPort.isEmpty()) {
			setStatus("Введите номер порта");
			return;
		}
		String strCount = regCount.getText();
		if (strCount == null || strCount.isEmpty()) {
			setStatus("Введите количество регистров");
			return;
		}
		
		// Запрещаем изменение номера порта и количества регистров
		setEditable(false);
		
		// Создаем задачу для прослушивания новых подключений
		taskConnect = new Task<Void>() {
			@Override
			public Void call(){
				return null;
			}
		};
		bindStatus(taskConnect.messageProperty());
		taskConnect.setOnFailed((value) -> {
			logger.debug("Creation of modbus TCP slave failed");
			setConnected(false);
		});
		taskConnect.setOnSucceeded((value) -> {
			logger.debug("Creation of modbus TCP slave succeded");
			setConnected(true);
		});
		// Use the executor service to schedule the task
		executor.submit(taskConnect);
	}

	/**
	 * Действие когда нажали кнопку "Отключени"
	 */
	@Override
	public void disconnect() {
		unbindStatus();
		taskConnect.cancel();
		setConnected(false);
		taskConnect = null;
	}

}
