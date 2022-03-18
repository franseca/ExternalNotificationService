package com.tecnotree.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class DMNResponse {

	private List<DMNValues> result;
	
	public List<DMNValues> getResult() {
		return result;
	}
	public void setResult(List<DMNValues> result) {
		this.result = result;
	}
	
	
}
