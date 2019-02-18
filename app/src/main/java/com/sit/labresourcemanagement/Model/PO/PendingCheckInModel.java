package com.sit.labresourcemanagement.Model.PO;

public class PendingCheckInModel {

	String returnId;
	String loanId;
	String userId;
	String assetNumber;
	String assetDescription;
	String inventoryId;
	String locker;
	String pin;

	public String getLocker() {
		return locker;
	}

	public void setLocker(String locker) {
		this.locker = locker;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}


	//String dateTime, location;

	public PendingCheckInModel( String userId, String assetNumber, String assetDescription,String loanId,String locker,String pin,String inventoryId) {
		//this.returnId = returnId;
		this.loanId = loanId;
		this.userId = userId;
		this.assetNumber = assetNumber;
		this.assetDescription = assetDescription;
		this.locker = locker;
		this.pin = pin;
		/*this.dateTime = dateTime;
		this.location = location;*/
		this.inventoryId = inventoryId;
	}

	public String getReturnId() {
		return returnId;
	}

	public void setReturnId(String returnId) {
		this.returnId = returnId;
	}

	public String getLoanId() {
		return loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
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

	/*public String getDateTime() {
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
	}*/

	public String getInventoryId() {
		return inventoryId;
	}

	public void setInventoryId(String inventoryId) {
		this.inventoryId = inventoryId;
	}
}

