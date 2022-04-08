package com.tecnotree.model;

public class InitialFilter {

	private String operationCode;
	private String rejectionCode;
	private String walletId;
	
	public String getOperationCode() {
		return operationCode;
	}
	public void setOperationCode(String operationCode) {
		this.operationCode = operationCode;
	}
	public String getRejectionCode() {
		return rejectionCode;
	}
	public void setRejectionCode(String rejectionCode) {
		this.rejectionCode = rejectionCode;
	}
	public String getWalletId() {
		return walletId;
	}
	public void setWalletId(String walletId) {
		this.walletId = walletId;
	}
	
	public boolean validateRule(String rule) throws Exception {
		if(this.operationCode == null) {
			throw new Exception("Not found operationCode property in the rule No." + rule);
		}else if(this.operationCode.equals(""))
			throw new Exception("Not found a value in the operationCode property in the rule No." + rule);
		
		
		if(this.rejectionCode == null) {
			throw new Exception("Not found rejectionCode property in the rule No." + rule);
		}else if(this.rejectionCode.equals(""))
			throw new Exception("Not found a value in the rejectionCode property in the rule No." + rule);
		
		
		/*if(this.walletId == null) {
			throw new Exception("Not found walletId property in the rule No." + rule);
		}else if(this.walletId.equals(""))
			throw new Exception("Not found a value in the walletId property in the rule No." + rule);
		*/
		
		return true;
		
	}
}
