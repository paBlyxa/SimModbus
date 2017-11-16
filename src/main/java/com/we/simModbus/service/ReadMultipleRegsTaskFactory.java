package com.we.simModbus.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.we.modbus.ModbusTCPMaster;
import com.we.modbus.model.ModbusDataModel;
import com.we.simModbus.model.Tag;

public class ReadMultipleRegsTaskFactory {
	
	private final static Logger logger = LoggerFactory.getLogger(ReadMultipleRegsTaskFactory.class);
	
	private final ModbusTCPMaster modbusTCPMaster;
	private final ModbusDataModel dataModel;
	private final TransactionIdFactory transIdFactrory;
	private final Set<Tag> tagList;
	private final int unitId;

	private int readReference;
	private int readLength;
	private boolean tagListChanged;
	
	public ReadMultipleRegsTaskFactory(int unitId, ModbusTCPMaster modbusTCPMaster, ModbusDataModel dataModel, TransactionIdFactory transIdFactrory){
		this.modbusTCPMaster = modbusTCPMaster;
		this.dataModel = dataModel;
		this.transIdFactrory = transIdFactrory;
		this.unitId = unitId;
		tagListChanged = true;
		this.tagList = new HashSet<Tag>();
	}
	
	public ReadMultipleRegsTask newInstance(){
		if (tagListChanged){
			calc();
			tagListChanged = false;
		}
		return new ReadMultipleRegsTask(unitId, readReference, readLength, transIdFactrory.getId(), modbusTCPMaster, dataModel);
	}
	
	/**
	 * Calculate read reference (register address) and read length (regoster
	 * count).
	 */
	private void calc() {
		// TODO calculate max size 125 regs
		readReference = tagList.iterator().next().getAddress();
		readLength = 1;
		if (tagList.iterator().next().getType().size() > 2) {
			readLength++;
		}
		for (Tag tag : tagList) {
			if (tag.getAddress() < readReference) {
				readLength += readReference - tag.getAddress();
				readReference = tag.getAddress();
			} else if (tag.getAddress() >= (readLength + readReference)) {
				readLength = tag.getAddress() - readReference + 1;
				if (tag.getType().size() > 2) {
					readLength++;
				}
			}

		}
		logger.debug("Calculated reference: {}, length = {}", readReference, readLength);
	}
	
	/**
	 * Add tag to set
	 */
	public void add(Tag tag) {
		tagList.add(tag);
		tagListChanged = true;
	}

	/**
	 * Add all tags to set
	 */
	public void addAll(Collection<? extends Tag> c) {
		tagList.addAll(c);
		tagListChanged = true;
	}

	/**
	 * Remove tag from set
	 */
	public boolean remove(Tag tag) {
		tagListChanged = tagList.remove(tag); 
		return tagListChanged;
	}

	/**
	 * Check if set contains tag
	 * 
	 * @return true if set conteins tag
	 */
	public boolean contains(Tag tag) {
		return tagList.contains(tag);
	}

	/**
	 * Returns true if this set contains no elements.
	 * 
	 * @return true if this set contains no elements.
	 */
	public boolean isEmpty() {
		return tagList.isEmpty();
	}
}
