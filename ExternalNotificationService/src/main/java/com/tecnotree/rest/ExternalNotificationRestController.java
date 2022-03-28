package com.tecnotree.rest;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.tecnotree.config.ApplicationProperties;
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
import com.tecnotree.service.RuleService;

import net.sf.json.JSONObject;

/**
 * @author cevalfr
 *
 */
@RestController
@RequestMapping("/api/tecnotree")
public class ExternalNotificationRestController {
	
	private final Logger logger = LoggerFactory.getLogger(ExternalNotificationRestController.class);
	
	Payload payload;
		
	private long TIMEOUT = 7000; // DEFAULT TIME OUT (7 SECONDS) 
	private String HTTP_SATUS_OK = "200";
	
	@Autowired
	ApplicationProperties applicationProperties;
	
	@Autowired
    private RuleService ruleService;
		
	/*@PostMapping(value="/validate")
	public ResponseEntity<String> validate(@RequestBody ObjectNode json) throws Exception{
		
		Long _start = System.currentTimeMillis();
		
		//SET LOG INITIAL INFORMATION
		SetLogData logData = new SetLogData();
		String transactionId;
		transactionId = logData.setLogInfo(applicationProperties.getServiceName(), applicationProperties.getLogLevel(), json.toString());
		
		try {
			logger.info("Timeout default: " + TIMEOUT + " ms");
			logger.info("TransactionId ID: " + transactionId);
	        logger.info("Payload received: " + json.toString());
	        
			//LOAD RULES CONFIGURED LIKE ENVIROMENT VARIABLES
			List<Rule> ruleList = new  ArrayList<>();
			int numRules = applicationProperties.getNumRules();
			logger.info("Total rules configured: " + numRules);
			
			for(int i = 1; i <= numRules; i++) {
				
				//GET RULE FROM ENVIROMENT VARIABLE AND CONVERTS IN JAVA OBJECT
				ObjectMapper mapperRule = new ObjectMapper();
				Rule rule = mapperRule.readValue(applicationProperties.get(String.valueOf(i)), Rule.class);
				
				//VALLIDATE THE RULE'S STRUCTURE
				validateRule(rule);
				
				//logger.info("Rule No." + i + " loaded.");
				logger.info("Rule No." + i + ": " + applicationProperties.get(String.valueOf(i)));
				ruleList.add(rule);
			}
			
			for(int i=0; i < ruleList.size(); i++) {
				Rule ruleTemp = (Rule)ruleList.get(i);
				
				logger.info("Rule No." + ruleTemp.getId() + " validating...");
				
				//GET PAYLOAD SENT
				ObjectMapper mapperPayload = new ObjectMapper();
				//mapperPayload.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				Payload payload = mapperPayload.readValue(json.toString(),Payload.class);
				
				if(ruleTemp.getRuleDetail().getApplicationName() == null) {
					logger.info("Not found applicationName property in the Rule No." + ruleTemp.getId() + ": " + ruleTemp.getRuleDetail().getApplicationName());
					throw new Exception("Not found applicationName property in the Rule No." + ruleTemp.getId() + ": " + ruleTemp.getRuleDetail().getApplicationName());
				}
				
				//VALIDATE INITIAL FILTER
				if(payload.getRequest().getOperationCode().equalsIgnoreCase(ruleTemp.getRuleDetail().getInitialFilter().getOperationCode())
						&& payload.getRequest().getRejectionCode().equalsIgnoreCase(ruleTemp.getRuleDetail().getInitialFilter().getRejectionCode())) {
						
					logger.info("Initial filter 1 validated");
					
					//GET TIMEOUT OF THE RULE
					if(ruleTemp.getRuleDetail().getTimeout() != null) {
						logger.info("Timeout configured in the rule "+ ruleTemp.getId() + ": " + TIMEOUT + " ms");
						TIMEOUT = ruleTemp.getRuleDetail().getTimeout();
					}else {
						logger.info("Not found timeout property in the Rule No." + ruleTemp.getId() + ": " + ruleTemp.getRuleDetail().getTimeout());
						logger.info("Defaul Timeout configured in the Rule No." + ruleTemp.getId() + ": " + TIMEOUT  + " ms");
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
					logger.info("Initial filter 1 no validated");
				}//END if(_request.getOperationCode().equalsIgnoreCase(ruleTemp.get("operationCode"))
				
				if(payload.getRequest().getOperationCode().equalsIgnoreCase(ruleTemp.getRuleDetail().getInitialFilter().getOperationCode())
						&& payload.getRequest().getWalletId().equalsIgnoreCase(ruleTemp.getRuleDetail().getInitialFilter().getWalletId())
						&& payload.getRequest().getRejectionCode().equalsIgnoreCase(ruleTemp.getRuleDetail().getInitialFilter().getRejectionCode())) {
					
					logger.info("Initial filter 2 validated");
					
					//GET TIMEOUT OF THE RULE
					if(ruleTemp.getRuleDetail().getTimeout() != null) {
						logger.info("Timeout configured in the rule "+ ruleTemp.getId() + ": " + TIMEOUT + " ms");
						TIMEOUT = ruleTemp.getRuleDetail().getTimeout();
					}else {
						logger.info("Not found timeout property in the Rule No." + ruleTemp.getId() + ": " + ruleTemp.getRuleDetail().getTimeout());
						logger.info("Defaul Timeout configured in the Rule No." + ruleTemp.getId() + ": " + TIMEOUT  + " ms");
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
				}else {
					logger.info("Initial filter 2 no validated");
				}//END if(payload.getRequest().getOperationCode().equalsIgnoreCase(ruleTemp.getRuleDetail().getInitialFilter().getOperationCode())
				
			}//END for(int i=0; i < ruleList.size(); i++) {
			
			GsLog.setLog(transactionId, "timeTaken", (System.currentTimeMillis() - _start));
			ResponseEntity<String> response = new ResponseEntity<>("{\"instanceID\":\""+transactionId+"\",\"code\":"+HTTP_SATUS_OK+"}", HttpStatus.OK);

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
	        
	        GsLog.sendLogs((String)transactionId);
	        
			return response;
			
		} catch (Exception e) {
			logger.info((String)e.getLocalizedMessage());
			JSONObject errorRes = GsCustomResponseMappingUtil.proccessException(ExceptionCode.EXCEPTION_CODE, (String)e.getLocalizedMessage());
            logData.sendLogs(transactionId, ExceptionCode.EXCEPTION_CODE, errorRes, _start);
            throw e;
		}
	}*/
	
