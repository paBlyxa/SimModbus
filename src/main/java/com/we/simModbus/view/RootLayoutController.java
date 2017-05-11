package com.we.simModbus.view;

import java.io.IOException;

import com.we.simModbus.MainApp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

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
			
			pane.getTabs().add(tab);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Создает новую вкладку Modbus Slave TCP
	 */
	@FXML
	private void handleNewSlaveTCP(){
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
			
			pane.getTabs().add(tab);
		} catch (IOException e) {
			e.printStackTrace();
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
