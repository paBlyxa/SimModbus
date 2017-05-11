package com.we.simModbus.view;

import com.we.modbus.ModbusTCPMaster;
import com.we.simModbus.model.Tag;
import com.we.simModbus.service.MasterTask;
import com.we.simModbus.service.ReadMultipleRegsService;
import com.we.simModbus.service.TagDeleteHandler;
import com.we.simModbus.service.TagRWHandler;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModbusMasterViewController extends ModbusViewController implements TagRWHandler, TagDeleteHandler {

	private final static Logger logger = LoggerFactory.getLogger(ModbusMasterViewController.class);

	@FXML
	private TextField ipAddress;
	
	private Service<ModbusTCPMaster> masterService;
	private ReadMultipleRegsService readService = null;
	private ModbusTCPMaster modbusTCPMaster = null;
	
	/**
	 * Конструктор. Конструктор вызывается раньше метода initialize().
	 */
	public ModbusMasterViewController() {
		
	}

	/**
	 * Инициализация класса-контроллера. Этот метод вызывается автоматически
	 * после того, как fxml-файл будет загружен.
	 */
	@Override
	public void initializeChild() {
		ipAddress.setText("127.0.0.1");
	}
	
	/**
	 * Действие когда нажали кнопку "Подключение"
	 */
	@Override
	public void connect() {

		// Проверяем ip address, порт и количество регистров
		String addr = ipAddress.getText();
		if (addr == null || addr.isEmpty()) {
			setStatus("Введите Ip адрес");
			return;
		}
		String strPort = getPort();
		if (strPort == null || strPort.isEmpty()) {
			setStatus("Введите номер порта");
			return;
		}
		
		// Запрещаем изменение ip адреса и номера порта
		setEditable(false);
		ipAddress.setEditable(false);
		
		// Создаем сервис для установления связи в отдельном потоке
		masterService = new Service<ModbusTCPMaster>() {
			@Override
			protected Task<ModbusTCPMaster> createTask() {
				return new MasterTask(ipAddress.getText(), Integer.parseInt(getPort()));
			}
		};
		bindStatus(masterService.messageProperty());
		masterService.setOnFailed((value) -> {
			logger.debug("Creation of modbus TCP master failed");
			setConnected(false);
		});
		masterService.setOnSucceeded((value) -> {
			logger.debug("Creation of modbus TCP master succeded");
			modbusTCPMaster = masterService.getValue();
			setConnected(true);
			Button butReadOnce = new Button("Read once");
			butReadOnce.setOnAction((val) -> handleReadOnce());
			VBox buttonBox = new VBox(butReadOnce);
			buttonBox.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 1;");
			setLeft(buttonBox);
		});
		masterService.start();
	}

	/**
	 * Действие когда нажали кнопку "Отключение"
	 */
	@Override
	public void disconnect() {
		try {
			unbindStatus();
			modbusTCPMaster.close();
			setStatus("Connection to the server is closed");
		} catch (IOException e) {
			logger.debug("An error occured when closing connection", e);
			setStatus("An error occured when closing connection");
		} finally {
			setConnected(false);
			modbusTCPMaster = null;
			masterService = null;
			readService = null;
			setLeft(null);
		}
	}

	/**
	 * Действие когда нажали кнопки "Read once"
	 */
	@FXML
	private void handleReadOnce() {
		if (modbusTCPMaster != null) {
			if (readService == null) {
				readService = new ReadMultipleRegsService(modbusTCPMaster, getTagList());
				readService.setReadAll();
				bindStatus(readService.messageProperty());
				readService.setOnFailed((value) -> {
					logger.debug("Read registers was failed");
					unbindStatus();
					disconnect();
				});
				readService.exceptionProperty().addListener((prop, oldValue, newValue) -> {
					if (newValue != null) {
						logger.debug(newValue.getMessage(), newValue);
					}
				});
				readService.start();
			} else {
				readService.setReadAll();
				readService.restart();
			}
		}
	}

	@Override
	public void read(Tag tag) {
		if (modbusTCPMaster != null) {
			if (readService == null) {
				readService = new ReadMultipleRegsService(modbusTCPMaster, getTagList());
				readService.setReadTag(tag);
				bindStatus(readService.messageProperty());
				readService.setOnFailed((value) -> {
					logger.debug("Read registers was failed");
					unbindStatus();
					disconnect();
				});
				readService.exceptionProperty().addListener((prop, oldValue, newValue) -> {
					if (newValue != null) {
						logger.debug(newValue.getMessage(), newValue);
					}
				});
				readService.start();
			} else {
				// TODO
				if (!readService.isRunning()) {
					readService.setReadTag(tag);
					readService.restart();
				}
			}
		}

	}

	@Override
	public void write(Tag tag) {
		// TODO Auto-generated method stub

	}

	@Override
	public ContextMenu getRowContextMenu(TableRow<Tag> row) {
		return ContextMenuFactory.getContextMenuMaster(this, this, row, isConnected);
	}

}
