package com.sit.labresourcemanagement.Model.Student;

/**
 * Created by admin on 6/6/18.
 */

public class StudentHistoryLoanModel {

    String assetname_history,date_from_history,date_to_history,loanId_history,inventoryid_history,reason_history,approval_status_history,loan_status_history,faculty_history,message1_history,poId_history,checkout_status_history,returnedPO;

    public StudentHistoryLoanModel(String assetname_history, String date_from_history, String date_to_history, String loanId_history, String inventoryid_history, String reason_history, String approval_status_history, String loan_status_history, String faculty_history, String message1_history, String poId_history, String checkout_status_history, String returnedPO) {
        this.assetname_history = assetname_history;
        this.date_from_history = date_from_history;
        this.date_to_history = date_to_history;
        this.loanId_history = loanId_history;
        this.inventoryid_history = inventoryid_history;
        this.reason_history = reason_history;
        this.approval_status_history = approval_status_history;
        this.loan_status_history = loan_status_history;
        this.faculty_history = faculty_history;
        this.message1_history = message1_history;
        this.poId_history = poId_history;
        this.checkout_status_history = checkout_status_history;
        this.returnedPO = returnedPO;
    }

    public String getAssetname_history() {
        return assetname_history;
    }

    public void setAssetname_history(String assetname_history) {
        this.assetname_history = assetname_history;
    }

    public String getDate_from_history() {
        return date_from_history;
    }

    public void setDate_from_history(String date_from_history) {
        this.date_from_history = date_from_history;
    }

    public String getDate_to_history() {
        return date_to_history;
    }

    public void setDate_to_history(String date_to_history) {
        this.date_to_history = date_to_history;
    }

    public String getLoanId_history() {
        return loanId_history;
    }

    public void setLoanId_history(String loanId_history) {
        this.loanId_history = loanId_history;
    }

    public String getInventoryid_history() {
        return inventoryid_history;
    }

    public void setInventoryid_history(String inventoryid_history) {
        this.inventoryid_history = inventoryid_history;
    }

    public String getReason_history() {
        return reason_history;
    }

    public void setReason_history(String reason_history) {
        this.reason_history = reason_history;
    }

    public String getApproval_status_history() {
        return approval_status_history;
    }

    public void setApproval_status_history(String approval_status_history) {
        this.approval_status_history = approval_status_history;
    }

    public String getLoan_status_history() {
        return loan_status_history;
    }

    public void setLoan_status_history(String loan_status_history) {
        this.loan_status_history = loan_status_history;
    }

    public String getFaculty_history() {
        return faculty_history;
    }

    public void setFaculty_history(String faculty_history) {
        this.faculty_history = faculty_history;
    }

    public String getMessage1_history() {
        return message1_history;
    }

    public void setMessage1_history(String message1_history) {
        this.message1_history = message1_history;
    }

    public String getPoId_history() {
        return poId_history;
    }

    public void setPoId_history(String poId_history) {
        this.poId_history = poId_history;
    }

    public String getCheckout_status_history() {
        return checkout_status_history;
    }

    public void setCheckout_status_history(String checkout_status_history) {
        this.checkout_status_history = checkout_status_history;
    }

    public String getReturnedPO() {
        return returnedPO;
    }

    public void setReturnedPO(String returnedPO) {
        this.returnedPO = returnedPO;
    }

}
