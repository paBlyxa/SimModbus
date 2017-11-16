package com.we.simModbus.service;

import com.we.simModbus.model.Tag;

import javafx.concurrent.ScheduledService;

public abstract class TagScheduleServiceThread extends ScheduledService<Void> {

	/**
	 * Add tag to cyclic change set
	 * 
	 * @param tag
	 */
	public abstract void add(Tag tag);

	/**
	 * Remove tag from cyclic change set
	 * 
	 * @param tag
	 * @return true if this list contained the specified element
	 */
	public abstract boolean remove(Tag tag);

	/**
	 * Return true if this service contains tag
	 * 
	 * @param tag
	 * @return true if this service contains tag
	 */
	public abstract boolean contains(Tag tag);

	/**
	 * Return true if no tag in service.
	 * 
	 * @return true if no tag in service.
	 */
	public abstract boolean isEmpty();

	/**
	 * Attempts to stop execution this service
	 * Returns:whether any running task was cancelled, false if no task was cancelled. In any case, the ScheduledService will stop iterating.
	 */
	public abstract boolean stop();

	/**
	 * @return true if task not executed
	 */
	abstract boolean isStopped();
}
