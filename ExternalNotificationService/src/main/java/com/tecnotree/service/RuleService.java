package com.tecnotree.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.tecnotree.config.ApplicationProperties;
import com.tecnotree.config.AsyncConfiguration;
import com.tecnotree.exception.ExceptionCode;
import com.tecnotree.model.AdditionalInformation;
import com.tecnotree.model.DMNResponse;
import com.tecnotree.model.DMNValues;
import com.tecnotree.model.Dmn;
import com.tecnotree.model.Payload;
import com.tecnotree.model.Rule;
import com.tecnotree.node.logger.GsLog;
import com.tecnotree.node.util.SetLogData;
import com.tecnotree.output.util.GsCustomResponseMappingUtil;

import net.sf.json.JSONObject;

@Service
public class RuleService {

	private static final Logger logger = LoggerFactory.getLogger(RuleService.class);
	
	private long TIMEOUT = 0; 
	private String HTTP_SATUS_OK = "200";
	private List<Rule> ruleList = new  ArrayList<Rule>();
	
	@Autowired
	ApplicationProperties applicationProperties;
	
	@Autowired
	AsyncConfiguration as;
	
	/**
	 * 
	 * @param json
	 * @return
	 * @throws Exception
	 */
	@Async(value="taskExecutor")
    public CompletableFuture<String> validate(ObjectNode json) throws Exception {
		Long start = System.currentTimeMillis();
		
		//logger.debug("Thread of validate method: {}", Thread.currentThread().getName());
				
		//SET LOG INITIAL INFORMATION
		SetLogData logData = new SetLogData();
		String transactionId;
		transactionId = logData.setLogInfo(applicationProperties.getServiceName(), applicationProperties.getLogLevel(), json.toString());
	
		try {
			
			logger.debug("Timeout default: {} ms", TIMEOUT);
			logger.info("TransactionId ID: {}", transactionId);
	        logger.info("Payload received: {}", json.toString());
	        
			for(int i=0; i < ruleList.size(); i++) {
				Rule ruleTemp = (Rule)ruleList.get(i);
				
				logger.info("Rule No.{} validating...", ruleTemp.getId());
				
				//GET PAYLOAD SENT
				ObjectMapper mapperPayload = new ObjectMapper();
				//mapperPayload.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				Payload payload = mapperPayload.readValue(json.toString(),Payload.class);
				
				//logger.info("MSISDN...", payload.getRequest().getMsisdn());
				
				if(ruleTemp.getRuleDetail().getApplicationName() == null) {
					logger.info("Not found applicationName property in the Rule No.{} : {}", ruleTemp.getId(), ruleTemp.getRuleDetail().getApplicationName());
					throw new Exception("Not found applicationName property in the Rule No." + ruleTemp.getId() + ": " + ruleTemp.getRuleDetail().getApplicationName());
				}
				
				//VALIDATE INITIAL FILTER
				if(payload.getRequest().getOperationCode().equalsIgnoreCase(ruleTemp.getRuleDetail().getInitialFilter().getOperationCode())
						&& payload.getRequest().getRejectionCode().equalsIgnoreCase(ruleTemp.getRuleDetail().getInitialFilter().getRejectionCode())) {
						
					logger.debug("Initial filter 1 validated");
					
					//GET TIMEOUT OF THE RULE
					if(ruleTemp.getRuleDetail().getTimeout() != null) {
						logger.debug("Timeout configured in the Rule No.{}: {} ms", ruleTemp.getId(), TIMEOUT);
						TIMEOUT = ruleTemp.getRuleDetail().getTimeout();
					}else {
						logger.info("Not found timeout property in the Rule No.{}: {}", ruleTemp.getId(), ruleTemp.getRuleDetail().getTimeout());
						logger.info("Defaul Timeout configured in the Rule No.{}: {}", ruleTemp.getId(), TIMEOUT);
					}
					logger.debug("Timeout set in the Rule No.{}: {} ms",ruleTemp.getId(),TIMEOUT);
					
					//VALIDATE DMN TABLE
					if(!ruleTemp.getRuleDetail().getDmn().getTable().equals("")
							&& !ruleTemp.getRuleDetail().getDmn().getSourceObject().equals("")
							&& !ruleTemp.getRuleDetail().getDmn().getInputColumn().equals("")
							&& !ruleTemp.getRuleDetail().getDmn().getOutputColum().equals("")
							&& !ruleTemp.getRuleDetail().getDmn().getRejectTx().equals("")) {
						
						//VALIDATE LOGICA ABOUT DMN
						Payload payloadTemp = validateDMN(ruleTemp, payload, json);
												
						if(payloadTemp == null) {//IF VALUE OF THE DMN OUPUT COLUMN IS EQUAL REJECT-TX VALUE
							continue;
						}else {
							payload = payloadTemp;
						}
						
					}else{
						logger.info("At least one property don't have a value. Verify the DMN properties in the rule");
						continue;
					}//END if(!ruleTemp.getRuleDetail().getDmn().getTable().equals("")) {
										
					//INVOKE THE OTHER SERVICE
					if(!ruleTemp.getRuleDetail().getPostHttpRest().equals("")) {
						logger.info("Invoking Post Http Rest...");
						new ResponseEntity<String>(callService(ruleTemp.getRuleDetail().getPostHttpRest(), payload), HttpStatus.OK);
					}else {
						logger.info("Not found PostHttpRest property in the rule");
						continue;
					}
					
					continue;
					
				}else{
					logger.debug("Initial filter 1 no validated");
				}//END if(_request.getOperationCode().equalsIgnoreCase(ruleTemp.get("operationCode"))
				
				if(payload.getRequest().getOperationCode().equalsIgnoreCase(ruleTemp.getRuleDetail().getInitialFilter().getOperationCode())
						&& payload.getRequest().getWalletId().equalsIgnoreCase(ruleTemp.getRuleDetail().getInitialFilter().getWalletId())
						&& payload.getRequest().getRejectionCode().equalsIgnoreCase(ruleTemp.getRuleDetail().getInitialFilter().getRejectionCode())) {
					
					logger.debug("Initial filter 2 validated");
					
					//GET TIMEOUT OF THE RULE
					if(ruleTemp.getRuleDetail().getTimeout() != null) {
						logger.debug("Timeout configured in the Rule No.{}: {} ms", ruleTemp.getId(), TIMEOUT);
						TIMEOUT = ruleTemp.getRuleDetail().getTimeout();
					}else {
						logger.info("Not found timeout property in the Rule No.{}: {}", ruleTemp.getId(), ruleTemp.getRuleDetail().getTimeout());
						logger.info("Defaul Timeout configured in the Rule No.{}: {}", ruleTemp.getId(), TIMEOUT);
					}
					
					//VALIDATE DMN TABLE
					if(!ruleTemp.getRuleDetail().getDmn().getTable().equals("")
							&& !ruleTemp.getRuleDetail().getDmn().getSourceObject().equals("")
							&& !ruleTemp.getRuleDetail().getDmn().getInputColumn().equals("")
							&& !ruleTemp.getRuleDetail().getDmn().getOutputColum().equals("")
							&& !ruleTemp.getRuleDetail().getDmn().getRejectTx().equals("")) {
						
						//VALIDATE LOGICA ABOUT DMN
						Payload payloadTemp = validateDMN(ruleTemp, payload, json);
						
						if(payloadTemp == null) {//IF VALUE OF THE DMN OUPUT COLUMN IS EQUAL REJECT-TX VALUE
							continue;
						}else {
							payload = payloadTemp;
						}
					}else{
						logger.info("At least one property don't have a value. Verify the DMN properties in the Rule");
						continue;
					}//END if(!ruleTemp.getRuleDetail().getDmn().getTable().equals("")) {
					
					//INVOKE THE OTHER SERVICE
					if(!ruleTemp.getRuleDetail().getPostHttpRest().equals("")) {
						logger.debug("Invoking Post Http Rest...");
						new ResponseEntity<String>(callService(ruleTemp.getRuleDetail().getPostHttpRest(), payload), HttpStatus.OK);
					}else {
						logger.info("Not found PostHttpRest property in the Rule");
						continue;
					}
					
					continue;
				}else {
					logger.debug("Initial filter 2 no validated");
				}//END if(payload.getRequest().getOperationCode().equalsIgnoreCase(ruleTemp.getRuleDetail().getInitialFilter().getOperationCode())
				
			}//END for(int i=0; i < ruleList.size(); i++) {
			
			ResponseEntity<String> response = new ResponseEntity<String>("{\"instanceID\":\""+transactionId+"\",\"code\":"+HTTP_SATUS_OK+"}", HttpStatus.OK);

			HttpStatus headers = response.getStatusCode();
	        Integer _statusCode = headers.value();
	        //GsLog.setLog(transactionId, "extCallStatus", _statusCode);
	        GsLog.setLog(transactionId, "statusCode", _statusCode);
	        
	        String responseBody = (String)response.getBody();
	        if (null != responseBody && !responseBody.isBlank()) {
	        	JSONObject _rest_response_obj = new JSONObject();
	            _rest_response_obj = JSONObject.fromObject(responseBody);
	            //boolean _string_res_check = _rest_response_obj.containsKey("*");
	            //logger.info("_string_res_check " + _string_res_check);
	            //JSONObject outputJson = new JSONObject();
	            //outputJson.put("responseMap", _rest_response_obj);
	            //_rest_response_obj = outputJson;
	            GsLog.setLog(transactionId, "response", _rest_response_obj);
	        }
	        
	        Long end = System.currentTimeMillis();
	        logger.debug("Total time: {}", (end-start) );
	        GsLog.setLog(transactionId, "timeTaken", (end-start));
			GsLog.sendLogs((String)transactionId);
	       			
			return CompletableFuture.completedFuture("{\"instanceID\":\""+transactionId+"\",\"code\":"+HTTP_SATUS_OK+"}");
			
		} catch (Exception e) {
			logger.info((String)e.getLocalizedMessage());
			JSONObject errorRes = GsCustomResponseMappingUtil.proccessException(ExceptionCode.EXCEPTION_CODE, (String)e.getLocalizedMessage());
            logData.sendLogs(transactionId, ExceptionCode.EXCEPTION_CODE, errorRes, start);
            throw e;
		}
		
	}
	
