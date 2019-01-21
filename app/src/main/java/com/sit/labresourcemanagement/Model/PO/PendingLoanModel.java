package com.sit.labresourcemanagement.Model.PO;

public class PendingLoanModel {
    String userId_pendingApprov, assetDesc_pendingApprov, dateFrom_pendingApprov, dateTo_pendingApprov, reason_pendingApprov, status_pendingApprov,
			lockerRequest_pendingApprov, loanid_pendingApprov, inventoryid_pendingApprov, assetNo_pendingApprov;

	public PendingLoanModel(String userId_pendingApprov, String assetDesc_pendingApprov, String dateFrom_pendingApprov, String dateTo_pendingApprov, String reason_pendingApprov, String status_pendingApprov, String lockerRequest_pendingApprov, String loanid_pendingApprov, String inventoryid_pendingApprov, String assetNo_pendingApprov) {
		this.userId_pendingApprov = userId_pendingApprov;
		this.assetDesc_pendingApprov = assetDesc_pendingApprov;
		this.dateFrom_pendingApprov = dateFrom_pendingApprov;
		this.dateTo_pendingApprov = dateTo_pendingApprov;
		this.reason_pendingApprov = reason_pendingApprov;
		this.status_pendingApprov = status_pendingApprov;
		this.lockerRequest_pendingApprov = lockerRequest_pendingApprov;
		this.loanid_pendingApprov = loanid_pendingApprov;
		this.inventoryid_pendingApprov = inventoryid_pendingApprov;
		this.assetNo_pendingApprov = assetNo_pendingApprov;
	}

	public String getUserId_pendingApprov() {
		return userId_pendingApprov;
	}

	public void setUserId_pendingApprov(String userId_pendingApprov) {
		this.userId_pendingApprov = userId_pendingApprov;
	}

	public String getAssetDesc_pendingApprov() {
		return assetDesc_pendingApprov;
	}

	public void setAssetDesc_pendingApprov(String assetDesc_pendingApprov) {
		this.assetDesc_pendingApprov = assetDesc_pendingApprov;
	}

	public String getDateFrom_pendingApprov() {
		return dateFrom_pendingApprov;
	}

	public void setDateFrom_pendingApprov(String dateFrom_pendingApprov) {
		this.dateFrom_pendingApprov = dateFrom_pendingApprov;
	}

	public String getDateTo_pendingApprov() {
		return dateTo_pendingApprov;
	}

	public void setDateTo_pendingApprov(String dateTo_pendingApprov) {
		this.dateTo_pendingApprov = dateTo_pendingApprov;
	}

	public String getReason_pendingApprov() {
		return reason_pendingApprov;
	}

	public void setReason_pendingApprov(String reason_pendingApprov) {
		this.reason_pendingApprov = reason_pendingApprov;
	}

	public String getStatus_pendingApprov() {
		return status_pendingApprov;
	}

	public void setStatus_pendingApprov(String status_pendingApprov) {
		this.status_pendingApprov = status_pendingApprov;
	}

	public String getLockerRequest_pendingApprov() {
		return lockerRequest_pendingApprov;
	}

	public void setLockerRequest_pendingApprov(String lockerRequest_pendingApprov) {
		this.lockerRequest_pendingApprov = lockerRequest_pendingApprov;
	}

	public String getLoanid_pendingApprov() {
		return loanid_pendingApprov;
	}

	public void setLoanid_pendingApprov(String loanid_pendingApprov) {
		this.loanid_pendingApprov = loanid_pendingApprov;
	}

	public String getInventoryid_pendingApprov() {
		return inventoryid_pendingApprov;
	}

	public void setInventoryid_pendingApprov(String inventoryid_pendingApprov) {
		this.inventoryid_pendingApprov = inventoryid_pendingApprov;
	}

	public String getAssetNo_pendingApprov() {
		return assetNo_pendingApprov;
	}

	public void setAssetNo_pendingApprov(String assetNo_pendingApprov) {
		this.assetNo_pendingApprov = assetNo_pendingApprov;
	}
}
