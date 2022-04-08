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
	
	public boolean validateRule(String rule) throws Exception {
		/*if(this.table == null) {
			throw new Exception("Not found table property in the rule No." + rule);
		}else if(this.table.equals(""))
			throw new Exception("Not found a value in the table property in the rule No." + rule);
				
		if(this.sourceObject == null) {
			throw new Exception("Not found sourceObject property in the rule No." + rule);
		}else if(this.sourceObject.equals(""))
			throw new Exception("Not found a value in the sourceObject property in the rule No." + rule);
				
		if(this.inputColumn == null) {
			throw new Exception("Not found inputColumn property in the rule No." + rule);
		}else if(this.inputColumn.equals(""))
			throw new Exception("Not found a value in the inputColumn property in the rule No." + rule);
		
		if(this.outputColum == null) {
			throw new Exception("Not found outputColum property in the rule No." + rule);
		}else if(this.outputColum.equals(""))
			throw new Exception("Not found a value in the outputColum property in the rule No." + rule);
		
		if(this.rejectTx == null) {
			throw new Exception("Not found rejectTx property in the rule No." + rule);
		}else if(this.rejectTx.equals(""))
			throw new Exception("Not found a value in the rejectTx property in the rule No." + rule);
		*/
		return true;
	}
}
