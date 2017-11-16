package com.we.simModbus.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.we.modbus.ModbusTCPServer;
import com.we.modbus.tcp.StatusListener;
import com.we.simModbus.model.Tag;
import com.we.simModbus.service.TagScheduleService;
import com.we.simModbus.service.TagScheduleServiceThreadFactory;
import com.we.simModbus.service.TagScheduleServiceThread;
import com.we.simModbus.service.ChangeTagScheduleServiceThread;
import com.we.simModbus.service.TagDeleteHandler;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;

public class ModbusSlaveViewController extends ModbusViewController implements TagDeleteHandler {

	private final static Logger logger = LoggerFactory.getLogger(ModbusSlaveViewController.class);

	@FXML
	private TextField regCount;
	
	private Task<Void> taskConnect;
	
	private ModbusTCPServer modbusTCPServer;
	private final TagScheduleService changeTagService;
	
	public ModbusSlaveViewController() {
		changeTagService = new TagScheduleService();
		changeTagService.setTagScheduleServiceThreadFactory(new TagScheduleServiceThreadFactory(){
			@Override
			public TagScheduleServiceThread newInstance(){
				return new ChangeTagScheduleServiceThread(getDataModel(), getExecutor());
			}
		});
	}
	
	/**
	 * Инициализация класса-контроллера. Этот метод вызывается автоматически
	 * после того, как fxml-файл будет загружен.
	 */
	@Override
	public void initializeChild() {
		regCount.setText("1000");
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
		String slaveAddress = getSlaveAddress();
		if (slaveAddress == null || slaveAddress.isEmpty()) {
			setStatus("Введите Modbus адрес");
			return;
		}
		
		// Запрещаем изменение номера порта и количества регистров
		setEditable(false);
		regCount.setEditable(false);
				
		setConnected(true);
		// Создаем задачу для прослушивания новых подключений
		taskConnect = new Task<Void>() {
			@Override
			public Void call(){
				modbusTCPServer = new ModbusTCPServer(Byte.parseByte(slaveAddress), Integer.parseInt(strPort), 3, getDataModel(),
						new StatusListener() {
					@Override
					public void updateStatus(String status){
						updateMessage(status);
					}
				});
				updateMessage("Start server");
				modbusTCPServer.start();
				return null;
			}
		};
		bindStatus(taskConnect.messageProperty());
		taskConnect.setOnFailed((value) -> {
			logger.debug("Creation of modbus TCP slave failed");
			setConnected(false);
			regCount.setEditable(true);
		});
		taskConnect.setOnSucceeded((value) -> {
			logger.debug("Server has stopped");
			setConnected(false);
			regCount.setEditable(true);
		});

		submit(taskConnect);
	}

	/**
	 * Действие когда нажали кнопку "Отключение"
	 */
	@Override
	public void disconnect() {
		if (modbusTCPServer != null){
			modbusTCPServer.stop();
			unbindStatus();
		}
		if (taskConnect != null && taskConnect.isRunning()){
			taskConnect.cancel();
		}
		setConnected(false);
		regCount.setEditable(true);
		taskConnect = null;
	}

	/**
	 * Метод для создания контекстного меню на строке
	 * тэга в таблице.
	 */
	@Override
	public ContextMenu getRowContextMenu(TableRow<Tag> row) {
		logger.debug("Get context menu");
		return ContextMenuFactory.getContextMenuSlave(changeTagService, this, row);
	}
	
	/**
	 * Stop all threads.
	 */
	@Override
	public void stop(){
		logger.debug("Stop threads");
		changeTagService.stop();
		disconnect();
		super.stop();
	}
}
