package com.we.simModbus.service;

import java.util.Collection;
import java.util.concurrent.Executor;

import com.we.modbus.ModbusTCPMaster;
import com.we.modbus.model.ModbusDataModel;
import com.we.simModbus.model.Tag;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ReadMultipleRegsService extends Service<Void> {

	private final ReadMultipleRegsTaskFactory factory;

	public ReadMultipleRegsService(int unitId, ModbusTCPMaster modbusTCPMaster, ModbusDataModel dataModel, TransactionIdFactory transactionIdFactory) {
		factory = new ReadMultipleRegsTaskFactory(unitId, modbusTCPMaster, dataModel, transactionIdFactory);
	}

	public ReadMultipleRegsService(int unitId, ModbusTCPMaster modbusTCPMaster, ModbusDataModel dataModel, TransactionIdFactory transactionIdFactory, Executor executor) {
		this(unitId, modbusTCPMaster, dataModel, transactionIdFactory);
		this.setExecutor(executor);
	}

	@Override
	protected Task<Void> createTask() {
		return factory.newInstance();
	}

	/**
	 * Add tag to set
	 */
	public void add(Tag tag) {
		factory.add(tag);
	}

	/**
	 * Add all tags to set
	 */
	public void addAll(Collection<? extends Tag> c) {
		factory.addAll(c);
	}

	/**
	 * Remove tag from set
	 */
	public boolean remove(Tag tag) {
		return factory.remove(tag);
	}

	/**
	 * Check if set contains tag
	 * 
	 * @return true if set conteins tag
	 */
	public boolean contains(Tag tag) {
		return factory.contains(tag);
	}

	/**
	 * Returns true if this set contains no elements.
	 * 
	 * @return true if this set contains no elements.
	 */
	public boolean isEmpty() {
		return factory.isEmpty();
	}

	/**
	 * Attempts to cancel execution of this task.
	 */
	public void stop() {
		this.cancel();
	}

	/**
	 * @return true if task is not executing.
	 */
	public boolean isStopped() {
		return !this.isRunning();
	}
}
