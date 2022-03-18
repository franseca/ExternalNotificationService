package com.tecnotree.model;

public class RuleDetail {

	private String applicationName;
	private InitialFilter initialFilter;
	private Dmn dmn;
	//private String rejectTx;
	private String postHttpRest;
	private Long timeout;
	
	public String getApplicationName() {
		return applicationName;
	}
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	public InitialFilter getInitialFilter() {
		return initialFilter;
	}
	public void setInitialFilter(InitialFilter initialFilter) {
		this.initialFilter = initialFilter;
	}
	/*public String getRejectTx() {
		return rejectTx;
	}
	public void setRejectTx(String rejectTx) {
		this.rejectTx = rejectTx;
	}*/
	public String getPostHttpRest() {
		return postHttpRest;
	}
	public void setPostHttpRest(String postHttpRest) {
		this.postHttpRest = postHttpRest;
	}
	public Dmn getDmn() {
		return dmn;
	}
	public void setDmn(Dmn dmn) {
		this.dmn = dmn;
	}
	public Long getTimeout() {
		return timeout;
	}
	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}
	
	
}