	/**
	 * 
	 * @param ruleTemp
	 * @param _payload
	 * @param _json
	 * @return
	 * @throws JsonProcessingException
	 */
	public Payload validateDMN(Rule ruleTemp, Payload _payload, ObjectNode _json) throws JsonProcessingException {
						
		//GET THE VALUE OF THE SOURCE OBJECT PROPERTY
		//IF THE PROPERTY DONT EXIST IN THE PLAYLOAD RETURN A EXCEPTION
		String valueSourceObject = _payload.getValueSourceObject(ruleTemp.getRuleDetail().getDmn().getSourceObject(), _json.toString());
		
		//GET INPUT JSON TO SEND DMN
		String dmnInputJson = _payload.getDmnInputJson(ruleTemp.getRuleDetail().getDmn().getInputColumn(), valueSourceObject);
		
		//CALL DMN AND GET RESPONSE FROM DMN
		String dmnResponse = callDMN(ruleTemp.getRuleDetail().getDmn().getTable(), dmnInputJson);
		
		if(dmnResponse != null) {//IF GET A RESPONSE FROM THE DMN
			
			//GET VALUE OF THE DMN OUTPUT COLUMN 
			String dmnOutputColumnValue = getDmnOutputColumnValue(dmnResponse,ruleTemp.getRuleDetail().getDmn().getOutputColum());
			
			
			//IF THE VALUE OF THE DMN OUTPUT COLUMN IS EQUAL THE REJECT TX
			if(dmnOutputColumnValue.equals(ruleTemp.getRuleDetail().getDmn().getRejectTx())) {
				logger.debug("The DMN's output value ("+dmnOutputColumnValue+") is equal a RejectTx ("+ruleTemp.getRuleDetail().getDmn().getRejectTx()+")");
				logger.debug("Exit of the rule and continue with the next rule \n");
				return null;
			}
			
			//SET THE ADDITIONAL INFORMATION
			AdditionalInformation additionalInformation = new AdditionalInformation();
			additionalInformation.setServiceName(ruleTemp.getRuleDetail().getApplicationName());
			
			ObjectMapper mapper = new ObjectMapper();
			DMNResponse dmnResponse1 = mapper.readValue(dmnResponse,DMNResponse.class);
			List<DMNValues> list = dmnResponse1.getResult();
			DMNValues dmnValues = (DMNValues)list.get(0);//THE INDEX IS ALLWAYS 0
			additionalInformation.setDmnValues(dmnValues);
			
			//ADD THE ADDITIONAL INFORMATION TO THE ORIGINAL PAYLOAD
			_payload.setAdditionalInformation(additionalInformation);
			logger.debug("Additional information added to the payload");
			
		}else{
			
			logger.info("Not get response from DMN");
		}//END if(dmnResponse != null) {
			
		return _payload;
	}
	
