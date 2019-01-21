package com.sit.labresourcemanagement.Model.PO;

/**
 * Created by Jun Hui on 11/01/19.
 */


public class ItemModel {

    String id;
    String assetNo;
    String assetDescription;
    String category;


    public String getAssetNo() {
        return assetNo;
    }

    public void setAssetNo(String assetNo) {
        this.assetNo = assetNo;
    }

  public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssetDescription() {
        return assetDescription;
    }

    public void setAssetDescription(String assetDescription) {
        this.assetDescription = assetDescription;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }



    public ItemModel(String id,String assetNo,String assetDescription,String category){
        this.assetNo = assetNo;
        this.id = id;
        this.assetDescription = assetDescription;
        this.category = category;

    }

}
