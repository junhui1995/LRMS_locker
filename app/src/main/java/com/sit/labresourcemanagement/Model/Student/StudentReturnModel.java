package com.sit.labresourcemanagement.Model.Student;

public class StudentReturnModel {

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

    public String getLookerId() {
        return lookerId;
    }

    public void setLookerId(String lookerId) {
        this.lookerId = lookerId;
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

    String userId;
    String assetNumber;
    String assetDescription;
    String lookerId;
    String loanId;
    String returnId;

    public StudentReturnModel(String loanId,String userId, String assetNumber, String assetDescription, String lookerId,  String returnId) {
        this.userId = userId;
        this.assetNumber = assetNumber;
        this.assetDescription = assetDescription;
        this.lookerId = lookerId;
        this.loanId = loanId;
        this.returnId = returnId;
    }


}