	@PostMapping(value="/validate")
	public ResponseEntity<String> validate(@RequestBody ObjectNode json) throws Exception{
		
		/*Long _start = System.currentTimeMillis();
		
		//SET LOG INITIAL INFORMATION
		SetLogData logData = new SetLogData();
		String transactionId;
		transactionId = logData.setLogInfo(applicationProperties.getServiceName(), applicationProperties.getLogLevel(), json.toString());
		
		try {*/
		
			CompletableFuture<String> cf = ruleService.validate2(json);
			ResponseEntity<String> response = new ResponseEntity<>(cf.get(), HttpStatus.OK);

			
			/*GsLog.setLog(transactionId, "timeTaken", (System.currentTimeMillis() - _start));
			ResponseEntity<String> response = new ResponseEntity<>("{\"instanceID\":\""+transactionId+"\",\"code\":"+HTTP_SATUS_OK+"}", HttpStatus.OK);

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
	        
	        GsLog.sendLogs((String)transactionId);
	        
			return response;*/
			
			//return ResponseEntity.status(HttpStatus.OK).build();
			

	        
	        
			return response;
			
		/*} catch (Exception e) {
			logger.info((String)e.getLocalizedMessage());
			JSONObject errorRes = GsCustomResponseMappingUtil.proccessException(ExceptionCode.EXCEPTION_CODE, (String)e.getLocalizedMessage());
            logData.sendLogs(transactionId, ExceptionCode.EXCEPTION_CODE, errorRes, _start);
            throw e;
		}*/
	}
	
	
	/**
	 * @param httpRestUrl
	 * @param payload
	 * @return
	 * @throws JsonProcessingException
	 */
	public String callService(String httpRestUrl, Payload payload) throws JsonProcessingException {
		
		String uri = httpRestUrl;
	    
	    logger.info("Http Rest Url invoked: " + httpRestUrl);
		
	    ObjectMapper mapperPayload = new ObjectMapper();
	    String jsonString = mapperPayload.writeValueAsString(payload);
	    
	    logger.info("Payload sent: " + jsonString);
	    
	    //RestTemplate restTemplate = new RestTemplate();
	    RestTemplate restTemplate = new RestTemplateBuilder()
	            .setConnectTimeout(Duration.ofMillis(TIMEOUT))
	            .build();
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    
	    HttpEntity<String> request = new HttpEntity<String>(jsonString, headers);
	    	    
	    String result = restTemplate.postForObject(uri, request, String.class);
	    logger.info("Response: " + result + "\n");
	    
	    return result;
	    
	}
	
