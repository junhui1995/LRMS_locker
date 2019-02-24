package com.sit.labresourcemanagement.Presenter.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sit.labresourcemanagement.Model.ApiRoutes;
import com.sit.labresourcemanagement.Model.SharedPrefManager;
import com.sit.labresourcemanagement.Model.User;
import com.sit.labresourcemanagement.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    Button btnLogin;
    EditText name,password;
    ProgressDialog progressDialog;
    String url = ApiRoutes.getUrl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(LoginActivity.this);
        // If already logged in, go to appropriate activity
        // Might be a security issue, possible to edit manually?
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();

            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            intent.putExtra("userDetails", SharedPrefManager.getInstance(getApplicationContext()).getUser());
            startActivity(intent);
        }

        btnLogin = findViewById(R.id.btnLogin);
        name = findViewById(R.id.etUsername);
        password = findViewById(R.id.etPassword);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin(view);

            }
        });
    }

    //Login function
    private void userLogin(final View view) {
        final String username = name.getText().toString();
        final String userpassword = password.getText().toString();

        if (TextUtils.isEmpty(username)){
            name.setError("Please enter your username");
            name.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(userpassword)){
            password.setError("Please enter your password");
            password.requestFocus();
            return;
        }
        progressDialog.setMessage("Logging in");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Send login request to LRMS/api/login.php
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + "login.php", new Response.Listener<String>() {
            public void onResponse(String response) {

                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equals("Success")){
                        JSONObject userjson = jsonObject.getJSONObject("user_detail");

                        if (userjson.getString("role").equals("PO") || userjson.getString("role").equals("student")){
							User user1 = new User(userjson.getString("id"),userjson.getString("name"),userjson.getString("role"), userjson.getString("email"),userjson.getString("faculty"));
							SharedPrefManager.getInstance(getApplicationContext()).userLogin(user1);

							finish();
							startActivity(new Intent(getApplicationContext(), MainActivity.class));
						} else {
                        	Snackbar.make(view, "The mobile app has not catered for your role. Look forward to updates!", Snackbar.LENGTH_SHORT).show();
						}
                    }
                    else {
                        Snackbar.make(view, "Login Failed!", Snackbar.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
				Snackbar.make(view, "An error occured while logging in!", Snackbar.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("id",username);
                params.put("pass",userpassword);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
