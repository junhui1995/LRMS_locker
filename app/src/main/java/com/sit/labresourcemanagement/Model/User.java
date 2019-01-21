package com.sit.labresourcemanagement.Model;

import java.io.Serializable;

/**
 * Created by Simone on 18/1/2018.
 */

public class User implements Serializable {
 String id,name,role, email, nav,faculty;

    public User(String id, String name, String role, String email , String faculty) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.email = email;
        this.faculty = faculty;
        nav = "swipe";
    }

	public String getNav() {
		return nav;
	}

	public void setNav(String nav) {
		this.nav = nav;
	}

	public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

	public String getEmail() {
		return email;
	}

	public String getFaculty() { return faculty;}
}
