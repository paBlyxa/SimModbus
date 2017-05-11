package com.we.simModbus.service;

import com.we.simModbus.model.Tag;

public interface TagRWHandler {

	public void read(Tag tag);
	public void write(Tag tag);
}
