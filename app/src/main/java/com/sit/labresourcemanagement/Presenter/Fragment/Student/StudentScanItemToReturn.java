package com.sit.labresourcemanagement.Presenter.Fragment.Student;

//Done By JunHui

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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
import com.sit.labresourcemanagement.Presenter.Activity.QrCodeScannerActivity;
import com.sit.labresourcemanagement.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.support.v4.content.FileProvider.getUriForFile;

public class StudentScanItemToReturn extends Fragment {

    //Declare Widgets
    Button btnreScan;
    TextView tvInformation;

    AlertDialog.Builder builder;

    //Declare pre load scanner view
    View initializeView;

    private String QRresult;
    private String inventoryID;
    private String url = ApiRoutes.getUrl();
    private boolean valideqpQRformat = false;

    Context context;

    public  StudentScanItemToReturn(){
// Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        builder = new AlertDialog.Builder(getContext());

        //scanQR();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_stud_scanto_returnreq, container, false);

        //Pending load view
        initializeView = rootview.findViewById(R.id.preLoadStudScanEQPforReturnReq);

        btnreScan = initializeView.findViewById(R.id.btnReScan);
        tvInformation.setText("Please hang on while we initialize your Scanner");

        return rootview;
    }

    //method to call the QR code reader from QRCodeScannerActivity.class
    private void scanQR() {
        Intent i = new Intent(getActivity(), QrCodeScannerActivity.class);
        i.putExtra("calledFrom","StudentScanToReturnRequest");
        startActivityForResult(i, 1);
    }

    //method th check QR code format
    public boolean checkQRformat(String QrResult){

        String[] splitedStr = QrResult.split("-");
        if(splitedStr.length == 3 && splitedStr[0].equalsIgnoreCase("LRMS") && splitedStr[1].equalsIgnoreCase("EQP"))
        {
            inventoryID = splitedStr[2];
            return true;
        }
        return false;
    }
    //Getting the result from scanning the QR code
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {

                //In this fragment , QR String will inventory id
                QRresult = data.getStringExtra("QrResult");

                //Check whether equipment is in a valid format
                valideqpQRformat = checkQRformat(QRresult);
                if(valideqpQRformat){
                    //activate camera to show proof

                    putIntoLoanReturn();

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
    //dialog box to indicate wrong QR code format
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
    //method to check if item is the correct item being loaned by the correct user
    public void putIntoLoanReturn()
    {
        //write api to put item into loanitemreturn
        StringRequest send_req = new StringRequest(Request.Method.POST,url + "returnloan.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);

                    //Check if response was exist
                    if (jsonObject.getString("status").equals("exist")){

                        Toast.makeText(context, "Item has been returned before", Toast.LENGTH_SHORT).show();



                    }
                    //Check if response was success
                    else if (jsonObject.getString("status").equals("Success")){

                        Toast.makeText(context, "You may proceed to locker", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(context, "please redo", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Something went wrong here..", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Details to post to delete a particular row
                Map<String, String>  params = new HashMap<String, String>();
                params.put("userId", SharedPrefManager.getInstance(context).getUser().getId());
                //params.put("lid", listofLoanInformation.get(position).getloanId());
                //params.put("inventoryid",listofLoanInformation.get(position).getinventoryId());
                return params;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(context);
        requestQueue.add(send_req);



    }



    public void backtoLoanFragment(){

        //popbackstack to go back home
        getFragmentManager().popBackStack();
    }
}
