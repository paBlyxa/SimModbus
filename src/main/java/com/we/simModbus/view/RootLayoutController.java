package com.we.simModbus.view;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.we.simModbus.MainApp;
import com.we.simModbus.model.ModbusWrapper;
import com.we.simModbus.model.Tag;
import com.we.simModbus.service.TagAddDeleteHandler;
import com.we.simModbus.service.TagImportExportService;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

/**
 * Контроллер для корневого макета. Корневой макет предоставляет базовый макет
 * приложения, содержащий строку меню и место, где будут размещены остальные
 * элементы JavaFX.
 * 
 * @author fakadey
 *
 */
public class RootLayoutController {

	@FXML
	private TabPane pane;

	// Ссылка на главное приложение
	private MainApp mainApp;
	private TagAddDeleteHandler tagAddDeleteHandler = null;

	/**
	 * Вызывается главным приложением, чтобы оставить ссылку на самого себя.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	/**
	 * Создает новую вкладку Modbus Master TCP
	 */
	@FXML
	private void handleNewMasterTCP() {
		createNewMaster("127.0.0.1", "502", null);
	}

	/**
	 * Создает новую вкладку Modbus Slave TCP
	 */
	@FXML
	private void handleNewSlaveTCP() {
		createNewSlave("502", null);
	}

	private void createNewMaster(String ipAddress, String port, List<Tag> tagList) {
		Tab tab = new Tab("MasterTCP");
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/ModbusMasterTCP.fxml"));
			BorderPane modbusMasterView = (BorderPane) loader.load();

			// Помещаем вид во вкладку
			tab.setContent(modbusMasterView);

			// Загружаем контроллер
			ModbusMasterViewController controller = loader.getController();
			controller.setMainApp(mainApp);
			controller.setIpAddress(ipAddress);
			controller.setPort(port);
			if (tagList != null) {
				controller.getTagList().clear();
				controller.getTagList().addAll(tagList);
			}

			if (pane.getTabs().isEmpty()) {
				tagAddDeleteHandler = controller;
			}
			pane.getTabs().add(tab);

			// Event when tab is selected
			tab.setOnSelectionChanged(new EventHandler<Event>() {
				@Override
				public void handle(Event t) {
					handleTabSelectionChanged(tab, controller);
					tagAddDeleteHandler = controller;
				}
			});

			// Event when tab is closed
			tab.setOnClosed(new EventHandler<Event>() {
				@Override
				public void handle(Event t) {
					controller.stop();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createNewSlave(String port, List<Tag> tagList) {
		Tab tab = new Tab("SlaveTCP");
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/ModbusSlaveTCP.fxml"));
			BorderPane modbusSlaveView = (BorderPane) loader.load();

			// Помещаем вид во вкладку
			tab.setContent(modbusSlaveView);

			// Загружаем контроллер
			ModbusSlaveViewController controller = loader.getController();
			controller.setMainApp(mainApp);
			controller.setPort(port);
			if (tagList != null) {
				controller.getTagList().clear();
				controller.getTagList().addAll(tagList);
			}

			if (pane.getTabs().isEmpty()) {
				tagAddDeleteHandler = controller;
			}
			pane.getTabs().add(tab);

			// Event when tab is selected
			tab.setOnSelectionChanged(new EventHandler<Event>() {
				@Override
				public void handle(Event t) {
					handleTabSelectionChanged(tab, controller);
					tagAddDeleteHandler = controller;
				}
			});

			// Event when tab is closed
			tab.setOnClosed(new EventHandler<Event>() {
				@Override
				public void handle(Event t) {
					controller.stop();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleTabSelectionChanged(Tab tab, ModbusViewController controller) {
		if (tab.isSelected()) {

		}
	}

	/**
	 * Создает новый тэг по команде в меню.
	 */
	@FXML
	private void handleNewTag() {
		if (tagAddDeleteHandler != null) {
			tagAddDeleteHandler.handleNewTag();
		}
	}

	/**
	 * Удаляет тэг по команде в меню.
	 */
	@FXML
	private void handleDeleteTag() {
		if (tagAddDeleteHandler != null) {
			tagAddDeleteHandler.handleDeleteTag();
		}
	}

	/**
	 * Открывает FileChooser, чтобы пользователь имел возможность выбрать файл
	 * для загрузки.
	 */
	@FXML
	private void handleOpen() {
		FileChooser fileChooser = new FileChooser();

		// Задаем фильтр расширения
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*,xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);

		// Показываем диалог загрузки файла
		File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

		if (file != null) {

			ModbusWrapper wrapper = mainApp.loadDataFromFile(file);
			if (wrapper != null) {
				if (wrapper.isMaster()) {
					createNewMaster(wrapper.getAddress(), wrapper.getPort(), wrapper.getTags());
				} else {
					createNewSlave(wrapper.getPort(), wrapper.getTags());
				}
			}
		}
	}

	/**
	 * Сохранить в файл по команде в меню.
	 */
	@FXML
	private void handleSave() {
		if (tagAddDeleteHandler != null) {
			File file = mainApp.getFilePath();
			if (file != null) {
				mainApp.saveDataToFile(file, tagAddDeleteHandler.getWrapper());
			} else {
				handleSaveAs();
			}
		}
	}

	/**
	 * Сохранить как в файл по команде в меню.
	 */
	@FXML
	private void handleSaveAs() {
		if (tagAddDeleteHandler != null) {
			FileChooser fileChooser = new FileChooser();

			// Задаем фильтр расширения
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*,xml)", "*.xml");
			fileChooser.getExtensionFilters().add(extFilter);

			// Показываем диалог загрузки файла
			File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

			if (file != null) {
				mainApp.saveDataToFile(file, tagAddDeleteHandler.getWrapper());
			}
		}
	}

	/**
	 * Импортировать тэги из файла по команде в меню.
	 */
	@FXML
	private void handleImportTags() {
		if (tagAddDeleteHandler != null) {
			FileChooser fileChooser = new FileChooser();

			// Задаем фильтр расширения
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*,csv)", "*.csv");
			fileChooser.getExtensionFilters().add(extFilter);

			// Показываем диалог загрузки файла
			File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

			if (file != null) {
				TagImportExportService.importTagsFromFile(file, tagAddDeleteHandler.getTagList());
			}
		}
	}

	/**
	 * Экспортировать тэги в файл по команде в меню.
	 */
	@FXML
	private void handleExportTags() {
		if (tagAddDeleteHandler != null) {
			FileChooser fileChooser = new FileChooser();

			// Задаем фильтр расширения
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*,csv)", "*.csv");
			fileChooser.getExtensionFilters().add(extFilter);

			// Показываем диалог выбора файла
			File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

			if (file != null) {
				TagImportExportService.exportTagsToFile(file, tagAddDeleteHandler.getTagList());
			}
		}

	}

	/**
	 * Закрывает приложение.
	 */
	@FXML
	private void handleExit() {
		Platform.exit();
	}
}
