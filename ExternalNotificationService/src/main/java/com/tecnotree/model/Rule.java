package com.tecnotree.model;

public class Rule {

	private String id;
	private RuleDetail ruleDetail;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public RuleDetail getRuleDetail() {
		return ruleDetail;
	}
	public void setRuleDetail(RuleDetail ruleDetail) {
		this.ruleDetail = ruleDetail;
	}
	
	public boolean validateRule(String rule) throws Exception {
		
		if(this.id == null) {
			throw new Exception("Not found id property in the rule No." + rule);
		}else if(this.id.equals(""))
			throw new Exception("Not found a value in the id property in the rule No." + rule);
		
		if(this.ruleDetail == null) {
			throw new Exception("Not found ruleDetail property in the rule No." + rule);
		}
		
		return true;
		
	}
	
}
