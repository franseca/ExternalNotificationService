package com.tecnotree;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class ExternalNotificationServiceApplication {

	/*@Autowired
	ApplicationProperties applicationProperties;*/
	
	public static void main(String[] args) {
		SpringApplication.run(ExternalNotificationServiceApplication.class, args);
	}
	
	/*public void run(String... args) throws Exception {
	    Long start = Long.valueOf(System.currentTimeMillis());
	    System.out.println(this.applicationProperties.getNumRules());
	}*/
	   

}
