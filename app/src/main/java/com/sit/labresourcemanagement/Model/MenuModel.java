package com.sit.labresourcemanagement.Model;

import android.media.Image;

public class MenuModel {
	int resource;
	String title;

	public MenuModel(int resource, String title) {
		this.resource = resource;
		this.title = title;
	}

	public int getResource() {
		return resource;
	}

	public void setResource(int resource) {
		this.resource = resource;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
