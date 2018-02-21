package com.we.simModbus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.we.simModbus.model.Tag;
import com.we.simModbus.model.TagBool;
import com.we.simModbus.model.TagFloat;
import com.we.simModbus.model.TagFloatInv;
import com.we.simModbus.model.TagForm;
import com.we.simModbus.model.TagInt16;
import com.we.simModbus.model.TagInt32;
import com.we.simModbus.model.ModbusWrapper;
import com.we.simModbus.service.StopThreadsHandler;
import com.we.simModbus.view.CreateTagDialogController;
import com.we.simModbus.view.RootLayoutController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainApp extends Application {

	private final static Logger logger = LoggerFactory.getLogger(MainApp.class);
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	private final List<StopThreadsHandler> stopThreadsHandlers;
	
	/**
	 * Конструктор
	 */
	public MainApp() {
		stopThreadsHandlers = new ArrayList<StopThreadsHandler>();
	}
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Modbus Simulator");

		initRootLayout();
	}

	@Override
	public void stop(){
		// TODO stop all threads
		for (StopThreadsHandler handler : stopThreadsHandlers){
			handler.stop();
		}
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
			
			// Даем контроллеру доступ к главному приложению.
			RootLayoutController controller = loader.getController();
			controller.setMainApp(this);
						
			primaryStage.show();
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

	/**
	 * Add stopThreadsHandler to list.
	 * @param handler
	 */
	public void addStopThreadsHandler(StopThreadsHandler handler){
		stopThreadsHandlers.add(handler);
	}
	
	/**
	 * Возвращает preference файла адресатов, то есть, последний открытый файл.
	 * Этот preference считывается из реестра, специфичного для конкретной
	 * операционной системы. Если preference не был найден, то возвращается null.
	 * 
	 * @return
	 */
	public File getFilePath(){
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
		String filePath = prefs.get("filePath", null);
		logger.debug("Get file path '{}'", filePath);
		if (filePath != null) {
			return new File(filePath);
		} else {
			return null;
		}
	}
	
	/**
	 * Задаёт путь текущему загруженному файлу. Этот путь сохраняется
	 * в реестре, специфичном для конкретной операционной системы.
	 * 
	 * @param file - файл или null, чтобы удалить путь
	 */
	public void setFilePath(File file){
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
		if (file != null){
			prefs.put("filePath", file.getPath());
			logger.debug("Set file path '{}'", file.getPath());
		} else {
			prefs.remove("filePath");
		}
	}
	
	/**
	 * Загружает информацию о переменных из указанного файла.
	 * Текущая информация о переменных будет заменена.
	 * 
	 * @param file
	 * @param tagList
	 */
	public ModbusWrapper loadDataFromFile(File file){
		try{
			logger.debug("Load data from file '{}'", file);
			JAXBContext context = JAXBContext.newInstance(ModbusWrapper.class, Tag.class, TagBool.class, TagFloat.class, TagFloatInv.class, TagInt16.class, TagInt32.class);
			Unmarshaller um = context.createUnmarshaller();
			
			// Чтение XML из файла и демаршализация.
			ModbusWrapper wrapper = (ModbusWrapper) um.unmarshal(file);
						
			// Сохраняем путь к файлу в реестре.
			setFilePath(file);
			return wrapper;
		} catch (Exception e){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Could not load data");
			alert.setContentText("Could not load data from file:\n" + file.getPath());
			System.out.println(e);
			alert.showAndWait();
		}
		return null;
	}
	
	/**
	 * Сохраняет информацию о переменных в указанном файле.
	 * 
	 * @param file
	 * @param tagList
	 */
	public void saveDataToFile(File file, ModbusWrapper wrapper){
		try{
			logger.debug("Save data to file '{}'", file);
			JAXBContext context = JAXBContext.newInstance(ModbusWrapper.class, Tag.class, TagBool.class, TagFloat.class, TagFloatInv.class, TagInt16.class, TagInt32.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
						
			// Маршиллируем и сохраняем XML в файл.
			m.marshal(wrapper, file);
			
			// Сохраняем путь к файлу в реестре.
			setFilePath(file);
		} catch (Exception e){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Could not save data");
			alert.setContentText("Could not save data to file:\n" + file.getPath());
			System.out.println(e);
			alert.showAndWait();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
