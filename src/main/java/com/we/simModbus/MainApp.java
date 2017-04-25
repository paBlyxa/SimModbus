package com.we.simModbus;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.we.modbus.ModbusTCPMaster;
import com.we.simModbus.model.Tag;
import com.we.simModbus.model.TagForm;
import com.we.simModbus.model.TagInt16;
import com.we.simModbus.model.Type;
import com.we.simModbus.view.CreateTagDialogController;
import com.we.simModbus.view.ModbusSlaveViewController;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;

	private ObservableList<Tag> tagList = FXCollections.observableArrayList();
	
	// Service
	Service<Void> masterService;
	

	/**
	 * Конструктор
	 */
	public MainApp() {
		// В качестве образца добавляем данные.
		Tag tag1 = new TagInt16();
		tag1.setAddress(1);
		tag1.setName("tag1");
		tag1.setType(Type.INT);
		tag1.setValue(5);
		
		Tag tag2 = new TagInt16();
		tag2.setAddress(2);
		tag2.setName("tag2");
		tag2.setType(Type.INT);
		tag2.setValue(0);
		
		tagList.addAll(tag1, tag2);
	}
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Modbus Simulator");

		initRootLayout();

		showModbusSlave();
	}

	/**
	 * Инициализирует корневой макет.
	 */
	public void initRootLayout() {
		try {
			// Загружаем корневой макет из fxml файла.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			// Отображаем сцену, содержащую корневой макет.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Показывает в корневом макете ModbusSlave.
	 */
	public void showModbusSlave() {
		try {
			// Загружаем сведения об адресатах.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/ModbusSlaveTCP.fxml"));
			BorderPane modbusSlaveView = (BorderPane) loader.load();

			// Помещаем вид в центр корневого макета.
			rootLayout.setCenter(modbusSlaveView);

			// Даем контроллеру доступ к главному приложению.
			ModbusSlaveViewController controller = loader.getController();
			controller.setMainApp(this);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Открывает диалоговое окно для изменения деталей указанной переменной. Если
	 * пользователь кликнул ОК, то изменения сохраняются в предоставленном
	 * объекте и возвращается значение true.
	 * @param <T>
	 * 
	 * @param tag
	 * 			- объект переменной, который надо изменить
	 * @return true, если пользователь кликнул ОК, в противном случае false.
	 */
	public boolean showCreateNewTagDialog(TagForm tag){
		try {
			// Загружаем fxml-файл и создаем новую сцену
			// для всплывающего диалогового окна.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/CreateTagDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			
			// Создаем диалоговое окно Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Edit Tag");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			
			// Передаем контроллер.
			CreateTagDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setTag(tag);
			
			// Отображаем диалоговое окно и ждем, пока пользователь его
			// не закроет
			dialogStage.showAndWait();
			
			return controller.isOkClicked();
			
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
	}
		
	/**
	 * Возвращает главную сцену.
	 * 
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Возвращает данные в виде наблюдаемого списка тэгов.
	 * 
	 * @return
	 */
	public ObservableList<Tag> getTagList() {
		return tagList;
	}
}
