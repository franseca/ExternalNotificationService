package com.tecnotree.rest;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tecnotree.config.ApplicationProperties;
import com.tecnotree.model.Payload;
import com.tecnotree.service.RuleService;

/**
 * @author cevalfr
 *
 */
@RestController
@RequestMapping("/api/tecnotree")
public class ExternalNotificationRestController {
	
	private final Logger logger = LoggerFactory.getLogger(ExternalNotificationRestController.class);
	
	Payload payload;
		
	@Autowired
	ApplicationProperties applicationProperties;
	
	@Autowired
    private RuleService ruleService;
	
	@PostMapping(value="/validate")
	public ResponseEntity<String> validate(@RequestBody ObjectNode json) throws Exception{
		
		logger.debug("Calling method validate...");
		
		CompletableFuture<String> cf = ruleService.validate(json);
		ResponseEntity<String> response = new ResponseEntity<String>(cf.get(), HttpStatus.OK);

		return response;
	
	}
	
}
