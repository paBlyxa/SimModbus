package com.we.simModbus.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.we.simModbus.model.Tag;
import com.we.simModbus.model.TagBool;
import com.we.simModbus.model.TagFloat;
import com.we.simModbus.model.TagFloatInv;
import com.we.simModbus.model.TagInt16;
import com.we.simModbus.model.TagInt32;
import com.we.simModbus.model.Type;

public class TagImportExportService {

	private final static Logger logger = LoggerFactory.getLogger(TagImportExportService.class);
	
	public static void exportTagsToFile(File file, List<Tag> tags){
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			bw.write("Name;Type;Address;Value");
			for (Tag tag : tags){
				bw.newLine();
				bw.write(tag.getName() + ";" + tag.getType().name() + ";" + tag.getAddress() + ";" + tag.getValue() + ";");
			}
			logger.info("Export to file {} has done", file);
		}catch (IOException e) {
			logger.error("An error occured while writing to file", e);
		}
	}
	
	public static void importTagsFromFile(File file, List<Tag> tags){
		try (BufferedReader br = new BufferedReader(new FileReader(file))){
			br.readLine();
			String str;
			while ((str = br.readLine()) != null){
				String[] strs = str.split(";");
				Tag tag = null;
				Type type = Type.valueOf(strs[1]);
				switch (type) {
				case BOOL:
					tag = new TagBool();
					tag.setValue(Integer.parseInt(strs[3]));
					break;
				case DINT:
					tag = new TagInt32();
				tag.setValue(Integer.parseInt(strs[3]));
					break;
				case FLOAT:
					tag = new TagFloat();
					tag.setValue(Float.parseFloat(strs[3]));
					break;
				case FLOATINV:
					tag = new TagFloatInv();
				tag.setValue(Float.parseFloat(strs[3]));
					break;
				case INT:
					tag = new TagInt16();
					tag.setValue(Integer.parseInt(strs[3]));
					break;
				default:
					break;
				}
				if (tag != null){
					tag.setName(strs[0]);
					tag.setAddress(Integer.parseInt(strs[2]));
					tag.setType(type);
					tags.add(tag);
				}
			}
			logger.info("Import from file {} has done", file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
