package com.we.simModbus.view;

import com.we.modbus.ModbusTCPMaster;
import com.we.simModbus.model.Tag;
import com.we.simModbus.service.MasterTask;
import com.we.simModbus.service.ReadMultipleRegsScheduleService;
import com.we.simModbus.service.ReadMultipleRegsService;
import com.we.simModbus.service.TagScheduleService;
import com.we.simModbus.service.TagDeleteHandler;
import com.we.simModbus.service.TagRWHandler;
import com.we.simModbus.service.TagScheduleServiceThread;
import com.we.simModbus.service.TagScheduleServiceThreadFactory;
import com.we.simModbus.service.TransactionIdFactory;
import com.we.simModbus.service.TransactionIdFactoryImpl;

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
	private ModbusTCPMaster modbusTCPMaster = null;
	private ReadMultipleRegsService readService = null;
	private final TagScheduleService scheduledTagService;
	private final TransactionIdFactory transactionIdFactory;

	/**
	 * Конструктор. Конструктор вызывается раньше метода initialize().
	 */
	public ModbusMasterViewController() {
		transactionIdFactory = new TransactionIdFactoryImpl();
		scheduledTagService = new TagScheduleService();
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
		String slaveAddress = getSlaveAddress();
		if (slaveAddress == null || slaveAddress.isEmpty()) {
			setStatus("Введите Modbus адрес");
			return;
		}
		try {
			Integer.parseInt(slaveAddress);
		} catch (NumberFormatException e) {
			logger.warn("Wrong slaveAddress format - {}", slaveAddress);
			setStatus("Введите числовое значение в поле Modbus адрес");
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
			scheduledTagService.setTagScheduleServiceThreadFactory(new TagScheduleServiceThreadFactory() {
				@Override
				public TagScheduleServiceThread newInstance() {
					ReadMultipleRegsScheduleService readScheduleService = new ReadMultipleRegsScheduleService(
							Integer.parseInt(slaveAddress), modbusTCPMaster, getDataModel(), transactionIdFactory,
							getExecutor());
					readScheduleService.setOnFailed((value) -> {
						logger.warn("Schedule read registers was failed");
						disconnect();
					});
					readScheduleService.setMaximumFailureCount(1);
					return readScheduleService;
				}
			});
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
			scheduledTagService.reset();
			modbusTCPMaster.close();
			setStatus("Connection to the server is closed");
		} catch (IOException e) {
			logger.debug("An error occured when closing connection", e);
			setStatus("An error occured when closing connection");
		} finally {
			setConnected(false);
			modbusTCPMaster = null;
			logger.debug("modbusTCPMaster = null");
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
			readService = createNewReadService();
			readService.addAll(getTagList());
			submit(readService);
		}
	}

	@Override
	public void read(Tag tag) {
		if (modbusTCPMaster != null) {
			readService = createNewReadService();
			readService.add(tag);
			submit(readService);
		}

	}

	private ReadMultipleRegsService createNewReadService() {
		ReadMultipleRegsService newReadService = new ReadMultipleRegsService(Integer.parseInt(getSlaveAddress()),
				modbusTCPMaster, getDataModel(), transactionIdFactory, getExecutor());
		bindStatus(newReadService.messageProperty());
		newReadService.setOnFailed((value) -> {
			logger.debug("Read registers was failed");
			unbindStatus();
			disconnect();
		});
		newReadService.exceptionProperty().addListener((prop, oldValue, newValue) -> {
			if (newValue != null) {
				logger.debug(newValue.getMessage(), newValue);
			}
		});
		return newReadService;
	}

	@Override
	public void write(Tag tag) {
		// TODO Auto-generated method stub

	}

	@Override
	public ContextMenu getRowContextMenu(TableRow<Tag> row) {
		return ContextMenuFactory.getContextMenuMaster(this, scheduledTagService, this, row, isConnected);
	}

	@Override
	public void stop() {
		logger.debug("Stop threads");
		scheduledTagService.stop();
		if (masterService != null && masterService.isRunning()) {
			masterService.cancel();
		}
		if (readService != null && readService.isRunning()) {
			readService.cancel();
		}
		super.stop();
	}

}
