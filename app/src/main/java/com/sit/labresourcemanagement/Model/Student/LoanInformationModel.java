package com.sit.labresourcemanagement.Model.Student;

/**
 * Created by admin on 10/7/18.
 */

public class LoanInformationModel {


    String assetDescription,loanId,inventoryId,dateFrom,dateTo,status,reasonOfLoan,rejReason,poId,lockerRequest,lockerId;

    public LoanInformationModel(String assetDescription, String loanId , String inventoryId, String dateFrom , String dateTo,String status, String reasonOfLoan,String rejReason , String poId, String lockerRequest,String lockerId){
        this.assetDescription = assetDescription;
        this.loanId = loanId;
        this.inventoryId = inventoryId;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.status = status;
        this.reasonOfLoan = reasonOfLoan;
        this.rejReason = rejReason;
        this.poId = poId;
        this.lockerRequest = lockerRequest;
        this.lockerId = lockerId;
    }

    public String getassetDescription(){
        return this.assetDescription;
    }

    public String getloanId(){
        return this.loanId;
    }

    public String getinventoryId(){
        return this.inventoryId;
    }

    public String getdateFrom(){
        return this.dateFrom;
    }

    public String getdateTo(){
        return this.dateTo;
    }

    public String getStatus(){
        return this.status;
    }

    public String getReasonOfLoan(){
        return this.reasonOfLoan;
    }

    public String getReasonReject(){
        return this.rejReason;
    }

    public String getPoId(){
        return this.poId;
    }

    public String getlockerRequest(){
        return this.lockerRequest;
    }

    public String getlockerId(){
        return this.lockerId;
    }

}
