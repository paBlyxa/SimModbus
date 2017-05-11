package com.we.simModbus.view;

import java.util.concurrent.TimeUnit;

import com.we.simModbus.model.Tag;
import com.we.simModbus.service.TagChangeHandler;
import com.we.simModbus.service.TagDeleteHandler;
import com.we.simModbus.service.TagRWHandler;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.input.KeyCombination;

public class ContextMenuFactory {

	public static ContextMenu getContextMenuMaster(TagRWHandler tagRWHandler, TagDeleteHandler tagDeleteHandler, TableRow<Tag> tableRow,
			BooleanProperty isConnected) {
		return new ContextMenuMaster(tagRWHandler, tagDeleteHandler, tableRow, isConnected);
	}

	public static ContextMenu getContextMenuSlave(TagChangeHandler tagChangeHandler, TagDeleteHandler tagDeleteHandler, TableRow<Tag> tableRow) {
		return new ContextMenuSlave(tagChangeHandler, tagDeleteHandler, tableRow);
	}

	static class ContextMenuMaster extends ContextMenu {

		ContextMenuMaster(TagRWHandler tagHandler, TagDeleteHandler tagDeleteHandler, TableRow<Tag> tableRow, BooleanProperty isConnected) {

			MenuItem readOnceItem = new MenuItem("Read once");
			readOnceItem.setAccelerator(KeyCombination.keyCombination("Ctrl+R"));
			readOnceItem.setOnAction((event) -> tagHandler.read(tableRow.getItem()));

			MenuItem writeOnceItem = new MenuItem("Write once");
			writeOnceItem.setAccelerator(KeyCombination.keyCombination("Ctrl+W"));
			writeOnceItem.setOnAction((event) -> tagHandler.write(tableRow.getItem()));

			SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();

			MenuItem deleteItem = new MenuItem("Delete");
			deleteItem.setOnAction((value) -> tagDeleteHandler.delete(tableRow.getItem()));

			this.getItems().addAll(readOnceItem, writeOnceItem, separatorMenuItem, deleteItem);

			// only display context menu for non-null items:
			tableRow.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(tableRow.itemProperty())).then(this)
					.otherwise((ContextMenuMaster) null));

			readOnceItem.disableProperty().bind(isConnected.not());
			writeOnceItem.disableProperty().bind(isConnected.not());
		}
	}

	static class ContextMenuSlave extends ContextMenu {
		ContextMenuSlave(TagChangeHandler tagChangeHandler, TagDeleteHandler tagDeleteHandler, TableRow<Tag> tableRow) {
			
			MenuItem addItemToCyclicChange500ms = new MenuItem("Add to cyclic change 0.5 s");
			addItemToCyclicChange500ms.setOnAction((event) -> {
				tagChangeHandler.removeFromCyclicChange(tableRow.getItem());
				tagChangeHandler.addToCyclicChange(tableRow.getItem(), 500, TimeUnit.MILLISECONDS);
			});

			MenuItem addItemToCyclicChange1s = new MenuItem("Add to cyclic change 1 s");
			addItemToCyclicChange1s.setOnAction((event) -> {
				tagChangeHandler.removeFromCyclicChange(tableRow.getItem());
				tagChangeHandler.addToCyclicChange(tableRow.getItem(), 1, TimeUnit.SECONDS);
			});

			MenuItem addItemToCyclicChange5s = new MenuItem("Add to cyclic change 5 s");
			addItemToCyclicChange5s.setOnAction((event) -> {
				tagChangeHandler.removeFromCyclicChange(tableRow.getItem());
				tagChangeHandler.addToCyclicChange(tableRow.getItem(), 5, TimeUnit.SECONDS);

			});

			MenuItem addItemToChangeByRequest = new MenuItem("Add to change by request");
			addItemToChangeByRequest.setOnAction((event) -> {
				tagChangeHandler.removeFromCyclicChange(tableRow.getItem());
				tagChangeHandler.addToCyclicChange(tableRow.getItem(), 0, null);
			});

			MenuItem removeItemFromCyclicChange = new MenuItem("Remove from cyclic change");
			removeItemFromCyclicChange.setOnAction((event) ->{
				tagChangeHandler.removeFromCyclicChange(tableRow.getItem());
			});
			
			SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();

			MenuItem deleteItem = new MenuItem("Delete");
			deleteItem.setOnAction((value) ->  tagDeleteHandler.delete(tableRow.getItem()));

			this.getItems().addAll(addItemToCyclicChange500ms, addItemToCyclicChange1s, addItemToCyclicChange5s,
					addItemToChangeByRequest, removeItemFromCyclicChange, separatorMenuItem, deleteItem);

			// only display context menu for non-null items:
			tableRow.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(tableRow.itemProperty())).then(this)
					.otherwise((ContextMenuSlave) null));
		}
	}
}
