package com.we.simModbus.view;

import com.we.simModbus.model.Tag;
import com.we.simModbus.service.TagScheduledHandler;
import com.we.simModbus.model.TimeCyclic;
import com.we.simModbus.service.TagAddDeleteHandler;
import com.we.simModbus.service.TagRWHandler;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.input.KeyCombination;

public class ContextMenuFactory {

	public static ContextMenu getContextMenuMaster(TagRWHandler tagRWHandler, TagScheduledHandler tagScheduledHandler, TagAddDeleteHandler tagDeleteHandler, TableRow<Tag> tableRow,
			BooleanProperty isConnected) {
		return new ContextMenuMaster(tagRWHandler, tagScheduledHandler, tagDeleteHandler, tableRow, isConnected);
	}

	public static ContextMenu getContextMenuSlave(TagScheduledHandler tagScheduledHandler, TagAddDeleteHandler tagDeleteHandler, TableRow<Tag> tableRow) {
		return new ContextMenuSlave(tagScheduledHandler, tagDeleteHandler, tableRow);
	}

	static class ContextMenuMaster extends ContextMenu {

		ContextMenuMaster(TagRWHandler tagHandler, TagScheduledHandler tagScheduledHandler, TagAddDeleteHandler tagDeleteHandler, TableRow<Tag> tableRow, BooleanProperty isConnected) {

			MenuItem readOnceItem = new MenuItem("Read once");
			readOnceItem.setAccelerator(KeyCombination.keyCombination("Ctrl+R"));
			readOnceItem.setOnAction((event) -> tagHandler.read(tableRow.getItem()));

			MenuItem writeOnceItem = new MenuItem("Write once");
			writeOnceItem.setAccelerator(KeyCombination.keyCombination("Ctrl+W"));
			writeOnceItem.setOnAction((event) -> tagHandler.write(tableRow.getItem()));

			MenuItem cyclicReadItem500ms = new MenuItem("Add to cyclic read 0.5 s");
			cyclicReadItem500ms.setOnAction((event) -> {
				tagScheduledHandler.removeFromSchedule(tableRow.getItem());
				tagScheduledHandler.addToSchedule(tableRow.getItem(), TimeCyclic._500MS);
			});

			MenuItem cyclicReadItem1s = new MenuItem("Add to cyclic read 1 s");
			cyclicReadItem1s.setOnAction((event) -> {
				tagScheduledHandler.removeFromSchedule(tableRow.getItem());
				tagScheduledHandler.addToSchedule(tableRow.getItem(), TimeCyclic._1S);
			});

			MenuItem cyclicReadItem5s = new MenuItem("Add to cyclic read 5 s");
			cyclicReadItem5s.setOnAction((event) -> {
				tagScheduledHandler.removeFromSchedule(tableRow.getItem());
				tagScheduledHandler.addToSchedule(tableRow.getItem(), TimeCyclic._5S);
			});
			
			MenuItem removeFormCyclicReadItem = new MenuItem("Remove from cyclic read");
			removeFormCyclicReadItem.setOnAction((event) -> {
				tagScheduledHandler.removeFromSchedule(tableRow.getItem());
			});
			
			SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();

			MenuItem deleteItem = new MenuItem("Delete");
			deleteItem.setAccelerator(KeyCombination.keyCombination("DELETE"));
			deleteItem.setOnAction((value) -> tagDeleteHandler.deleteTag(tableRow.getItem()));

			this.getItems().addAll(readOnceItem, writeOnceItem, cyclicReadItem500ms, cyclicReadItem1s, cyclicReadItem5s,
					removeFormCyclicReadItem, separatorMenuItem, deleteItem);

			// only display context menu for non-null items:
			tableRow.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(tableRow.itemProperty())).then(this)
					.otherwise((ContextMenuMaster) null));

			readOnceItem.disableProperty().bind(isConnected.not());
			writeOnceItem.disableProperty().bind(isConnected.not());
		}
	}

	static class ContextMenuSlave extends ContextMenu {
		ContextMenuSlave(TagScheduledHandler tagScheduledHandler, TagAddDeleteHandler tagDeleteHandler, TableRow<Tag> tableRow) {
			
			MenuItem addItemToCyclicChange500ms = new MenuItem("Add to cyclic change 0.5 s");
			addItemToCyclicChange500ms.setOnAction((event) -> {
				tagScheduledHandler.removeFromSchedule(tableRow.getItem());
				tagScheduledHandler.addToSchedule(tableRow.getItem(), TimeCyclic._500MS);
			});

			MenuItem addItemToCyclicChange1s = new MenuItem("Add to cyclic change 1 s");
			addItemToCyclicChange1s.setOnAction((event) -> {
				tagScheduledHandler.removeFromSchedule(tableRow.getItem());
				tagScheduledHandler.addToSchedule(tableRow.getItem(), TimeCyclic._1S);
			});

			MenuItem addItemToCyclicChange5s = new MenuItem("Add to cyclic change 5 s");
			addItemToCyclicChange5s.setOnAction((event) -> {
				tagScheduledHandler.removeFromSchedule(tableRow.getItem());
				tagScheduledHandler.addToSchedule(tableRow.getItem(), TimeCyclic._5S);

			});

			MenuItem addItemToChangeByRequest = new MenuItem("Add to change by request");
			addItemToChangeByRequest.setOnAction((event) -> {
				tagScheduledHandler.removeFromSchedule(tableRow.getItem());
				tagScheduledHandler.addToSchedule(tableRow.getItem(), null);
			});

			MenuItem removeItemFromCyclicChange = new MenuItem("Remove from cyclic change");
			removeItemFromCyclicChange.setOnAction((event) ->{
				tagScheduledHandler.removeFromSchedule(tableRow.getItem());
			});
			
			SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();

			MenuItem deleteItem = new MenuItem("Delete");
			deleteItem.setAccelerator(KeyCombination.keyCombination("Delete"));
			deleteItem.setOnAction((value) ->  tagDeleteHandler.deleteTag(tableRow.getItem()));

			this.getItems().addAll(addItemToCyclicChange500ms, addItemToCyclicChange1s, addItemToCyclicChange5s,
					addItemToChangeByRequest, removeItemFromCyclicChange, separatorMenuItem, deleteItem);

			// only display context menu for non-null items:
			tableRow.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(tableRow.itemProperty())).then(this)
					.otherwise((ContextMenuSlave) null));
		}
	}
}
