package com.sit.labresourcemanagement.Model.Student;

public class StudentReturnModel {

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getAssetDescription() {
        return assetDescription;
    }

    public void setAssetDescription(String assetDescription) {
        this.assetDescription = assetDescription;
    }

/*    public String getLookerId() {
        return lookerId;
    }

    public void setLookerId(String lookerId) {
        this.lookerId = lookerId;
    }*/

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

    String userId;

    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getLockerId() {
        return lockerId;
    }

    public void setLockerId(String lockerId) {
        this.lockerId = lockerId;
    }

    String inventoryId;
    String assetDescription;
    String lockerId;
    String loanId;
    String returnId;

    public String getPoId() {
        return PoId;
    }

    public void setPoId(String poId) {
        PoId = poId;
    }

    String PoId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String status;

    public StudentReturnModel(String loanId,String userId, String inventoryId,String Poid, String status) {
        this.loanId = loanId;
        this.userId = userId;
        this.inventoryId = inventoryId;
        this.PoId = Poid;
        this.status = status;
        //this.lookerId = lookerId;




    }


}
