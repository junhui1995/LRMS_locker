package com.sit.labresourcemanagement.Model;

 /**
 * Created by Simone on 11/2/2018.
 */

public class ApiRoutes {



     private static String base_url = "http://192.168.1.11/labresourcemgmt/";
    //private static String base_url = "http://192.168.1.11/labresourcemgmt/";
	private static String url = base_url + "api/";

	public static String getBase_url() {
		 return base_url;
	 }
	public static String getUrl(){
        return url;
    }

 }

