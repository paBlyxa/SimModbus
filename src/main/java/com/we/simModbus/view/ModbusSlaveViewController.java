package com.we.simModbus.view;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.we.modbus.ModbusTCPServer;
import com.we.modbus.model.ModbusDataModel;
import com.we.modbus.tcp.StatusListener;
import com.we.simModbus.model.Tag;
import com.we.simModbus.service.ChangeTagService;
import com.we.simModbus.service.TagChangeHandler;
import com.we.simModbus.service.TagDataModel;
import com.we.simModbus.service.TagDeleteHandler;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;

public class ModbusSlaveViewController extends ModbusViewController implements TagChangeHandler, TagDeleteHandler {

	private final static Logger logger = LoggerFactory.getLogger(ModbusMasterViewController.class);

	@FXML
	private TextField regCount;
	
	private Task<Void> taskConnect;
	private final ExecutorService executor;
	private final ScheduledExecutorService schExService;
	private ModbusTCPServer modbusTCPServer;
	private ChangeTagService[] changeTagService;
	
	public ModbusSlaveViewController() {
		executor = Executors.newSingleThreadExecutor();
		schExService = Executors.newScheduledThreadPool(1);
		changeTagService = new ChangeTagService[3];
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
		regCount.setEditable(false);
				
		setConnected(true);
		// Создаем задачу для прослушивания новых подключений
		taskConnect = new Task<Void>() {
			@Override
			public Void call(){
				modbusTCPServer = new ModbusTCPServer(Integer.parseInt(strPort), 3, getDataModel(),
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
		// Use the executor service to schedule the task
		executor.submit(taskConnect);
	}

	/**
	 * Действие когда нажали кнопку "Отключени"
	 */
	@Override
	public void disconnect() {
		modbusTCPServer.stop();
		unbindStatus();
		taskConnect.cancel();
		setConnected(false);
		regCount.setEditable(true);
		taskConnect = null;
	}

	@Override
	public ContextMenu getRowContextMenu(TableRow<Tag> row) {
		logger.debug("Get context menu");
		return ContextMenuFactory.getContextMenuSlave(this, this, row);
	}

	@Override
	public void addToCyclicChange(Tag tag, long delay, TimeUnit unit) {
		if (unit == TimeUnit.MILLISECONDS && delay == 500){
			if (changeTagService[0] == null){
				changeTagService[0] = new ChangeTagService(getDataModel());
				schExService.scheduleWithFixedDelay(changeTagService[0], delay, delay, unit);
			}
			changeTagService[0].add(tag);
		}
		
	}

	@Override
	public void removeFromCyclicChange(Tag tag) {
		for (ChangeTagService changeService : changeTagService){
			if (changeService != null){
				if (changeService.remove(tag)){
					if (changeService.size() < 1){
						changeService = null;
					}
					break;
				}
			}
		}
		
	}

	@Override
	public boolean isInCyclicChange(Tag tag) {
		for (ChangeTagService changeService : changeTagService){
			if (changeService != null){
				if (changeService.contains(tag)){
					return true;
				}
			}
		}
		return false;
	}

}
