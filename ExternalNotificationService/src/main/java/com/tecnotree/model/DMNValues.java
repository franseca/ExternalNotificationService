package com.tecnotree.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class DMNValues {

	private String duration;
	private String bono;
	private String mplayDuration;
	private String serviceName;
	private String rtdEnable;
	private String subscriberCategory;
	private String userType;
	private String profile;
	private String offerId;
	private String productType;
	private String uniqueABESBTrigger;
	private String uniqueABSMSTrigger;
	private String isEnabled;
	
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getBono() {
		return bono;
	}
	public void setBono(String bono) {
		this.bono = bono;
	}
	public String getMplayDuration() {
		return mplayDuration;
	}
	public void setMplayDuration(String mplayDuration) {
		this.mplayDuration = mplayDuration;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getRtdEnable() {
		return rtdEnable;
	}
	public void setRtdEnable(String rtdEnable) {
		this.rtdEnable = rtdEnable;
	}
	public String getSubscriberCategory() {
		return subscriberCategory;
	}
	public void setSubscriberCategory(String subscriberCategory) {
		this.subscriberCategory = subscriberCategory;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	public String getOfferId() {
		return offerId;
	}
	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public String getUniqueABESBTrigger() {
		return uniqueABESBTrigger;
	}
	public void setUniqueABESBTrigger(String uniqueABESBTrigger) {
		this.uniqueABESBTrigger = uniqueABESBTrigger;
	}
	public String getUniqueABSMSTrigger() {
		return uniqueABSMSTrigger;
	}
	public void setUniqueABSMSTrigger(String uniqueABSMSTrigger) {
		this.uniqueABSMSTrigger = uniqueABSMSTrigger;
	}
	public String getIsEnabled() {
		return isEnabled;
	}
	public void setIsEnabled(String isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	
	
}
