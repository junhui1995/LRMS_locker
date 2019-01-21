package com.sit.labresourcemanagement.Model.Student;

/**
 * Created by admin on 6/6/18.
 */

public class StudentReturnRequestModel {

    private String loanId,time,place,status,rejReason,poId,remarks,assetDescription;

    public StudentReturnRequestModel(String loanId, String time ,String place ,String status, String rejReason,String poId,String remarks ,String assetDescription) {
        this.loanId = loanId;
        this.time = time;
        this.place = place;
        this.status = status;
        this.rejReason = rejReason;
        this.poId=poId;
        this.remarks= remarks;
        this.assetDescription = assetDescription;
    }

    public String getLoanId() {
        return loanId;
    }

    public String getTime() {
        return time;
    }

    public String getPlace() {
        return place;
    }

    public String getStatus() {
        return status;
    }

    public String getRejReason() {
        return rejReason;
    }

    public String getPoId() {
        return poId;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getAssetDescription(){
        return assetDescription;
    }

}
