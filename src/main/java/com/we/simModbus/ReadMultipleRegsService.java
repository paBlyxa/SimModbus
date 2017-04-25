package com.we.simModbus;

import com.we.modbus.ModbusTCPMaster;
import com.we.simModbus.model.Tag;

import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ReadMultipleRegsService extends Service<Void> {

	private final ModbusTCPMaster modbusTCPMaster;
	private int readReference;
	private int readLength;
	private int[] readResults;
	private ObservableList<Tag> tagList;
	private Tag tag;

	public ReadMultipleRegsService(ModbusTCPMaster modbusTCPMaster) {
		this.modbusTCPMaster = modbusTCPMaster;
	}

	public ReadMultipleRegsService(ModbusTCPMaster modbusTCPMaster, ObservableList<Tag> tagList) {
		this.modbusTCPMaster = modbusTCPMaster;
		this.tagList = tagList;
	}

	public void setReadReference(int reference) {
		this.readReference = reference;
	}

	public void setReadLength(int length) {
		this.readLength = length;
	}

	public void setReadAll() {
		tag = null;
	}

	public void setReadTag(Tag tag) {
		this.tag = tag;
	}

	@Override
	protected Task<Void> createTask() {
		return new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				calc();
				readResults = new int[readLength];
				modbusTCPMaster.readMultipleRegisters(readReference, readLength, readResults);
				this.updateMessage("Registers has been read from: " + readReference + ", length: " + readLength);
				updateTags();
				return null;
			}
		};
	}

	private void calc() {
		if (tag == null) {
			if (!tagList.isEmpty()) {
				readReference = tagList.get(0).getAddress();
				readLength = 1;
				for (Tag tag : tagList) {
					System.out.println("tagName: " + tag.getName() + ", addr = " + tag.getAddress());
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
			}
		} else {
			readReference = tag.getAddress();
			readLength = 1;
		}
	}

	private void updateTags() {
		for (Tag tag : tagList) {
			switch (tag.getType()) {
			case INT:
				if ((tag.getAddress() >= readReference) && (tag.getAddress() - readReference < readLength)) {
					tag.setValue(readResults[tag.getAddress() - readReference]);
				}
				break;
			case BOOL:
				break;
			case DINT:
				if ((tag.getAddress() >= readReference) && (tag.getAddress() - readReference + 1 < readLength)) {
					tag.setValue((readResults[tag.getAddress() - readReference] << 16)
							+ readResults[tag.getAddress() - readReference + 1]);
				}
				break;
			case FLOAT:
				if ((tag.getAddress() >= readReference) && (tag.getAddress() - readReference + 1 < readLength)) {
					tag.setValue((readResults[tag.getAddress() - readReference] << 16)
							+ readResults[tag.getAddress() - readReference + 1]);
				}
				break;
			default:
				break;
			}
		}
	}
}
