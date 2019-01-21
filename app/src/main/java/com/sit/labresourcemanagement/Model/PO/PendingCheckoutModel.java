package com.sit.labresourcemanagement.Model.PO;

public class PendingCheckoutModel {
	String userId, assetNumber, assetDescription, dateFrom, dateTo, lockerId, assetId, loanId;
	Boolean locker;

	public PendingCheckoutModel(String userId, String assetNumber, String assetDescription, String dateFrom, String dateTo, String locker, String lockerId, String assetId, String loanId) {
		this.userId = userId;
		this.assetNumber = assetNumber;
		this.assetDescription = assetDescription;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.assetId = assetId;
		this.loanId = loanId;

		if(locker.equals("Yes")){
			this.locker = true;
			this.lockerId = lockerId;
		}
		else
			this.locker = false;
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

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public Boolean getLocker() {
		return locker;
	}

	public void setLocker(Boolean locker) {
		this.locker = locker;
	}

	public String getLockerId() {
		return lockerId;
	}

	public void setLockerId(String lockerId) {
		this.lockerId = lockerId;
	}

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public String getLoanId() {
		return loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}
}
