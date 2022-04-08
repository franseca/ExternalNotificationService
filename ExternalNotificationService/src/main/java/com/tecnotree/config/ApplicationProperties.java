package com.tecnotree.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class ApplicationProperties {
	
	@Value("${service.servicename}")
	private String serviceName;
	
	@Value("${logging.level.com.tecnotree}")
	private String logLevel;
	
	@Value("${service.timeout}")
	private long timeout;
	
	@Autowired
	private Environment env;
	
	@Value("${service.numRules}") //FROM PROPERTIES FILE
	private int numRules;
	
	@Value("${executor.corePoolSize}")
	private int corePoolSize;
	
	@Value("${executor.queueCapacity}")
	private int queueCapacity;
	
	@Value("${executor.maxPoolSize}")
	private int maxPoolSize;
	
	@Value("${service.mongodb.user}")
	private String mongodbUser;
	
	@Value("${service.mongodb.password}")
	private String mongodbPassword;
	
	@Value("${service.mongodb.host}")
	private String mongodbHost;
	
	@Value("${service.mongodb.port}")
	private String mongodbPort;
	
	@Value("${service.mongodb.database}")
	private String mongodbDatabase;
			
	public String get(String numRule) {
		return env.getProperty("RULE_" + numRule);
	}
	
	public int getNumRules() {
		return numRules;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public Environment getEnv() {
		return env;
	}

	public void setEnv(Environment env) {
		this.env = env;
	}

	public void setNumRules(int numRules) {
		this.numRules = numRules;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public int getQueueCapacity() {
		return queueCapacity;
	}

	public void setQueueCapacity(int queueCapacity) {
		this.queueCapacity = queueCapacity;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public String getMongodbUser() {
		return mongodbUser;
	}

	public void setMongodbUser(String mongodbUser) {
		this.mongodbUser = mongodbUser;
	}

	public String getMongodbPassword() {
		return mongodbPassword;
	}

	public void setMongodbPassword(String mongodbPassword) {
		this.mongodbPassword = mongodbPassword;
	}

	public String getMongodbHost() {
		return mongodbHost;
	}

	public void setMongodbHost(String mongodbHost) {
		this.mongodbHost = mongodbHost;
	}

	public String getMongodbPort() {
		return mongodbPort;
	}

	public void setMongodbPort(String mongodbPort) {
		this.mongodbPort = mongodbPort;
	}

	public String getMongodbDatabase() {
		return mongodbDatabase;
	}

	public void setMongodbDatabase(String mongodbDatabase) {
		this.mongodbDatabase = mongodbDatabase;
	}

	
}
