package com.tecnotree;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecnotree.config.ApplicationProperties;
import com.tecnotree.model.Rule;
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
			List<Rule> ruleList = new  ArrayList<>();
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
						
	    }catch (Exception e) {
	    	logger.info("External Notidicacion Service failed to start: {} ms",(System.currentTimeMillis() - start));
            throw new Exception("Exception occured : " + e.getLocalizedMessage(), (Throwable)e);
        }
		
	}
	   

}
