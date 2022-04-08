package com.tecnotree;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bson.Document;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.tecnotree.config.ApplicationProperties;
import com.tecnotree.model.Rule;
import com.tecnotree.node.db.mongo.config.EnMongoDBConfiguration;
import com.tecnotree.service.RuleService;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class ExternalNotificationServiceApplication implements CommandLineRunner{

	private final Logger logger = LoggerFactory.getLogger(ExternalNotificationServiceApplication.class);
	
	private int numRules = 0;
	
	@Autowired
	ApplicationProperties applicationProperties;
	
	@Autowired
    private RuleService ruleService;
	
	public static void main(String[] args) {
		SpringApplication.run(ExternalNotificationServiceApplication.class, args);
	}
	
	public void run(String... args) throws Exception {
	    Long start = Long.valueOf(System.currentTimeMillis());
	   
	    try {
		    //LOAD RULES CONFIGURED LIKE ENVIROMENT VARIABLES
			List<Rule> ruleList = new  ArrayList<Rule>();
			numRules = applicationProperties.getNumRules();
			logger.info("Total Rules configured: {}", numRules);
			
			if(numRules == 0) {
				logger.info("The NUM_RULES variable is equal 0.  The NUM_RULES variable must be greater than 0.");
	            throw new Exception("The NUM_RULES variable is equal 0.  The NUM_RULES variable must be greater than 0.");
			}
				
			for(int i = 1; i <= numRules; i++) {
				
				//GET RULE FROM ENVIROMENT VARIABLE AND CONVERTS IN JAVA OBJECT
				ObjectMapper mapperRule = new ObjectMapper();
				Rule rule = mapperRule.readValue(applicationProperties.get(String.valueOf(i)), Rule.class);
				
				//VALLIDATE THE RULE'S STRUCTURE
				ruleService.validateRule(rule);
				
				logger.info("Rule No.{}: {}", i, applicationProperties.get(String.valueOf(i)));
				ruleList.add(rule);
			}
			
			ruleService.setRuleList(ruleList);
			
			logger.info("Total Rules loaded: {}", numRules);
			
			logger.info("Loading DMN in memory...");
			
			//CONNECTION TO MONGO DB WITH SPRING-BOOT LIBRARIES
			EnMongoDBConfiguration.connectToMongoDB(applicationProperties.getMongodbHost(), applicationProperties.getMongodbPort(), applicationProperties.getMongodbDatabase(), applicationProperties.getMongodbUser(), applicationProperties.getMongodbPassword());
			MongoDatabase mongoDatabase = EnMongoDBConfiguration.mongodatabase;
			MongoCollection<Document> collection = null;
			collection = mongoDatabase.getCollection("DAP_DMN_REPO", Document.class);
			
			DmnDecision decisionDmn = null;
			HashMap<String,DmnDecision> dmnMap = new HashMap<String,DmnDecision>();
			for(int i = 0; i < ruleList.size(); i++) {
				Rule rule = (Rule)ruleList.get(i);
				String dmnName = rule.getRuleDetail().getDmn().getTable(); //DPA's DMN
				
				if(!dmnMap.containsKey(dmnName) && !dmnName.equals("")) {
					logger.debug("DMN {} isn't in memory", dmnName);
					
					BasicDBObject queryObject = new BasicDBObject("serviceName", dmnName);
					FindIterable<Document> response = collection.find(queryObject);
					logger.debug("Response From MongoDB is : " + response);
					MongoCursor<Document> cursor = response.iterator();
	
					while(cursor.hasNext()) {
						Document json = (Document)cursor.next();
						
						String resultDB = json.toJson();
						JSONObject jsonObject = new JSONObject(resultDB);
						String resultXml = jsonObject.getJSONArray("processDoc").getJSONObject(0).get("xml").toString();
						DmnEngine dmnEngine = DmnEngineConfiguration.createDefaultDmnEngineConfiguration().buildEngine();
			            InputStream inputStream = new ByteArrayInputStream(resultXml.getBytes());
			            List<DmnDecision> decisions = dmnEngine.parseDecisions(inputStream);
			            decisionDmn = decisions.get(0);
			            
			            dmnMap.put(dmnName, decisionDmn);
						
						logger.debug("DMN loaded: {}", dmnName);
					}
					
					//CLOSE CURSOR
					cursor.close();
							
				}else{
					logger.debug("DMN {} is in memory o is empty", dmnName);
				}//END if(!dmnMap.containsKey(dmnName)) {
				
			}//EDN for(int i = 0; i < ruleList.size(); i++) {
			
			//CLOSE CONNECTION TO MONGO DB
			EnMongoDBConfiguration.closeConnectionMongoDB();
						
			ruleService.setDmnMap(dmnMap);
			
			logger.info("Total DMN loaded in memory: {}", dmnMap.size());
									
	    }catch (Exception e) {
	    	logger.info("External Notidicacion Service failed to start: {} ms",(System.currentTimeMillis() - start));
            throw new Exception("Exception occured : " + e.getLocalizedMessage(), (Throwable)e);
        }
		
	}
	   

}
