package com.sit.labresourcemanagement.Presenter.Fragment.Student;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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
import com.sit.labresourcemanagement.Presenter.Activity.MainActivity;
import com.sit.labresourcemanagement.Presenter.Activity.QrCodeScannerActivity;
import com.sit.labresourcemanagement.Model.ApiRoutes;
import com.sit.labresourcemanagement.Model.SharedPrefManager;
import com.sit.labresourcemanagement.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class StudentScanQR extends Fragment {

	private static final String UNIVERSAL_TAG = "Universal";
	private static final String LOAN_TAG = "Loan Asset";
	private String prevFragment;

    TextView tvInformation ;
    Button btnreScan;

    private String url = ApiRoutes.getUrl();
    private String loanId = "";
    private String QRresult = "";
    private String lockerID =  "";
    private String Category = "";
    private String itemCategory = "";
    private String lockerName = "";
    private String lockerLocation = "";
    private String lockerPin = "";
    private String inventoryID = "";
    private String inventoryAssetNo="";
    private String inventoryAssetDescription = "";
    private String inventorylocation = "";
	private String inventoryTag = "";
    private boolean valideqpQRformat = false;
    private boolean isInvLoanable = false;


    //Declaration for widget pop up box and progress dialog
    AlertDialog.Builder builder;
    AlertDialog alert;
    ProgressDialog pd;

    public StudentScanQR() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		((MainActivity) getActivity()).disableDrawer();

        builder = new AlertDialog.Builder(getContext());

        Bundle args = getArguments();
        if (args != null){
        	prevFragment = UNIVERSAL_TAG;
        	Intent intent = new Intent();
        	intent.putExtra("QrResult", args.getString(UNIVERSAL_TAG));
        	onActivityResult(1, RESULT_OK, intent);
		} else {
			prevFragment = LOAN_TAG;
			scanQR();
		}
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        //Initialize progress dialog
        pd = new ProgressDialog(getContext());


        View rootview = inflater.inflate(R.layout.fragment_pendingload_qrscanner, container, false);

        //Pending load view
        tvInformation = rootview.findViewById(R.id.tvInformation);
        btnreScan = rootview.findViewById(R.id.btnReScan);
        tvInformation.setText("Please hang on while we intialize QR Scanner");


        return rootview;

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

                    if(inventoryID.isEmpty()){

                        //if inventory id is empty, view locker details
                        viewLKRdetails(SharedPrefManager.getInstance(getActivity()).getUser().getId());

                    }
                    else if(lockerID.isEmpty()){
                        //if locker id is empty,

                        //Retrieve all the loanable inventory items from database/
                        retrieveloanableInvDetail();

                    }

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

    public void retrieveloanableInvDetail(){

        //Go into database and retrieve inventory items for loan
        StringRequest itemReq =new StringRequest(Request.Method.POST,url+ "loanitemdetail.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);

                    //Check if response was success
                    if (jsonObject.getString("status").equals("Success")){

                        //Store loanable inventory details
                        itemCategory = jsonObject.getString("category");
                        inventoryAssetNo = jsonObject.getString("assetNo");
                        inventoryAssetDescription = jsonObject.getString("assetDescription");
                        //inventorylocation = jsonObject.getString("assetLocation");
                        inventoryID = jsonObject.getString("id");
                        //inventoryTag = jsonObject.getString("tag");


                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                        builder1.setTitle("Locker Details");
                        builder1.setMessage("Resource Name : " + inventoryAssetDescription);
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "Request",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        transitIntoNewLoanFragment();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                        //Go into new loan fragment with details

                    }

                    else if (jsonObject.getString("status").equals("Fail")){

                        //Insert alert dialog to tell user that item is no available for loan
                        displayNoLoanAvailableMessage();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "An JSON error has occured", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "An Response error has occured", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Details to be submitted into database to retrieve inventory items
                Map<String, String>  params = new HashMap<String, String>();
                params.put("inventoryId", inventoryID);
                //params.put("category", Category);
                return params;
            }
        };
        RequestQueue requestQueue=Volley.newRequestQueue(getActivity());
        requestQueue.add(itemReq);
    }

    private void scanQR() {
        Intent i = new Intent(getActivity(), QrCodeScannerActivity.class);
        i.putExtra("calledFrom","StudentScanEQ");
        startActivityForResult(i, 1);
    }

    public boolean checkQRformat(String QrResult){

        String[] splitedStr = QrResult.split("-");
        if(splitedStr.length == 3 && splitedStr[0].equalsIgnoreCase("LRMS") && (splitedStr[1].equalsIgnoreCase("EQP") || splitedStr[1].equalsIgnoreCase("LKR") || splitedStr[1].equalsIgnoreCase("TOL")  || splitedStr[1].equalsIgnoreCase("ACC")))
        {
            //Check whether its a eqp or a locker
            if(splitedStr[1].equalsIgnoreCase("EQP") || splitedStr[1].equalsIgnoreCase("TOL")  || splitedStr[1].equalsIgnoreCase("ACC")){
                inventoryID = splitedStr[2];

                //Now its an inventory item , find its category
                checkCategory(splitedStr[1]);

                //Make sure lockerId is clear since scanned result is an inventory id
                lockerID = "";

            }
            else if(splitedStr[1].equalsIgnoreCase("LKR")){
                lockerID = splitedStr[2];

                //Make sure inventoryID is clear since scanned result is a locker id
                inventoryID = "";
            }
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

    //View Locker details
    public void viewLKRdetails(final String userId){

        StringRequest itemReq =new StringRequest(Request.Method.POST,url+ "viewLockerDetails.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.getString("status").equals("Success")){

                        JSONObject lockerDetails = jsonObject.getJSONObject("lockerdetail");

                        loanId = lockerDetails.getString("loanId");
                        lockerName = lockerDetails.getString("name");
                        lockerLocation = lockerDetails.getString("location");
                        lockerPin = lockerDetails.getString("pin");

                        //Allow user to view locker details
                        displayConfirmationCheckout();


                    }
                    else if(jsonObject.getString("status").equals("Fail")){
                        //Tell user that they cannot view the details because locker doesnt belong to them
                        displayLKRdetailsNotAvailable();
                    }
                    else if (jsonObject.getString("status").equals("No Locker"))
                    {
                        Toast.makeText(getActivity(), "There is no available locker at this time", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "An error has occurred", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "An error has occurred", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Details to be submitted into database to retrieve inventory items
                Map<String, String>  params = new HashMap<String, String>();
                params.put("userId",userId);
                params.put("lockerId",lockerID);
                return params;
            }
        };
        RequestQueue requestQueue=Volley.newRequestQueue(getActivity());
        requestQueue.add(itemReq);

    }

    public void transitIntoNewLoanFragment(){
    	//This function is for users who scanned an asset QR and wants to go to the learning gallery.

        // Create fragment that you would want to go to
        StudentNewLoanFragment studentNewLoanFragment = new StudentNewLoanFragment();

        //Prepare data to be inserted into StudentNewLoanFragment fragment
        Bundle bundleObj = new Bundle();
        bundleObj.putString("InventoryID",inventoryID);
        bundleObj.putString("assetDescription",inventoryAssetDescription);
        bundleObj.putString("category",Category);
        bundleObj.putString("location",inventorylocation);
        bundleObj.putString("tag", inventoryTag);

        studentNewLoanFragment.setArguments(bundleObj);

        //Transit to the new fragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, studentNewLoanFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    public void displayLKRdetails(){

        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setTitle("Locker Details");
        builder1.setMessage("Locker Name : " + lockerName + "\n" + "Locker location : " + lockerLocation + "\n" + "Locker Pin : "  + lockerPin + "\n\n");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //go back home page
                        getFragmentManager().popBackStack();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void displayConfirmationCheckout(){

        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setTitle("Locker Details");
        builder1.setMessage("Please be reminded that once you check out , its your responsibility to take care of this item and the locker will be free up");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Check out",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        displayLKRdetails();
                        checkOutLocker();
                    }
                });

        builder1.setNegativeButton(
                "Dont check out",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        //go back home page
                        getFragmentManager().popBackStack();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    public void displayLKRdetailsNotAvailable(){

        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setTitle("Locker Details");
        builder1.setMessage("Sorry you are not allowed to view this locker's details");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Re-Scan",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        scanQR();
                    }
                });

        builder1.setNegativeButton(
                "Back",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //go back home page
                        getFragmentManager().popBackStack();
                    }
                });


        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    public void displayNoLoanAvailableMessage(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setMessage("Sorry item not available for loan. Re-scan ?");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        scanQR();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //go back home page
                        getFragmentManager().popBackStack();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
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


    public void displaySuccessfullyCheckoutMessage(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setMessage("Successfully check out item. Please take good care of the item");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void checkCategory(String acronym){

        switch (acronym){
            case "ACC":
                Category = "accessories";
                break;
            case "EQP":
                Category = "equipment";
                break;
            case "TOL":
                Category = "tools";
                break;
        }

    }

    public void checkOutLocker(){

        StringRequest itemReq =new StringRequest(Request.Method.POST,url+ "studentCheckoutLocker.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.getString("status").equals("Success")){
                        displaySuccessfullyCheckoutMessage();
                    }
                    else if(jsonObject.getString("status").equals("Fail")){
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "An error has occurred", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "An error has occurred", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Details to be submitted into database to retrieve inventory items
                Map<String, String>  params = new HashMap<String, String>();
                params.put("lockerId",lockerID);
                params.put("loanId",loanId);
                return params;
            }
        };
        RequestQueue requestQueue=Volley.newRequestQueue(getActivity());
        requestQueue.add(itemReq);

    }

	@Override
	public void onDestroy() {
		super.onDestroy();
		((MainActivity) getActivity()).enableDrawer();
	}
}
