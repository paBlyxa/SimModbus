package com.we.simModbus.view;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.we.simModbus.MainApp;
import com.we.simModbus.model.Tag;
import com.we.simModbus.model.TagBool;
import com.we.simModbus.model.TagFloat;
import com.we.simModbus.model.TagFloatInv;
import com.we.simModbus.model.TagForm;
import com.we.simModbus.model.TagInt16;
import com.we.simModbus.model.TagInt32;
import com.we.simModbus.model.Type;
import com.we.simModbus.service.StopThreadsHandler;
import com.we.simModbus.service.TagDataModel;
import com.we.simModbus.service.TagAddDeleteHandler;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

public abstract class ModbusViewController implements StopThreadsHandler, TagAddDeleteHandler {

	private final static Logger logger = LoggerFactory.getLogger(ModbusViewController.class);
	
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
	private TextField port;
	@FXML
	private TextField slaveAddress;
	@FXML
	private Label status;
	@FXML
	private Button butConnect;
	@FXML
	private BorderPane pane;
	@FXML
	private MenuItem openMenuItem;
	
	// Ссылка на главное приложение
	private MainApp mainApp;

	protected final BooleanProperty isConnected;
	private final TagDataModel dataModel;
	private final ExecutorService executor;
	
	public ModbusViewController() {
		executor = Executors.newFixedThreadPool(2);
		isConnected = new SimpleBooleanProperty(false);

		dataModel = new TagDataModel(20000);
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
		valueColumn.setCellFactory(col -> new IntegerEditingCell(dataModel));
		
		status.setText("");
		slaveAddress.setText("1");
		registerTable.setEditable(true);
		registerTable.setRowFactory(new Callback<TableView<Tag>, TableRow<Tag>>() {
			@Override
			public TableRow<Tag> call(TableView<Tag> tableView) {
				final TableRow<Tag> row = new TableRow<>();
				final ContextMenu rowMenu = getRowContextMenu(row);

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

		// Добавление в таблицу данных из наблюдаемого списка
		registerTable.setItems(dataModel.getTagList());
		initializeChild();
	}

	/**
	 * Метод вызывается при добавлении новой переменной.
	 */
	@FXML
	public void handleNewTag() {
		TagForm tagForm = new TagForm();
		tagForm.setType(Type.INT);
		tagForm.setValue("0");
		boolean okClicked = mainApp.showCreateNewTagDialog(tagForm);
		Tag tag = null;
		if (okClicked) {

			int countTags = tagForm.getCount();

			for (int i = 0; i < countTags; i++) {

				switch (tagForm.getType()) {
				case BOOL:
					tag = new TagBool();
					tag.setValue(Integer.parseInt(tagForm.getValue()));
					tag.setAddress(tagForm.getAddress() + i + i * tagForm.getOffset());
					break;
				case DINT:
					tag = new TagInt32();
					tag.setValue(Integer.parseInt(tagForm.getValue()));
					tag.setAddress(tagForm.getAddress() + (i << 1) + i * tagForm.getOffset());
					break;
				case FLOAT:
					tag = new TagFloat();
					tag.setValue(Float.parseFloat(tagForm.getValue()));
					tag.setAddress(tagForm.getAddress() + (i << 1) + i * tagForm.getOffset());
					break;
				case FLOATINV:
					tag = new TagFloatInv();
					tag.setValue(Float.parseFloat(tagForm.getValue()));
					tag.setAddress(tagForm.getAddress() + (i << 1) + i * tagForm.getOffset());
					break;
				case INT:
					tag = new TagInt16();
					tag.setValue(Integer.parseInt(tagForm.getValue()));
					tag.setAddress(tagForm.getAddress() + i + i * tagForm.getOffset());
					break;
				default:
					break;

				}

				if (countTags > 1) {
					tag.setName(tagForm.getName() + (i+1));
				} else {
					tag.setName(tagForm.getName());
				}
				tag.setType(tagForm.getType());
				dataModel.addTag(tag);
			}
		}
	}

	/**
	 * Метод вызывается при удалении переменной.
	 */
	@FXML
	public void handleDeleteTag() {
		int selectedIndex = registerTable.getSelectionModel().getSelectedIndex();
		if (selectedIndex >= 0) {
			Tag tag = registerTable.getItems().remove(selectedIndex);
			deleteTag(tag);
		} else {
			// Ничего не выбрано.
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("No Selection");
			alert.setHeaderText("No Person Selected");
			alert.setContentText("Please select a person in the table.");
			
			alert.showAndWait();
		}
	}
	

	/**
	 * Открывает FileChooser, чтобы пользователь имел возможность
	 * выбрать файл для загрузки.
	 */
	void handleOpen(){
		
	}
	
	/**
	 * Метод для добавления переменной.
	 * @param tag
	 */
	public void addTag(Tag tag){
		dataModel.addTag(tag);
	}
	
	/**
	 * Метод для удаления переменной.
	 * @param tag
	 */
	public void deleteTag(Tag tag){
		dataModel.deleteTag(tag);
	}
	
	/**
	 * Метод вызывается когда нажали кнопку "Подключить".
	 */
	@FXML
	private void handleConnect() {
		connect();
	}

	/**
	 * Метод вызывается когда нажали кнопку "Отключить".
	 */
	@FXML
	private void handleDisconnect() {
		disconnect();
	}

	/**
	 * Вызывается главным приложением, которое дает на себя ссылку
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainAp) {
		this.mainApp = mainAp;
		mainApp.addStopThreadsHandler(this);
	}

	/**
	 * Возвращает номер порта из текстого поля.
	 * 
	 * @return
	 */
	public String getPort() {
		return port.getText();
	}

	/**
	 * Возвращает Modbus адрес.
	 * 
	 * @return
	 */
	public String getSlaveAddress() {
		return slaveAddress.getText();
	}

	/**
	 * Задание текста в поле статус
	 * 
	 * @param text
	 */
	public void setStatus(String text) {
		status.setText(text);
	}

	/**
	 * Bind status field
	 */
	public void bindStatus(ObservableValue<? extends String> observable) {
		status.textProperty().bind(observable);
	}

	/**
	 * Unbind status field
	 */
	public void unbindStatus() {
		status.textProperty().unbind();
	}

	/**
	 * Отключение/включение возможности изменения текстовых полей
	 */
	public void setEditable(boolean editable) {
		port.setEditable(editable);
	}

	/**
	 * Возвращает наблюдаемый список тэгов
	 * 
	 * @return
	 */
	public ObservableList<Tag> getTagList() {
		return dataModel.getTagList();
	}

	/**
	 * Возвращает модель памяти
	 */
	protected TagDataModel getDataModel(){
		return dataModel;
	}
	
	/**
	 * Задает состояние подключения
	 * 
	 * @param connected
	 */
	public void setConnected(boolean connected) {
		unbindStatus();
		if (connected) {
			isConnected.set(true);
			butConnect.setText("Отключение");
			butConnect.setOnAction((actionEvent) -> handleDisconnect());
			port.setEditable(false);
			slaveAddress.setEditable(false);
		} else {
			isConnected.set(false);
			butConnect.setText("Подключение");
			butConnect.setOnAction((event) -> handleConnect());
			port.setEditable(true);
			slaveAddress.setEditable(true);
		}
	}

	/**
	 * Задает меню слева
	 * 
	 * @param Node
	 *            value
	 */
	public void setLeft(Node value) {
		pane.setLeft(value);
	}

	/**
	 * Подключение (создание соединения)
	 * 
	 */
	public abstract void connect();

	/**
	 * Отключение (разрыв соединения)
	 * 
	 */
	public abstract void disconnect();
	
	/**
	 * Возвращает контекстное меню для строки в таблице.
	 * @return
	 */
	public abstract ContextMenu getRowContextMenu(TableRow<Tag> row);
	
	/**
	 * Метод для инициализации.
	 * Должен быть реализован в классе наследнике.
	 */
	public void initializeChild(){
	}
	
	/**
	 * Метод для выполнения задачи в другом потоке.
	 * Используется ExecutorService.
	 */
	void submit(Task<Void> task){
		// Use the executor service to schedule the task
		executor.submit(task);
	}
	
	/**
	 * Метод для выполнения задачи в другом потоке.
	 * Используется ExecutorService.
	 */
	void submit(Service<Void> service){
		// Use the executor service to schedule the task
		service.start();
	}	
	
	Executor getExecutor(){
		return executor;
	}
	
	@Override
	public void stop(){
		logger.debug("Stop execution of tasks");
		executor.shutdownNow();
	}
	
	public void setPort(String port){
		this.port.setText(port);
	}
}
