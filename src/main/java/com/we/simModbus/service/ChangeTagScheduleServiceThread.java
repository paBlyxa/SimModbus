package com.we.simModbus.service;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.we.simModbus.model.Tag;

import javafx.concurrent.Task;

public class ChangeTagScheduleServiceThread extends TagScheduleServiceThread {

	private final static Logger logger = LoggerFactory.getLogger(ChangeTagScheduleServiceThread.class);

	private final TagDataModel tagDataModel;
	private final Set<Tag> tagList;

	public ChangeTagScheduleServiceThread(TagDataModel tagDataModel) {
		this.tagDataModel = tagDataModel;
		tagList = new CopyOnWriteArraySet<Tag>();
	}

	public ChangeTagScheduleServiceThread(TagDataModel tagDataModel, Executor executor) {
		this.tagDataModel = tagDataModel;
		tagList = new CopyOnWriteArraySet<Tag>();
		this.setExecutor(executor);
	}

	/**
	 * Add tag to cyclic change set
	 * 
	 * @param tag
	 */
	public void add(Tag tag) {
		tagList.add(tag);
	}

	/**
	 * Remove tag from cyclic change set
	 * 
	 * @param tag
	 * @return true if this list contained the specified element
	 */
	public boolean remove(Tag tag) {
		return tagList.remove(tag);
	}

	/**
	 * Return true if this service contains tag
	 * 
	 * @param tag
	 * @return true if this service contains tag
	 */
	public boolean contains(Tag tag) {
		return tagList.contains(tag);
	}

	/**
	 * Return true if no tags in this service.
	 * 
	 * @return true if no tags in this service.
	 */
	@Override
	public boolean isEmpty() {
		return tagList.isEmpty();
	}

	/**
	 * Attempts to stop execution this service
	 * Returns:whether any running task was cancelled, false if no task was cancelled. In any case, the ScheduledService will stop iterating.
	 */
	public boolean stop() {
		return this.cancel();
	}

	/**
	 * @return true if task not executed
	 */
	public boolean isStopped() {
		return !this.isRunning();
	}

	/**
	 * Increment tags which in set
	 */
	@Override
	protected Task<Void> createTask() {
		logger.debug("run createTask - change tags");
		return new Task<Void>() {
			@Override
			protected Void call() {
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
					case FLOATINV:
						tag.setValue((float) tag.getValue() + 1);
						break;
					default:
						break;
					}
					tagDataModel.updateTag(tag);
				}
				return null;
			}
		};
	}
}
