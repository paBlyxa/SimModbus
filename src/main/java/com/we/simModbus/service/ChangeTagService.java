package com.we.simModbus.service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.we.simModbus.model.Tag;

public class ChangeTagService implements Runnable {

	private final static Logger logger = LoggerFactory.getLogger(ChangeTagService.class);
	
	private final TagDataModel tagDataModel;
	
	List<Tag> tagList;

	public ChangeTagService(TagDataModel tagDataModel) {
		this.tagDataModel = tagDataModel;
		logger.debug("Create new ChangeTagService");
		tagList = new CopyOnWriteArrayList<Tag>();
	}

	/**
	 * Add tag to cyclic change
	 * 
	 * @param tag
	 */
	public void add(Tag tag) {
		tagList.add(tag);
	}

	/**
	 * Remove tag from cyclic change
	 * 
	 * @param tag
	 * @return true if this list contained the specified element
	 */
	public boolean remove(Tag tag) {
		return tagList.remove(tag);
	}
	
	/**
	 * Return true if this service contains tag
	 * @param tag
	 * @return true if this service contains tag
	 */
	public boolean contains(Tag tag){
		return tagList.contains(tag);
	}

	/**
	 * Return the number tags in this service.
	 * @return
	 * 		the number tags in this service.
	 */
	public int size(){
		return tagList.size();
	}
	
	@Override
	public void run() {
		logger.debug("run change tags");
		for (Tag tag : tagList) {
			logger.debug("Change tag: [{}]", tag.getName());
			switch (tag.getType()) {
			case BOOL:
				if ((int) tag.getValue() == 1) {
					tag.setValue(0);
				} else {
					tag.setValue(1);
				}
				break;
			case DINT:
				tag.setValue((int) tag.getValue() + 1);
				break;
			case FLOAT:
				tag.setValue((float) tag.getValue() + 1);
				break;
			case INT:
				tag.setValue((int) tag.getValue() + 1);
				break;
			}
			tagDataModel.updateTag(tag);
		}
	}
}
