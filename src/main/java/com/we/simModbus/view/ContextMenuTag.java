package com.we.simModbus.view;

import com.we.simModbus.model.Tag;
import com.we.simModbus.service.TagHandler;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.input.KeyCombination;

public class ContextMenuTag extends ContextMenu {

	private final MenuItem readOnceItem;
	private final MenuItem writeOnceItem;
	private final MenuItem deleteItem;
	
	public ContextMenuTag(TagHandler tagHandler, TableRow<Tag> tableRow, BooleanProperty isConnected) {

		readOnceItem = new MenuItem("Read once");
		readOnceItem.setAccelerator(KeyCombination.keyCombination("Ctrl+R"));
		readOnceItem.setOnAction((event) -> tagHandler.read(tableRow.getItem()));

		writeOnceItem = new MenuItem("Write once");
		writeOnceItem.setAccelerator(KeyCombination.keyCombination("Ctrl+W"));

		SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();

		deleteItem = new MenuItem("Delete");

		this.getItems().addAll(readOnceItem, writeOnceItem, separatorMenuItem, deleteItem);

		// only display context menu for non-null items:
		tableRow.contextMenuProperty().bind(
				Bindings.when(Bindings.isNotNull(tableRow.itemProperty()))
				.then(this)
				.otherwise((ContextMenuTag)null));
		
		readOnceItem.disableProperty().bind(isConnected.not());
		writeOnceItem.disableProperty().bind(isConnected.not());
	}
}
