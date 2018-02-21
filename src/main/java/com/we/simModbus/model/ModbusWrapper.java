package com.we.simModbus.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Вспомогательный класс для обёртывания списка переменных.
 * Используется для сохранения списка переменных в XML.
 * 
 */
@XmlRootElement(name = "tags")
public class ModbusWrapper {

	private List<Tag> tags;
	private String address = "127.0.0.1";
	private String port = "502";
	private boolean master = true;
	
	@XmlElement(name = "tag")
	public List<Tag> getTags(){
		return tags;
	}
	
	@XmlElement(name = "address")
	public String getAddress(){
		return address;
	}
	
	@XmlElement(name = "port")
	public String getPort(){
		return port;
	}
	
	@XmlElement(name = "master")
	public boolean isMaster(){
		return master;
	}
	
	public void setTags(List<Tag> tags){
		this.tags = tags;
	}
	
	public void setAddress(String address){
		this.address = address;
	}
	
	public void setPort(String port){
		this.port = port;
	}
	
	public void setMaster(boolean master){
		this.master = master;
	}
}
