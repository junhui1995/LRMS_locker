package com.sit.labresourcemanagement.Model;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by Simone on 18/1/2018.
 */

public class SharedPrefManager {
    public static final String SHARED_PREFERENCE_NAME="labresourcepref";
    public static final String KEY_USER_ID="keyid";
    public static final String KEY_USER_NAME="keyname";
    public static final String KEY_USER_ROLE="keyrole";
	public static final String KEY_USER_EMAIL="keyemail";
	public static final String KEY_USER_NAV="keynav";
	public static final String KEY_USER_FACULTY= "keyfaculty";

    private static Context mCtx;
    private static SharedPrefManager mInstance;

    public SharedPrefManager(Context context) {
        mCtx=context;
    }
    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }
    // this method will store user data
    public void userLogin(User user){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_ROLE, user.getRole());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_NAV, user.getNav());
        editor.putString(KEY_USER_FACULTY,user.getFaculty());
        editor.apply();
    }

    // this method will check whether the user is logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_NAME, null) != null;
    }

    // this method will give the logged in user
    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getString(KEY_USER_ID,null),
                sharedPreferences.getString(KEY_USER_NAME, null),
                sharedPreferences.getString(KEY_USER_ROLE, null),
				sharedPreferences.getString(KEY_USER_EMAIL, null),
                sharedPreferences.getString(KEY_USER_FACULTY, null)

        );
    }
    //  this method for logout
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        //mCtx.startActivity(new Intent(mCtx, LoginActivity.class));
    }

}
