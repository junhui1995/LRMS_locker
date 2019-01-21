package com.sit.labresourcemanagement.Model.Student;

/**
 * Created by admin on 11/6/18.
 */

public class BookingInformationModel {

    String userId,workbenchId,name,location,DateandTimeStart,DateandTimeEnd,ApprovalStatus,status;

    public BookingInformationModel(String userId, String workbenchId, String name , String location, String DateandTimeStart , String DateandTimeEnd, String ApprovalStatus,String status){
        this.userId = userId;
        this.workbenchId = workbenchId;
        this.name = name;
        this.location = location;
        this.DateandTimeStart = DateandTimeStart;
        this.DateandTimeEnd = DateandTimeEnd;
        this.ApprovalStatus = ApprovalStatus;
        this.status = status;
    }

    public String getUserID(){
        return this.userId;
    }

    public String getWorkbenchId() { return this.workbenchId;}

    public String getworkbenchname(){
        return this.name;
    }

    public String getlocation(){
        return this.location;
    }

    public String getDateandTimeStart(){
        return this.DateandTimeStart;
    }

    public String getDateandTimeEnd(){
        return this.DateandTimeEnd;
    }

    public String getApprovalStatus(){
        return this.ApprovalStatus;
    }

    public String getStatus(){
        return this.status;
    }


}
