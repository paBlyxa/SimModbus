package com.we.simModbus.service;

import com.we.simModbus.model.Tag;

public interface TagHandler {

	public void read(Tag tag);
	public void write(Tag tag);
	public void delete(Tag tag);
}