	/**
	 * 
	 * @param table
	 * @param jsonDMNInput
	 * @return
	 * @throws JsonProcessingException
	 */
	public String callDMN(String table, String jsonDMNInput) throws JsonProcessingException {
		
		Dmn dmnTable = new Dmn();
		
		String DMNUri = dmnTable.getUri(table);
	    logger.debug("DMN invoked: " + DMNUri);
	    logger.debug("DMN Input: " + jsonDMNInput);
		
	    //RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
	    RestTemplate restTemplate = new RestTemplateBuilder()
	            .setConnectTimeout(Duration.ofMillis(TIMEOUT))
	            .build();
	    
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    
	    HttpEntity<String> request = new HttpEntity<String>(jsonDMNInput, headers);
	    	    
	    String result = restTemplate.postForObject(DMNUri, request, String.class);
	    
	    logger.debug("DMN Response: " + result);
	    
	    return result;
	    
	}
	
	/**
	 * 
	 * @param dmnResponse
	 * @param dmnOutputColumn
	 * @return
	 */
	public String getDmnOutputColumnValue(String dmnResponse, String dmnOutputColumn) {
		DocumentContext context = JsonPath.parse(dmnResponse);
		String dmnOutputColumnValue  = context.read("$.result[0]."+dmnOutputColumn, String.class); //THE INDEX IS ALWAYS 0
		return dmnOutputColumnValue;
	}
	
