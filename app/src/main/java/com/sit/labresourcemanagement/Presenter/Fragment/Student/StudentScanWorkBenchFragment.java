package com.sit.labresourcemanagement.Presenter.Fragment.Student;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.sit.labresourcemanagement.Presenter.Activity.QrCodeScannerActivity;
import com.sit.labresourcemanagement.Model.ApiRoutes;
import com.sit.labresourcemanagement.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class StudentScanWorkBenchFragment extends Fragment {

	private static final String UNIVERSAL_TAG = "Universal";
	private static final String WORKBENCH_TAG = "Booking";
	private String prevFragment;

    //Variables for this fragment
    private String QrResult = "";
    private String workbenchId = "";
    private String url = ApiRoutes.getUrl();
    private String WorkBenchLocation = "";
    private String WorkBenchName = "";
    private boolean validWorkbenchQRformat = false;

    TextView tvInformation;
    Button btnreScan;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


		Bundle args = getArguments();
		if (args != null){
			prevFragment = UNIVERSAL_TAG;
			Intent intent = new Intent();
			intent.putExtra("QrResult", args.getString(UNIVERSAL_TAG));
			onActivityResult(1, RESULT_OK, intent);
		} else {
			prevFragment = WORKBENCH_TAG;
			scanQR();
		}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_pendingload_qrscanner, container, false);

        tvInformation = rootview.findViewById(R.id.tvInformation);
        btnreScan = rootview.findViewById(R.id.btnReScan);
        tvInformation.setText("Please hang on while we intialize QR Scanner");



        return rootview;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                QrResult = data.getStringExtra("QrResult");

                //Check whether workbench is in a valid format
                validWorkbenchQRformat = checkQRformat(QrResult);
                if(validWorkbenchQRformat){

                    retrieveWorkBenchDetails();
                }
                else{
                    displayInvalidFormat();
                }

            }
            else{

                //Come here means user press back , let user re scan
                allowUsertoReScan();
            }
        }
    }

    private void scanQR(){
        Intent i = new Intent(getActivity(), QrCodeScannerActivity.class);
        i.putExtra("calledFrom","StudentScanWorkBench");
        startActivityForResult(i, 1);
    }

    private void retrieveWorkBenchDetails(){

        StringRequest send_req = new StringRequest(Request.Method.POST,url + "getWorkBenchNameAndLocation.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(LoanRequestActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject=new JSONObject(response);

                    //Check if response was success
                    if (jsonObject.getString("status").equals("Success")){

                        WorkBenchName = jsonObject.getString("workbench");
                        WorkBenchLocation = jsonObject.getString("location");

                        TransitIntoNewbookingFragment();
                    }
                    else if(jsonObject.getString("status").equals("Sorry no workbench found")){

                        displayNoWorkBenchFound();

                        //Recall scanQR is there is no workbench found after scanning
                        scanQR();
                    }
                    else{
                        Toast.makeText(getActivity(), "Sorry some sql statement went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "An error has occurred", Toast.LENGTH_SHORT).show();
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
                params.put("id", workbenchId);
                return params;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(send_req);
    }

    public void TransitIntoNewbookingFragment(){

        //Note ** QrResult is WorkbenchID in this fragment
        //Concatanate the WorkbenchId , workbench name and work bench location together
        String WorkBenchDetails = workbenchId + "," + WorkBenchName + "," + WorkBenchLocation;

        //Transit into new booking fragment

        // Create fragment that you would want to go to
        StudentNewBooking studentNewBooking = new StudentNewBooking();

        //Prepare data to be inserted into StudentNewBooking fragment
        Bundle bundleObj = new Bundle();
        bundleObj.putString("WorkBenchDetails",WorkBenchDetails);
        studentNewBooking.setArguments(bundleObj);

        //Transit to the new fragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, studentNewBooking);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();

    }

    public boolean checkQRformat(String QrResult){

        String[] splitedStr = QrResult.split("-");
        if(splitedStr.length == 3 && splitedStr[0].equalsIgnoreCase("LRMS") && splitedStr[1].equalsIgnoreCase("WKB"))
        {
            workbenchId = splitedStr[2];
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

    public void displayInvalidFormat(){
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

    public void displayNoWorkBenchFound(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setMessage("Sorry no workbench found. Re-scan ?");
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


}
