package com.tecnotree.config;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(AsyncConfiguration.class);
    
    @Autowired
	ApplicationProperties applicationProperties;
    
    @Bean (name = "taskExecutor")
    public Executor taskExecutor() {
    	logger.debug("Enter: taskExecutor");
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(applicationProperties.getCorePoolSize()); 
        executor.setQueueCapacity(applicationProperties.getQueueCapacity()); 
        executor.setMaxPoolSize(applicationProperties.getMaxPoolSize()); 
        logger.debug("CorePoolSize: {}", applicationProperties.getCorePoolSize());
        logger.debug("QueueCapacity: {}", applicationProperties.getQueueCapacity());
        logger.debug("MaxPoolSize: {}", applicationProperties.getMaxPoolSize());
        executor.setThreadNamePrefix("ExtNotThread-");
        executor.initialize();
        logger.debug("Exit: taskExecutor");
        
        return executor;
    }

}