	/**
	 * 
	 * @param httpRestUrl
	 * @param payload
	 * @return
	 * @throws JsonProcessingException
	 */
	public String callService(String httpRestUrl, Payload payload) throws JsonProcessingException {
		
		String uri = httpRestUrl;
	    
	    logger.debug("Http Rest Url invoked: {}", httpRestUrl);
		
	    ObjectMapper mapperPayload = new ObjectMapper();
	    String jsonString = mapperPayload.writeValueAsString(payload);
	    
	    logger.info("Payload sent to Http Rest Url: {}", jsonString);
	    
	    //RestTemplate restTemplate = new RestTemplate();
	    RestTemplate restTemplate = new RestTemplateBuilder()
	            .setConnectTimeout(Duration.ofMillis(TIMEOUT))
	            .build();
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    
	    HttpEntity<String> request = new HttpEntity<String>(jsonString, headers);
	    	    
	    String result = restTemplate.postForObject(uri, request, String.class);
	    logger.info("Http Rest Url response: {}", result);
	    
	    return result;
	    
	}
	
	/**
	 * 
	 * @param rule
	 * @return
	 * @throws Exception
	 */
	public boolean validateRule(Rule rule) throws Exception {
		String idRule = rule.getId();
		if(rule.validateRule(idRule)){
			if(rule.getRuleDetail().validateRule(idRule)) {
				if(rule.getRuleDetail().getInitialFilter().validateRule(idRule)) {
					if(rule.getRuleDetail().getDmn().validateRule(idRule)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}

	/**
	 * 
	 * @return
	 */
	public List<Rule> getRuleList() {
		return ruleList;
	}

	/**
	 * 
	 * @param ruleList
	 */
	public void setRuleList(List<Rule> ruleList) {
		this.ruleList = ruleList;
	}
		
}
