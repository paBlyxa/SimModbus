package com.we.simModbus.view;

import com.we.simModbus.model.TagForm;
import com.we.simModbus.model.Type;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateTagDialogController {

	@FXML
	private TextField nameTagField;
	@FXML
	private ComboBox<Type> typeTagField;
	@FXML
	private TextField addressTagField;
	@FXML
	private TextField valueTagField;
	@FXML
	private TextField countTagField;
	
	private Stage dialogStage;
	private TagForm tag;
	private boolean okClicked = false;
	
	/**
	 * Инициализирует класс-контроллер. Этот метод вызывается
	 * автоматически после того, как fxml-файл будет загружен.
	 */
	@FXML
	private void initialize(){
		countTagField.setText("1");
	}
	
	/**
	 * Устанавливает сцену для этого окна.
	 * 
	 * @param dialogStage	
	 */
	public void setDialogStage(Stage dialogStage){
		this.dialogStage = dialogStage;
	}
	
	/**
	 * Задает переменную, информацию о которой будем менять.
	 * @param <T>
	 * 
	 * @param tag
	 */
	public void setTag(TagForm tag){
		this.tag = tag;
		
		nameTagField.setText(tag.getName());
		typeTagField.getItems().setAll(Type.values());
		addressTagField.setText(Integer.toString(tag.getAddress()));
		valueTagField.setText(tag.getValue());
	}
	
	/**
	 * Returns true, если пользователь кликнул ОК, в другом случае false.
	 * 
	 * @return
	 */
	public boolean isOkClicked() {
		return okClicked;
	}
	
	/**
	 * Вызывается, когда пользователь кликнул по кнопке ОК.
	 */
	@FXML
	private void handleOk(){
		if (isInputValid()){
			tag.setName(nameTagField.getText());
			tag.setType(typeTagField.getValue());
			tag.setAddress(Integer.parseInt(addressTagField.getText()));
			tag.setValue(valueTagField.getText());
			tag.setCount(Integer.parseInt(countTagField.getText()));
			
			okClicked = true;
			dialogStage.close();
		}
	}
	
	/**
	 * Вызывается, когда пользователь кликнул по кнопке Cancel.
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}
	
	/**
	 * Проверяет пользовательский ввод в текстовых полях.
	 * 
	 * @return true, если пользовательский ввод корректен
	 */
	private boolean isInputValid(){
		// TODO validate creating new tag
		return true;
	}
}
