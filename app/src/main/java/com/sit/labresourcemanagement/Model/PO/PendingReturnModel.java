package com.sit.labresourcemanagement.Model.PO;

public class PendingReturnModel {

	String userId, assetNumber, assetDescription, dateTime, location, loanId, returnId;

	public PendingReturnModel(String userId, String assetNumber, String assetDescription, String dateTime, String location, String loanId, String returnId) {
		this.userId = userId;
		this.assetNumber = assetNumber;
		this.assetDescription = assetDescription;
		this.dateTime = dateTime;
		this.location = location;
		this.loanId = loanId;
		this.returnId = returnId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAssetNumber() {
		return assetNumber;
	}

	public void setAssetNumber(String assetNumber) {
		this.assetNumber = assetNumber;
	}

	public String getAssetDescription() {
		return assetDescription;
	}

	public void setAssetDescription(String assetDescription) {
		this.assetDescription = assetDescription;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLoanId() {
		return loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}

	public String getReturnId() {
		return returnId;
	}

	public void setReturnId(String returnId) {
		this.returnId = returnId;
	}
}
