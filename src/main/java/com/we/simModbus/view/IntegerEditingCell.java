package com.we.simModbus.view;

import java.util.regex.Pattern;

import com.we.simModbus.model.Tag;
import com.we.simModbus.model.Type;
import com.we.simModbus.service.TagDataModel;

import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;

public class IntegerEditingCell extends TableCell<Tag, Number> {
	
	private final TextField textField = new TextField();
	private final Pattern intPattern = Pattern.compile("-?\\d+");
	private final Pattern floatPattern = Pattern.compile("-?\\d*\\.?,?\\d*");
	private final TagDataModel dataModel;
	
	public IntegerEditingCell(TagDataModel dataModel) {
		this.dataModel = dataModel;
		textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
			if (! isNowFocused){
				processEdit();
			}
		});
		textField.setOnAction(envent -> processEdit());
	}
	
	private void processEdit() {
		String text = textField.getText();
		if (intPattern.matcher(text).matches()) {
			commitEdit(Integer.parseInt(text));
		} else if (floatPattern.matcher(text).matches()) {
			commitEdit(Float.parseFloat(text));
		} else {
			cancelEdit();
		}
	}
	
	@Override
	public void updateItem(Number value, boolean empty) {
		super.updateItem(value, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else if (isEditing()) {
			setText(null);
			textField.setText(value.toString());
			setGraphic(textField);
		} else {
			setText(value.toString());
			setGraphic(null);
		}
	}
	
	@Override
	public void startEdit() {
		super.startEdit();
		Number value = getItem();
		if (value != null) {
			textField.setText(value.toString());
			setGraphic(textField);
			setText(null);
		}
	}
	
	@Override
	public void cancelEdit() {
		super.cancelEdit();
		setText(getItem().toString());
		setGraphic(null);
	}
	
	// This seems necessary to persist the edit on loss of focus: not sure why
	@Override
	public void  commitEdit(Number value){
		super.commitEdit(value);
		Tag tag = (Tag)this.getTableRow().getItem();
		if ((tag.getType() == Type.FLOAT) || (tag.getType() == Type.FLOATINV)){
			tag.setValue(value.floatValue());
		} else {
			tag.setValue(value.intValue());
		}
		dataModel.updateTag(tag);
	}

}
