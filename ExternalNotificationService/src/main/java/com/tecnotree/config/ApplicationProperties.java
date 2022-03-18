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
	
	@Value("${logging.level.com.tecnotree.rest}")
	private String logLevel;
	
	@Value("${service.timeout}")
	private long timeout;
	
	@Autowired
	private Environment env;
	
	@Value("${numRules}") //FROM PROPERTIES FILE
	private int numRules;
	
	public String get(String numRule) {
		return env.getProperty("RULE." + numRule);
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
	
	
	
}
