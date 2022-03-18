package com.tecnotree.model;

public class Dmn {

	private String table;
	private String sourceObject;
	private String inputColumn;
	private String outputColum;
	private String rejectTx;
	
	String DECISION_DEFINITION = "http://uat.appolo.com.pe/dmnService/decision-definition/";
	
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public String getSourceObject() {
		return sourceObject;
	}
	public void setSourceObject(String sourceObject) {
		this.sourceObject = sourceObject;
	}
	public String getInputColumn() {
		return inputColumn;
	}
	public void setInputColumn(String inputColumn) {
		this.inputColumn = inputColumn;
	}
	public String getOutputColum() {
		return outputColum;
	}
	public void setOutputColum(String outputColum) {
		this.outputColum = outputColum;
	}
	public String getRejectTx() {
		return rejectTx;
	}
	public void setRejectTx(String rejectTx) {
		this.rejectTx = rejectTx;
	}
	public String getDECISION_DEFINITION() {
		return DECISION_DEFINITION;
	}
	public void setDECISION_DEFINITION(String dECISION_DEFINITION) {
		DECISION_DEFINITION = dECISION_DEFINITION;
	}
	public String getUri(String table) {
		return DECISION_DEFINITION + table;
	}
}
