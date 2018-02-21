package com.we.simModbus.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.we.simModbus.model.TagForm;
import com.we.simModbus.model.Type;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateTagDialogController {

	private final static Logger logger = LoggerFactory.getLogger(CreateTagDialogController.class);
	
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
	@FXML
	private TextField offsetTagField;
	@FXML
	private CheckBox numberItemsCheckBox;
	
	private Stage dialogStage;
	private TagForm tag;
	private boolean okClicked = false;
	
	/**
	 * Инициализирует класс-контроллер. Этот метод вызывается
	 * автоматически после того, как fxml-файл будет загружен.
	 */
	@FXML
	private void initialize(){
		countTagField.disableProperty().bind(numberItemsCheckBox.selectedProperty().not());
		offsetTagField.disableProperty().bind(numberItemsCheckBox.selectedProperty().not());
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
			if (numberItemsCheckBox.isSelected()){
				tag.setCount(Integer.parseInt(countTagField.getText()));
				tag.setOffset(Integer.parseInt(offsetTagField.getText()));
			} else {
				tag.setCount(1);
				tag.setOffset(0);
			}
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
		if ((nameTagField.getText() == null) || (nameTagField.getText().isEmpty())){
			logger.warn("Tag name not valid");
			return false;
		}
		if (typeTagField.getValue() == null){
			logger.warn("Tag type not valid");
			return false;
		}
		if ((addressTagField.getText() == null) || (addressTagField.getText().isEmpty())){
			logger.warn("Tag address not valid");
			return false;
		}
		if ((valueTagField.getText() == null) || (valueTagField.getText().isEmpty())){
			logger.warn("Tag value not valid");
			return false;
		}
		if (numberItemsCheckBox.isSelected()){
			if ((countTagField.getText() == null) || (countTagField.getText().isEmpty())){
				logger.warn("Tag count not valid");
				return false;
			}
			if ((offsetTagField.getText() == null) || (offsetTagField.getText().isEmpty())){
				logger.warn("Tag offset not valid");
				return false;
			}
		}
		return true;
	}
}
