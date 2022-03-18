package com.tecnotree.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author cevalfr
 *
 */
@JsonInclude(Include.NON_NULL)
public class Payload {

	@JsonProperty("ApplicationDetails")
	private ApplicationDetails applicationDetails;
	
	@JsonProperty("Request")
	private Request request;
	
	@JsonProperty("AdditionalInformation")
	private AdditionalInformation additionalInformation;

	public ApplicationDetails getApplicationDetails() {
		return applicationDetails;
	}

	public void setApplicationDetails(ApplicationDetails applicationDetails) {
		this.applicationDetails = applicationDetails;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public AdditionalInformation getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(AdditionalInformation additionalInformation) {
		this.additionalInformation = additionalInformation;
	}
	
	
	/**
	 * Method sets input json to the DMN
	 * @param inputColumn
	 * @param valueInputColumn
	 * @return Input json to the DMN
	 */
	public String getDmnInputJson(String inputColumn, String valueInputColumn) {
		String dmnInputPayload = "";
				
		dmnInputPayload = "{ \""+inputColumn+"\":\""+valueInputColumn+"\" }";
			
		return dmnInputPayload;
	}
	
	public String getValueSourceObject(String sourceObject, String jsonPayload) {
		DocumentContext context = JsonPath.parse(jsonPayload);
		String valueSourceObject  = context.read("$."+sourceObject, String.class);
		return valueSourceObject;
	}
	
	
}
