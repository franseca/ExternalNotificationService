package com.tecnotree.config;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(AsyncConfiguration.class);

    @Bean (name = "taskExecutor")
    public Executor taskExecutor() {
    	logger.debug("Enter: taskExecutor");
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(30); 
        executor.setQueueCapacity(300); 
        executor.setMaxPoolSize(40); 
        executor.setThreadNamePrefix("ExtNotThread-");
        executor.initialize();
        logger.debug("Exit: taskExecutor");
        
        return executor;
    }

}