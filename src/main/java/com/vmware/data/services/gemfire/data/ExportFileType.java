package com.vmware.data.services.gemfire.data;

public enum ExportFileType {
    gfd("gfd"),
	json("json"),
	csv("csv")
	;
	
	private final String s;
	
	ExportFileType(String s) {
		this.s = s;
	}
	
	public String toString() {
		return this.s;
	}
	

}
