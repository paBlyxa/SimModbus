package com.we.simModbus.model;

public enum Type {

	BOOL("BOOL", 1) {
		public Class<?> getType() { return Boolean.class; }
	},
	INT("INT", 2) {
		public Class<?> getType() {return Short.class; }
	},
	DINT("DINT", 4) {
		public Class<?> getType() {return Integer.class; }
	},
	FLOAT("FLOAT", 4) {
		public Class<?> getType() { return Float.class; }
	};
	
	public abstract Class<?> getType();
	
	Type(String label, int size){
		this.label = label;
		this.size = size;
	}
	
	@Override
	public String toString(){
		return label;
	}
	
	public int size(){
		return size;
	}
	
	private String label;
	// Size in bytes
	private int size;
}
