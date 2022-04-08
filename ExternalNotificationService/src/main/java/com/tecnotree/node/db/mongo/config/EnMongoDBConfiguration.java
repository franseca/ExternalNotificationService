/*
 * Decompiled with CFR 0.137.
 * 
 * Could not load the following classes:
 *  com.mongodb.ConnectionString
 *  com.mongodb.MongoClientSettings
 *  com.mongodb.MongoClientSettings$Builder
 *  com.mongodb.client.MongoClient
 *  com.mongodb.client.MongoClients
 *  com.mongodb.client.MongoDatabase
 *  com.tecnotree.dap.node.action.db.mongo.config.GsMongoDBConfiguration
 *  com.tecnotree.dap.node.action.db.mysql.config.GenericDecryption
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.tecnotree.node.db.mongo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class EnMongoDBConfiguration {
    private static final Logger _log = LoggerFactory.getLogger(EnMongoDBConfiguration.class);
    private static MongoClient mongoClient = null;
    public static MongoDatabase mongodatabase = null;
        
    public static void connectToMongoDB(String host, String port, String databaseName, String user, String password) throws Exception {
    	_log.info("Start EnMongoDBConfiguration.class : connectToMongoDB()");
    	String uri = "mongodb://"+user+":"+password+"@"+host+":"+port+"/"+databaseName;
    	if (_log.isDebugEnabled()) {
    		_log.debug("Host: {} dbName: {} port: {}",host, databaseName, port);
        }
    	
    	_log.info("Connecting to Mongo... : " + uri);
        ConnectionString connString = new ConnectionString(uri);
        MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(connString).retryWrites(true).build();
        mongoClient = MongoClients.create((MongoClientSettings)settings);
        mongodatabase = mongoClient.getDatabase(databaseName);
        
        if (_log.isDebugEnabled()) {
        	_log.info("DatabaseName: {}", databaseName);
        	_log.debug("Connected To Mongo Database - host: {} port: {}", host, port);
        }
    	
    }
    
    public static void closeConnectionMongoDB() throws Exception {
    	mongoClient.close();
    }
}

