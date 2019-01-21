package com.sit.labresourcemanagement.Presenter.Fragment.Student;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sit.labresourcemanagement.Presenter.Activity.QrCodeScannerActivity;
import com.sit.labresourcemanagement.Model.ApiRoutes;
import com.sit.labresourcemanagement.Model.SharedPrefManager;
import com.sit.labresourcemanagement.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class StudentAttendanceFragment extends Fragment {

    private View resultView , intializeView;
    private static final String PASS = "Attendance recorded. Enjoy your class!";
    private static final String FAIL = "Attendance not recorded. Try again!";
    private String url = ApiRoutes.getUrl();
    private AlertDialog.Builder builder;
    private ProgressDialog pd;


    Button btnreScan;
    ImageView ivAttendance;
    TextView tvInformation;

    public StudentAttendanceFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        scnaQR();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_stud_attendance, container, false);


        //initialize 2 views (Pending Load View and Student Attendance result view)

        //preload Scan view
        intializeView = rootview.findViewById(R.id.preLoadStudAttendance);
        tvInformation = intializeView.findViewById(R.id.tvInformation);
        btnreScan = intializeView.findViewById(R.id.btnReScan);
        tvInformation.setText("Please hang on while we intialize QR Scanner");



        //Student Attendance result view
        resultView = rootview.findViewById(R.id.ResultStudAttendance);
        ivAttendance = resultView.findViewById(R.id.imageViewAttendance);

        showpreLoadAttendance();
		scnaQR();

        return rootview;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {

                //Receive the QR scanned by the student
                String attendanceQR = data.getStringExtra("QrResult");

                //Start a progress dialog while interacting with data in db
                pd = new ProgressDialog(getContext());
                pd.setMessage("Loading...");
                pd.show();

                showstudResultDetails();

                //Check if attendance is valid
                checkIfAttendanceQrExist(attendanceQR);

            }
            else{

                //This chunk of codes below appear when user pressed back button
                btnreScan.setVisibility(View.VISIBLE);
                tvInformation.setText("Accidentally pressed back?");
                btnreScan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scnaQR();
                    }
                });
            }
        }
    }

    public void showpreLoadAttendance() {
        intializeView.setVisibility(View.VISIBLE);
        resultView.setVisibility(View.GONE);

    }


    public void showstudResultDetails() {
        resultView.setVisibility(View.VISIBLE);
        intializeView.setVisibility(View.GONE);


    }

    private void scnaQR(){

        Intent i = new Intent(getActivity(), QrCodeScannerActivity.class);
        i.putExtra("calledFrom","Attendance");
        startActivityForResult(i, 1);
    }

    public void checkIfAttendanceQrExist(final String attendanceQR) {

        StringRequest send_req = new StringRequest(Request.Method.POST,url + "getAttendanceQRValidity.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);

                    //Check if response was success
                    if (jsonObject.getString("status").equals("Success")){


                        //if QR exists , take its moduleId , labId and topicId
                        String moduleId = jsonObject.getString("moduleId");
                        String labId = jsonObject.getString("labId");
                        String topicId = jsonObject.getString("topicId");


                        //If success means qr attendance exist therefore proceed into inserting student details in
                        insertStudentIn(attendanceQR,moduleId,labId,topicId);


                    }
                    else if(jsonObject.getString("status").equals("Fail")){

                        //If fail , allow user to have the choice to re-try
                        pd.dismiss();

                        ivAttendance.setImageResource(R.drawable.ic_cross);
                        ivAttendance.setVisibility(View.VISIBLE);

                        Snackbar.make(getView(), FAIL, Snackbar.LENGTH_LONG).setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                scnaQR();
                            }
                        }).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "An Error has occurred", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Something went wrong here..", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Details to be submitted into database
                Map<String, String>  params = new HashMap<String, String>();
                params.put("attendanceQR", attendanceQR);

                return params;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(send_req);

    }

    public void insertStudentIn(final String attendanceQR , final String moduleId , final String labId , final String topicId){

        StringRequest send_req = new StringRequest(Request.Method.POST,url + "insertStudentAttendance.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);

                    //Check if response was success
                    if (jsonObject.getString("status").equals("Success")){

                        pd.dismiss();
                        builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Attendance for Module name : " + jsonObject.getString("ModuleName") + " has been submitted" );
                        builder.show();

                        ivAttendance.setImageResource(R.drawable.ic_tick);
						ivAttendance.setVisibility(View.VISIBLE);

                    }
                    else if (jsonObject.getString("status").equals("Same Day")){

                        //If response received is same day , means user already scan awhile ago
                        pd.dismiss();
                        builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("You have already scanned for this lesson , good bye" );
                        builder.show();

                        ivAttendance.setImageResource(R.drawable.ic_cross);
                        ivAttendance.setVisibility(View.VISIBLE);


                    }
                    else{
                        pd.dismiss();

                        ivAttendance.setImageResource(R.drawable.ic_cross);
                        ivAttendance.setVisibility(View.VISIBLE);

                        Snackbar.make(getView(), FAIL, Snackbar.LENGTH_LONG).setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                scnaQR();
                            }
                        }).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Something went wrong here..", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Something went wrong here..", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Details to be submitted into database
                Map<String, String>  params = new HashMap<String, String>();
                params.put("userId",SharedPrefManager.getInstance(getContext()).getUser().getId());
                params.put("attendanceQR", attendanceQR);
                params.put("moduleId",moduleId);
                params.put("labId",labId);
                params.put("topicId",topicId);

                return params;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(send_req);

    }

}
