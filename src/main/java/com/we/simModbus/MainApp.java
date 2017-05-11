package com.we.simModbus;

import java.io.IOException;

import com.we.simModbus.model.TagForm;
import com.we.simModbus.view.CreateTagDialogController;
import com.we.simModbus.view.RootLayoutController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;
	
	/**
	 * Конструктор
	 */
	public MainApp() {

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

	public static void main(String[] args) {
		launch(args);
	}
}
