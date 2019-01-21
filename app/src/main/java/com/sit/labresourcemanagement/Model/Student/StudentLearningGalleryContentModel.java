package com.sit.labresourcemanagement.Model.Student;

public class StudentLearningGalleryContentModel {
	String pageNumber, name, instruction, imagePath;

	public StudentLearningGalleryContentModel(String pageNumber, String name, String imagePath, String instruction) {
		this.pageNumber = pageNumber;
		this.name = name;
		this.imagePath = imagePath;
		this.instruction = instruction;
	}

	public String getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}
}
