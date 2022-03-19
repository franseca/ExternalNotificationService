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
	
	public boolean validateRule(String rule) throws Exception {
		if(this.applicationName == null) {
			throw new Exception("Not found applicationName property in the rule No." + rule);
		}else if(this.applicationName.equals(""))
			throw new Exception("Not found a value in the applicationName property in the rule No." + rule);
		
		if(this.initialFilter == null) {
			throw new Exception("Not found initialFilter property in the rule No." + rule);
		}
		
		if(this.dmn == null) {
			throw new Exception("Not found dmn property in the rule No." + rule);
		}
		
		if(this.postHttpRest == null) {
			throw new Exception("Not found postHttpRest property in the rule No." + rule);
		}else if(this.postHttpRest.equals(""))
			throw new Exception("Not found a value in the postHttpRest property in the rule No." + rule);
		
		if(this.timeout == null) {
			throw new Exception("Not found timeout property in the rule No." + rule);
		}//SET A DEFAULT VALUE
		
		return true;
	}
	
}
