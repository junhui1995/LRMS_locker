package com.sit.labresourcemanagement.Presenter.Fragment.Student;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class StudentScanToReturnItem extends Fragment {


    //Declare 2 views , pre load scanner and new return request
    View initializeView,newReturnReqView;

    //Declaration for widgets
    TextView tvLocation,tvLocker, tvReturnedItem, tvPin,tvInformation;
    //EditText etReturnDate,etReturnTime,etMeetingPlace;
    Button btnSubmit;
    Button btnreScan;
    ImageView ivDate;
    ImageView ivTime;

    private String QRresult;
    private String inventoryID;
    private String url = ApiRoutes.getUrl();
    private boolean valideqpQRformat = false;
    private HashMap<String,String> hminvIDtoDescription;
    private HashMap<String,String> hminvIDtoloanId;


    //Declaration for widget pop up box and progress dialog
    AlertDialog.Builder builder;
    ProgressDialog pd;


    public StudentScanToReturnItem() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        builder = new AlertDialog.Builder(getContext());

        scanQR();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        //Initialize progress dialog and make sure hashmaps are clean
        pd = new ProgressDialog(getContext());
        hminvIDtoDescription = new HashMap<String,String>();
        hminvIDtoloanId = new HashMap<String,String>();


        View rootview = inflater.inflate(R.layout.fragment_stud_scanto_returnreq, container, false);

        //Pending load view
        initializeView = rootview.findViewById(R.id.preLoadStudScanEQPforReturnReq);

        btnreScan = initializeView.findViewById(R.id.btnReScan);
        tvInformation.setText("Please hang on while we initialize your Scanner");

        //Student new return request
        newReturnReqView = rootview.findViewById(R.id.stud_new_item_return);
        tvLocation = initializeView.findViewById(R.id.tvLocation);
        tvLocker = initializeView.findViewById(R.id.tvLocker);
        tvPin = initializeView.findViewById(R.id.tvPin);
        tvReturnedItem = initializeView.findViewById(R.id.tvReturnItem);

        /*tvReturnedItem = newReturnReqView.findViewById(R.id.tvReturnItem);
        etReturnDate = newReturnReqView.findViewById(R.id.etDateReturn);
        etReturnTime = newReturnReqView.findViewById(R.id.etTimeReturn);
        ivDate = newReturnReqView.findViewById(R.id.ivDateReturn);
        ivTime = newReturnReqView.findViewById(R.id.ivTime);
        etMeetingPlace = newReturnReqView.findViewById(R.id.etMeetingPlace);
        btnSubmit = newReturnReqView.findViewById(R.id.btnSubmit);*/

        //Set listeners
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReturnRequest();

            }
        });

        showpreLoadScanner();

        return rootview;

    }

    
    public void showpreLoadScanner() {
        initializeView.setVisibility(View.VISIBLE);
        newReturnReqView.setVisibility(View.GONE);

    }


    public void showNewReturnReqDetails() {
        newReturnReqView.setVisibility(View.VISIBLE);
        initializeView.setVisibility(View.GONE);

        //Display Scanned return req item

        //this is to get the returned item at the top
        tvReturnedItem.setText("Return Item : " + hminvIDtoDescription.get(inventoryID));


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {

                //In this fragment , QR String will inventory id
                QRresult = data.getStringExtra("QrResult");

                //Check whether equipment is in a valid format
                valideqpQRformat = checkQRformat(QRresult);
                if(valideqpQRformat){
                    getUserCurrentLoanDetails();
                }
                else{
                    displayInvalidFormatErrorBox();
                }

            }
            else{

                //This chunk of codes below appear when user pressed back button
                allowUsertoReScan();
            }
        }
    }


    private void scanQR() {
        Intent i = new Intent(getActivity(), QrCodeScannerActivity.class);
        i.putExtra("calledFrom","StudentScanToReturnRequest");
        startActivityForResult(i, 1);
    }

    public boolean checkQRformat(String QrResult){

        String[] splitedStr = QrResult.split("-");
        if(splitedStr.length == 3 && splitedStr[0].equalsIgnoreCase("LRMS") && splitedStr[1].equalsIgnoreCase("EQP"))
        {
            inventoryID = splitedStr[2];
            return true;
        }
        return false;
    }

    public void allowUsertoReScan(){

        btnreScan.setVisibility(View.VISIBLE);
        tvInformation.setText("Accidentally pressed back? Do you want to re-scan ?");
        btnreScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanQR();
            }
        });
    }


    public void displayInvalidFormatErrorBox(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setMessage("Sorry Invalid format. Re-scan ?");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        scanQR();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void displayEQPnotonUserLoanBox(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setMessage("You didnt loan this item, please re-scan");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        scanQR();
                    }
                });

        builder1.setNegativeButton(
                "Back",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        backtoLoanFragment();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void getUserCurrentLoanDetails() {
        StringRequest spinner_req=new StringRequest(Request.Method.POST,url +"getStudentCurrentLoan.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.getString("status").equals("Success")){
                        pd.dismiss();

                        JSONArray jsonArrayLoanDetails = jsonObject.getJSONArray("loanDetails");
                        JSONArray jsonArrayInvDetails = jsonObject.getJSONArray("intDetails");

                        for (int i=0; i<jsonArrayLoanDetails.length(); i++){
                            JSONObject loanDetail = jsonArrayLoanDetails.getJSONObject(i);
                            String loanId = loanDetail.getString("lid");
                            String assetDesc = jsonArrayInvDetails.getString(i);

                            Log.e("hello",assetDesc);
                            //Put details retrieve in a hashmap
                            hminvIDtoDescription.put(inventoryID,assetDesc);
                            hminvIDtoloanId.put(inventoryID,loanId);
                            Log.e("hello",hminvIDtoDescription.get(inventoryID));

                        }

                        //Display return req details
                        showNewReturnReqDetails();

                    }
                    else if(jsonObject.getString("status").equals("Fail")){
                        pd.dismiss();
                        //Fail means user didnt loan this item
                        displayEQPnotonUserLoanBox();
                    }

                } catch (JSONException e) {
                    //e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("userId", SharedPrefManager.getInstance(getContext()).getUser().getId());
                params.put("inventoryId", inventoryID);
                return params;
            }
        };

        RequestQueue spinner_que= Volley.newRequestQueue(getContext());
        spinner_que.add(spinner_req);
    }

    public void submitReturnRequest(){

        //Start the progress dialog so user cannot spam submit
        pd.setMessage("Submitting your request");
        pd.show();

        //setting calendar event

        //Submit return request using volley
        StringRequest spinner_req=new StringRequest(Request.Method.POST,url +"putUpReturnLoanRequest.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.getString("status").equals("Success")){
                        pd.dismiss();
                        Toast.makeText(getActivity(), "Return request added successfully", Toast.LENGTH_SHORT).show();

                        //Go back to loan fragment
                        backtoLoanFragment();
                    }
                    else if(jsonObject.getString("status").equals("Fail")){
                        pd.dismiss();
                        Toast.makeText(getActivity(), "Return request Fail", Toast.LENGTH_SHORT).show();
                    }
                    else if(jsonObject.getString("status").equals("Already exist")){
                        pd.dismiss();
                        Toast.makeText(getActivity(), "You have already submitted this return request before", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "An error has occurred", Toast.LENGTH_SHORT).show();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("loanId", hminvIDtoloanId.get(inventoryID));
                //params.put("")

                //params.put("time", dateReturn + " " + timeReturn);
                //params.put("place",meetingPlace);
                return params;
            }
        };

        RequestQueue spinner_que= Volley.newRequestQueue(getContext());
        spinner_que.add(spinner_req);

    }

    public void backtoLoanFragment(){

        //popbackstack to go back home
        getFragmentManager().popBackStack();
    }
}
