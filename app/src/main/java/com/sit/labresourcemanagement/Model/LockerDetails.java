package com.sit.labresourcemanagement.Model;

/**
 * Created by admin on 10/7/18.
 */

public class LockerDetails {
    String id,name,location,pin;

	public LockerDetails(String id, String name, String location) {
		this.id = id;
		this.name = name;
		this.location = location;
	}

	public LockerDetails(String id, String name , String location, String pin){
        this.id = id;
        this.name = name;
        this.location = location;
        this.pin = pin;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}
}