	/**
	 * @param table
	 * @param jsonDMNInput
	 * @return
	 * @throws JsonProcessingException
	 */
	public String callDMN(String table, String jsonDMNInput) throws JsonProcessingException {
		
		Dmn dmnTable = new Dmn();
		
		String DMNUri = dmnTable.getUri(table);
	    logger.info("DMN invoked: " + DMNUri);
	    logger.info("DMN Input: " + jsonDMNInput);
		
	    //RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
	    RestTemplate restTemplate = new RestTemplateBuilder()
	            .setConnectTimeout(Duration.ofMillis(TIMEOUT))
	            .build();
	    
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    
	    HttpEntity<String> request = new HttpEntity<String>(jsonDMNInput, headers);
	    	    
	    String result = restTemplate.postForObject(DMNUri, request, String.class);
	    
	    logger.info("DMN Response: " + result);
	    
	    return result;
	    
	}
	
	/**
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
				logger.info("The DMN's output value ("+dmnOutputColumnValue+") is equal a RejectTx ("+ruleTemp.getRuleDetail().getDmn().getRejectTx()+")");
				logger.info("Exit of the rule and continue with the next rule \n");
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
			logger.info("Additional information added to the payload");
			
		}else{
			
			logger.info("Not get response from DMN");
		}//END if(dmnResponse != null) {
			
		return _payload;
	}
	
	/*public boolean validateRule(Rule rule) throws Exception {
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
	}*/
	/********************** TEST METHODS ****************************************************/
	
	/*private HttpComponentsClientHttpRequestFactory getClientHttpRequestFactory() 
	{
	    HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
	                      = new HttpComponentsClientHttpRequestFactory();
	    //Connect timeout
	    clientHttpRequestFactory.setConnectTimeout(10_000);
	     
	    //Read timeout
	    clientHttpRequestFactory.setReadTimeout(10_000);
	    return clientHttpRequestFactory;
	}
	
	public String validateExistSourceObject(ObjectNode objectNode, String sourceObject) throws IOException {
		String[] sourceObjects = descomposeSourceObject(sourceObject);
		
		JsonNode rootNode = new ObjectMapper().readTree(new StringReader(objectNode.toString()));
		JsonNode a = rootNode.at(sourceObject);
		
		for(int i = 0; i < sourceObjects.length; i++) {
			String jsonProperty = sourceObjects[i];
			JsonNode innerNode = rootNode.get(jsonProperty);
			if(innerNode == null){
				return "The " + jsonProperty + " property doesn't exist.";
			}else {
				//existe 
			}
			
		}
		
		return "";
	    
	}

	public String[] descomposeSourceObject(String sourceObject) {
		String[] sourceObjects = sourceObject.split("\\.");
		return sourceObjects;
	}
	
	//vallida su existe la estructura dentro del paylod de ingreso
	public String validateExistSourceObject2(ObjectNode objectNode, String sourceObject) throws IOException {
		boolean exist = true;
		String valuePropery = "";
		String[] sourceObjects = descomposeSourceObject(sourceObject);
		
		ObjectMapper mapperPayload = new ObjectMapper();
		Map map = mapperPayload.readValue(objectNode.toString(),Map.class);
		
		for(int i = 0; i < sourceObjects.length; i++) {
			String jsonProperty = sourceObjects[i];
			
			exist = map.containsKey(jsonProperty);
			if(exist) { //IF THE PROPERTY EXISTS
								
				if(map.get(jsonProperty) instanceof Map)
					
					map = (Map)map.get(jsonProperty);
				
				else 
					valuePropery = (String)map.get(jsonProperty);
				
			}else{
				return valuePropery;
			}
			
		}
		
	    return valuePropery;
	}
	
	public void processJson(String jsonStr) {
	    ObjectMapper objectMapper = new ObjectMapper();

	    try {
	        JsonNode node = objectMapper.readTree(jsonStr);  
	        boolean first = true;
	        processNode(node);

	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	}

	/////////////
	private void processNode(JsonNode n) {
		String location = "", lastKey = "";
		int propertyCount = 0;
		
	    if (n.isContainerNode()) {
	        if (n.isArray()){
	            Iterator<JsonNode> itt = n.iterator();
	            while (itt.hasNext()) {
	                JsonNode innerNode = itt.next();
	                processNode(innerNode);
	            }
	        }
	        else {
	            Iterator<Map.Entry<String,JsonNode>> fieldsIterator = n.fields();
	            Map.Entry<String,JsonNode> field;               
	            while (fieldsIterator.hasNext()){
	                field = fieldsIterator.next();
	                lastKey = field.getKey();      
	                location += "/" + lastKey;
	                processNode(field.getValue());
	            }
	        }       
	    }
	    else if (n.isNull()) {
	        propertyCount++;
	        logger.info("Key: " + lastKey + " Value: " + n);
	    } else {
	        propertyCount++;
	        location = location.substring(0,location.lastIndexOf("/"));

	        logger.info("Key: " + lastKey  + " Value: " + n.asText());          
	    }
	}*/
}
