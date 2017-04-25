package com.we.simModbus.view;

import com.we.modbus.ModbusTCPMaster;
import com.we.simModbus.MainApp;
import com.we.simModbus.MasterTask;
import com.we.simModbus.ReadMultipleRegsService;
import com.we.simModbus.model.Tag;
import com.we.simModbus.model.TagBool;
import com.we.simModbus.model.TagFloat;
import com.we.simModbus.model.TagForm;
import com.we.simModbus.model.TagInt16;
import com.we.simModbus.model.TagInt32;
import com.we.simModbus.model.Type;
import com.we.simModbus.service.TagHandler;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModbusSlaveViewController implements TagHandler {

	private final static Logger logger = LoggerFactory.getLogger(ModbusSlaveViewController.class);

	@FXML
	private TableView<Tag> registerTable;
	@FXML
	private TableColumn<Tag, String> nameColumn;
	@FXML
	private TableColumn<Tag, Type> typeColumn;
	@FXML
	private TableColumn<Tag, Integer> addressColumn;
	@FXML
	private TableColumn<Tag, Number> valueColumn;
	@FXML
	private TextField ipAddress;
	@FXML
	private TextField port;
	@FXML
	private TextField regCount;
	@FXML
	private Label status;
	@FXML
	private Button butConnect;
	@FXML
	private BorderPane pane;

	// Ссылка на главное приложение
	private MainApp mainApp;
	// Ссылка на service
	private Service<ModbusTCPMaster> masterService;
	private ReadMultipleRegsService readService = null;
	private ModbusTCPMaster modbusTCPMaster = null;
	private final BooleanProperty isConnected;
	private final TagHandler tagHandler;

	/**
	 * Конструктор. Конструктор вызывается раньше метода initialize().
	 */
	public ModbusSlaveViewController() {
		tagHandler = this;
		isConnected = new SimpleBooleanProperty(false);
	}

	/**
	 * Инициализация класса-контроллера. Этот метод вызывается автоматически
	 * после того, как fxml-файл будет загружен.
	 */
	@FXML
	private void initialize() {
		// Инициализация таблицы тэгов.
		nameColumn.setCellValueFactory(new PropertyValueFactory<Tag, String>("name"));
		typeColumn.setCellValueFactory(new PropertyValueFactory<Tag, Type>("type"));
		addressColumn.setCellValueFactory(new PropertyValueFactory<Tag, Integer>("address"));
		valueColumn.setCellValueFactory(cellData -> cellData.getValue().getValueProperty());
		status.setText("");
		ipAddress.setText("127.0.0.1");
		port.setText("502");
		regCount.setText("100");
		registerTable.setEditable(true);
		registerTable.setRowFactory(new Callback<TableView<Tag>, TableRow<Tag>>() {
			@Override
			public TableRow<Tag> call(TableView<Tag> tableView) {
				final TableRow<Tag> row = new TableRow<>();
				final ContextMenu rowMenu = new ContextMenuTag(tagHandler, row, isConnected);

				// "Borrow" menu items from table's context menu,
				// if there is one.
				final ContextMenu tableMenu = tableView.getContextMenu();
				if (tableMenu != null) {
					rowMenu.getItems().add(new SeparatorMenuItem());
					rowMenu.getItems().addAll(tableMenu.getItems());
				}
				return row;
			}
		});

	}

	/**
	 * Вызывается главным приложением, которое дает на себя ссылку
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainAp) {
		this.mainApp = mainAp;

		// Добавление в таблицу данных из наблюдаемого списка
		registerTable.setItems(mainApp.getTagList());
	}

	@FXML
	private void handleNewTag() {
		TagForm tagForm = new TagForm();
		tagForm.setType(Type.INT);
		tagForm.setValue("0");
		boolean okClicked = mainApp.showCreateNewTagDialog(tagForm);
		Tag tag = null;
		if (okClicked) {
			switch (tagForm.getType()) {
			case BOOL:
				tag = new TagBool();
				tag.setValue(Integer.parseInt(tagForm.getValue()));
				break;
			case DINT:
				tag = new TagInt32();
				tag.setValue(Integer.parseInt(tagForm.getValue()));
				break;
			case FLOAT:
				tag = new TagFloat();
				tag.setValue(Float.parseFloat(tagForm.getValue()));
				break;
			case INT:
				tag = new TagInt16();
				tag.setValue(Integer.parseInt(tagForm.getValue()));
				break;
			default:
				break;

			}

			tag.setName(tagForm.getName());
			tag.setType(tagForm.getType());
			tag.setAddress(tagForm.getAddress());
			mainApp.getTagList().add(tag);
		}
	}

	/**
	 * Действие когда нажали кнопку "Подключение"
	 */
	@FXML
	private void handleConnect() {

		// Проверяем ip address, порт и количество регистров
		String addr = ipAddress.getText();
		if (addr == null || addr.isEmpty()) {
			status.setText("Введите Ip адрес");
			return;
		}
		String strPort = port.getText();
		if (strPort == null || strPort.isEmpty()) {
			status.setText("Введите номер порта");
			return;
		}
		String strCount = regCount.getText();
		if (strCount == null || strCount.isEmpty()) {
			status.setText("Введите количество регистров");
			return;
		}

		ipAddress.setEditable(false);
		port.setEditable(false);
		regCount.setEditable(false);
		masterService = new Service<ModbusTCPMaster>() {
			@Override
			protected Task<ModbusTCPMaster> createTask() {
				return new MasterTask(ipAddress.getText(), Integer.parseInt(port.getText()));
			}
		};
		status.textProperty().bind(masterService.messageProperty());
		masterService.setOnFailed((value) -> {
			logger.debug("Creation of modbus TCP master failed");
			status.textProperty().unbind();
			ipAddress.setEditable(true);
			port.setEditable(true);
			regCount.setEditable(true);
		});
		masterService.setOnSucceeded((value) -> {
			logger.debug("Creation of modbus TCP master succeded");
			modbusTCPMaster = masterService.getValue();
			status.textProperty().unbind();
			butConnect.setText("Отключение");
			butConnect.setOnAction((actionEvent) -> handleDisconnect());
			Button butReadOnce = new Button("Read once");
			butReadOnce.setOnAction((val) -> handleReadOnce());
			VBox buttonBox = new VBox(butReadOnce);
			buttonBox.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 1;");
			pane.setLeft(buttonBox);
			isConnected.set(true);
		});
		masterService.start();
	}

	/**
	 * Действие когда нажали кнопку "Отключение"
	 */
	private void handleDisconnect() {
		try {
			status.textProperty().unbind();
			modbusTCPMaster.close();
			status.setText("Connection to the server is closed");
		} catch (IOException e) {
			logger.debug("An error occured when closing connection", e);
			status.setText("An error occured when closing connection");
		} finally {
			ipAddress.setEditable(true);
			port.setEditable(true);
			regCount.setEditable(true);
			butConnect.setText("Подключение");
			butConnect.setOnAction((event) -> handleConnect());
			modbusTCPMaster = null;
			masterService = null;
			readService = null;
			pane.setLeft(null);
			isConnected.set(false);
		}
	}

	/**
	 * Действие когда нажали кнопки "Read once"
	 */
	@FXML
	private void handleReadOnce() {
		if (modbusTCPMaster != null) {
			if (readService == null) {
				readService = new ReadMultipleRegsService(modbusTCPMaster, mainApp.getTagList());
				readService.setReadAll();
				status.textProperty().bind(readService.messageProperty());
				readService.setOnFailed((value) -> {
					logger.debug("Read registers was failed");
					status.textProperty().unbind();
					handleDisconnect();
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
				readService = new ReadMultipleRegsService(modbusTCPMaster, mainApp.getTagList());
				readService.setReadTag(tag);
				status.textProperty().bind(readService.messageProperty());
				readService.setOnFailed((value) -> {
					logger.debug("Read registers was failed");
					status.textProperty().unbind();
					handleDisconnect();
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
	public void delete(Tag tag) {
		// TODO Auto-generated method stub

	}
}
