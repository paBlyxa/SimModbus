package com.we.simModbus.service;

import com.we.simModbus.model.ModbusWrapper;
import com.we.simModbus.model.Tag;

import javafx.collections.ObservableList;

public interface TagAddDeleteHandler {

	public void addTag(Tag tag);
	public void deleteTag(Tag tag);
	public void handleNewTag();
	public void handleDeleteTag();
	public ObservableList<Tag> getTagList();
	public ModbusWrapper getWrapper();
}
