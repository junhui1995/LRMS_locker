package com.sit.labresourcemanagement.Model.PO;

public class PendingBookingModel {
	String bookingId, userId, labId, workbenchId, timeFrom, timeTo, date, reason;

	public PendingBookingModel(String bid, String userid, String labid, String workbenchId, String timeFrom, String timeTo, String date, String reason) {
		this.bookingId = bid;
		this.userId = userid;
		this.labId = labid;
		this.workbenchId = workbenchId;
		this.timeFrom = timeFrom;
		this.timeTo = timeTo;
		this.date = date;
		this.reason = reason;
	}

	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLabId() {
		return labId;
	}

	public void setLabId(String labId) {
		this.labId = labId;
	}

	public String getWorkbenchId() {
		return workbenchId;
	}

	public void setWorkbenchId(String workbenchId) {
		this.workbenchId = workbenchId;
	}

	public String getTimeFrom() {
		return timeFrom;
	}

	public void setTimeFrom(String timeFrom) {
		this.timeFrom = timeFrom;
	}

	public String getTimeTo() {
		return timeTo;
	}

	public void setTimeTo(String timeTo) {
		this.timeTo = timeTo;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}
