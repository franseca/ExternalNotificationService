package com.tecnotree.model;

public class ApplicationDetails {

	private String tenant;
	private String sender;
	private String userName;
	private String messageId;
	private String channel;
	private String requestedTime;
	
	public String getTenant() {
		return tenant;
	}
	public void setTenant(String tenant) {
		this.tenant = tenant;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getRequestedTime() {
		return requestedTime;
	}
	public void setRequestedTime(String requestedTime) {
		this.requestedTime = requestedTime;
	}
	
	
}
