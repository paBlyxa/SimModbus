package com.we.simModbus.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicIntegerArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.we.modbus.model.DataModelTable;
import com.we.modbus.model.ModbusDataModel;
import com.we.simModbus.model.Tag;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class TagDataModel implements ModbusDataModel {

	private final static Logger logger = LoggerFactory.getLogger(TagDataModel.class);

	private final Map<Integer, List<Tag>> tagMap;
	private final ObservableList<Tag> tagList;
	// All memory
	private final AtomicIntegerArray data;

	public TagDataModel(int regCounts) {
		tagMap = new HashMap<Integer, List<Tag>>();
		tagList = FXCollections.observableArrayList();
		data = new AtomicIntegerArray(regCounts);
		tagList.addListener(new ListChangeListener<Tag>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends Tag> change) {
				onChangedList(change);
			}

		});
		init();
	}

	private void init() {
		// В качестве образца добавляем данные.
/*		Tag tag1 = new TagInt16();
		tag1.setAddress(1);
		tag1.setName("tag1");
		tag1.setType(Type.INT);
		tag1.setValue(0);

		Tag tag2 = new TagInt16();
		tag2.setAddress(2);
		tag2.setName("tag2");
		tag2.setType(Type.INT);
		tag2.setValue(0);

		tagList.addAll(tag1, tag2);*/
	}

	/**
	 * Метод для чтения из памяти.
	 * 
	 * @param reference
	 *            адрес данных в памяти
	 * @param length
	 *            количество данных
	 * @param table
	 *            тип данных
	 * 
	 * @return массив байт прочитанных значений
	 */
	@Override
	public byte[] read(int reference, int length, DataModelTable table) {
		byte[] values = new byte[0];
		if (table == DataModelTable.Coils || table == DataModelTable.DiscretesInput) {
			// Чтение по битам
			values = new byte[(length + 7) / 8];
			for (int i = 0; i < length; i++) {
				int refReg = (reference + i) / 16;
				int refBit = (reference + i) % 16;
				values[i / 8] <<= 1;
				values[i / 8] += (byte) ((data.get(refReg) >> refBit) & 0x01);
			}
		} else {
			// Чтение по регистрам
			values = new byte[length * 2];
			for (int i = 0; i < length; i++) {
				values[i * 2] = (byte) (data.get(reference + i) >> 8);
				values[i * 2 + 1] = (byte) (data.get(reference + i));
			}
		}
		logger.debug("Read from model - reference {}, length {}, values {}", reference, length, values);
		return values;
	}

	/**
	 * Метод для записи в память.
	 * 
	 * @param reference
	 *            адрес данных в памяти
	 * @param length
	 *            количество данных
	 * @param table
	 *            тип данных
	 * @param values
	 *            массив байт значений для записи
	 * 
	 * @return true если запись успешна
	 */
	@Override
	public boolean write(int reference, int length, DataModelTable table, byte[] values) {
		logger.debug("Write to model - reference {}, length {}, values {}", reference, length, values);
		if (table == DataModelTable.Coils || table == DataModelTable.DiscretesInput) {
			// Запись по битам
			for (int i = 0; i < length; i++) {
				int refReg = (reference + i) / 16;
				int refBit = (reference + i) % 16;
				int value = 1 << refBit;
				if ((values[i / 8] & 0x01) == 0x01) {
					// Set bit
					data.set(refReg, data.get(refReg) | value);
				} else {
					// Reset bit
					data.set(refReg, data.get(refReg) & (~value));
				}
				values[i / 8] >>= 1;
			}
		} else {
			// Запись по регистрам
			for (int i = 0; i < length; i++) {
				int value = (((int)values[i * 2] & 0xFF) << 8) + ((int)values[i * 2 + 1] & 0xFF);
				logger.debug("Write to model register reference: {}, value: {}", reference + i, value);
				data.set(reference + i, value);
			}
			updateTags(reference, length);
		}
		return true;
	}

	/**
	 * Возвращает количество регистров.
	 * 
	 * @return количество регистров
	 */
	@Override
	public int getRegisterCount() {
		return data.length();
	}

	/**
	 * Обновление значений переменных из памяти.
	 * @param readReference
	 * @param readLength
	 */
	private void updateTags(int readReference, int readLength) {
		if (readReference > 0){
			// Для того чтобы обновились тэги, которые занимают больше одного регистра
			readReference--;
			readLength++;
		}
		for (int ref = readReference; ref < (readReference + readLength); ref++) {
			List<Tag> tagList = tagMap.get(ref);
			if (tagList != null) {
				for (Tag tag : tagList) {
					switch (tag.getType()) {
					case INT:
						tag.setValue(data.get(tag.getAddress()));
						break;
					case BOOL:
						break;
					case DINT:
						tag.setValue(((data.get(tag.getAddress() + 1) & 0xFFFF) << 16)
								+ (data.get(tag.getAddress()) & 0xFFFF));
						break;
					case FLOAT:
						tag.setValue(Float.intBitsToFloat(((data.get(tag.getAddress() + 1) & 0xFFFF) << 16)
								+ (data.get(tag.getAddress()) & 0xFFFF)));
						break;
					case FLOATINV:
						tag.setValue(Float.intBitsToFloat(((data.get(tag.getAddress() + 1) & 0xFFFF) )
								+ ((data.get(tag.getAddress()) & 0xFFFF) << 16)));
						break;
					default:
						break;
					}
				}
			}
		}
		logger.debug("Update tags from model - reference {}, length {}", readReference, readLength);
	}

	/**
	 * Возвращает наблюдаемый список все переменных
	 * 
	 * @return
	 */
	public ObservableList<Tag> getTagList() {
		return tagList;
	}

	/**
	 * Вызывается при изменении списка переменных
	 */
	private void onChangedList(ListChangeListener.Change<? extends Tag> change) {
		while (change.next()) {
			if (change.wasAdded()) {
				for (Tag tag : change.getAddedSubList()) {
					addToMap(tag);
				}
			} else if (change.wasRemoved()) {
				for (Tag tag : change.getAddedSubList()) {
					removeFromMap(tag);
				}
			}
		}
	}

	/**
	 * Добавляем переменную во внутреннюю Map
	 * 
	 * @param tag
	 */
	private void addToMap(Tag tag) {
		List<Tag> tagList = tagMap.get(tag.getAddress());
		if (tagList == null) {
			tagList = new ArrayList<Tag>();
			tagList.add(tag);
			tagMap.put(tag.getAddress(), tagList);
		} else {
			tagList.add(tag);
		}
		logger.debug("Added tag to map [{} {}]", tag.getName(), tag.getAddress());
	}

	/**
	 * Удаляет переменную из внутренней Map
	 * 
	 * @param tag
	 */
	private void removeFromMap(Tag tag) {
		List<Tag> tagList = tagMap.get(tag.getAddress());
		if (tagList == null) {
			logger.error("No such tag in map [{} {}]", tag.getName(), tag.getAddress());
		} else {
			if (!tagList.remove(tag)) {
				logger.error("No such tag in map [{} {}]", tag.getName(), tag.getAddress());
			} else {
				logger.debug("Delete tag from map [{} {}]", tag.getName(), tag.getAddress());
			}
		}
	}

	/**
	 * Добавление переменной в память
	 * 
	 * @param tag
	 */
	public void addTag(Tag tag) {
		tagList.add(tag);
	}

	/**
	 * Удаление переменной
	 * 
	 * @param tag
	 */
	public void deleteTag(Tag tag) {
		tagList.remove(tag);
	}

	/**
	 * Обновление значения переменной
	 */
	public void updateTag(Tag tag) {
		switch(tag.getType()){
		case BOOL:
			data.set(tag.getAddress(), (int) tag.getValue());
			break;
		case INT:
			data.set(tag.getAddress(), (int) tag.getValue());
			break;
		case DINT:
			data.set(tag.getAddress(), (int) tag.getValue() & 0xFFFF);
			data.set(tag.getAddress() + 1, ((int) tag.getValue() >> 16) & 0xFFFF);			
			break;
		case FLOAT:
			data.set(tag.getAddress(), Float.floatToIntBits(tag.getValue().floatValue()) & 0xFFFF);
			data.set(tag.getAddress() + 1, (Float.floatToIntBits(tag.getValue().floatValue()) >> 16) & 0xFFFF);
			break;
		case FLOATINV:
			data.set(tag.getAddress(), (Float.floatToIntBits(tag.getValue().floatValue())  >> 16) & 0xFFFF);
			data.set(tag.getAddress() + 1, (Float.floatToIntBits(tag.getValue().floatValue())) & 0xFFFF);
			break;
		}
		// Так как возможно к одной части памяти подключить несколько переменных, то нужно обновить их значения.
		updateTags(tag.getAddress(), (tag.size() + 1) / 2);
	}
}
