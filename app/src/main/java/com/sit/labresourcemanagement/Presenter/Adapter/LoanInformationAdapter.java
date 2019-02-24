package com.sit.labresourcemanagement.Presenter.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.sit.labresourcemanagement.Model.ApiRoutes;
import com.sit.labresourcemanagement.Model.SharedPrefManager;
import com.sit.labresourcemanagement.Model.Student.LoanInformationModel;
import com.sit.labresourcemanagement.Model.LockerDetails;
import com.sit.labresourcemanagement.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 11/7/18.
 */

public class LoanInformationAdapter extends BaseAdapter{

    //Declaration for API Route
    String url = ApiRoutes.getUrl();

    //Widgets declaration
    TextView tvAssetDescription ,tvdateFrom ,tvdateTo , tvStatus,tvReasonofLoan,tvPoId,tvRejReason ,tvLockerReq , tvDelete, tvReturn;
    Button btnLockerDetails;


    Context context;
    List<LoanInformationModel> listofLoanInformation = new ArrayList<>();
    HashMap<String,LockerDetails> hmLockerIdToDetails = new HashMap<String,LockerDetails>();
    private static LayoutInflater inflater = null;



    public LoanInformationAdapter(Context context, List<LoanInformationModel> listofLoanInformation, HashMap<String,LockerDetails> hmLockerIdToDetails){
        this.context = context;
        this.listofLoanInformation = listofLoanInformation;
        this.hmLockerIdToDetails = hmLockerIdToDetails;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listofLoanInformation.size();
    }

    @Override
    public Object getItem(int position) {
        return listofLoanInformation.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        if(vi == null){
            vi = inflater.inflate(R.layout.card_student_loan_information,null);
        }

        //Initialize widgets
        tvAssetDescription = vi.findViewById(R.id.tvAssetDescription);
        tvdateFrom = vi.findViewById(R.id.tvDateFrom);
        tvdateTo = vi.findViewById(R.id.tvDateTo);
        tvStatus = vi.findViewById(R.id.tvLoanStatus);
        tvReasonofLoan = vi.findViewById(R.id.tvReasonOfLoan);
        tvPoId = vi.findViewById(R.id.tvPoId);
        tvRejReason = vi.findViewById(R.id.tvRejReason);
        tvLockerReq = vi.findViewById(R.id.tvLockerRequest);
        btnLockerDetails = vi.findViewById(R.id.btnViewLockerDetails);
        tvDelete = vi.findViewById(R.id.tvDelete);
        tvReturn = vi.findViewById(R.id.tvReturn);

        //Insert content into widgets
        tvAssetDescription.setText(listofLoanInformation.get(position).getassetDescription());
        tvdateFrom.setText(listofLoanInformation.get(position).getdateFrom());
        tvdateTo.setText(listofLoanInformation.get(position).getdateTo());
        tvStatus.setText(listofLoanInformation.get(position).getStatus());
        //Set color for status
        setStatusColor(listofLoanInformation.get(position).getStatus(),tvStatus);


        tvRejReason.setText(listofLoanInformation.get(position).getReasonReject());
        tvReasonofLoan.setText(listofLoanInformation.get(position).getReasonOfLoan());
        tvPoId.setText(listofLoanInformation.get(position).getPoId());
        tvLockerReq.setText(listofLoanInformation.get(position).getlockerRequest());

        //Activate listeners for button and image
        btnLockerDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Check if po has assigned an locker
                if(listofLoanInformation.get(position).getlockerId().equals("null")){
                    displayNoLockerAssigned();
                }
                else{
                    displaylockerDetails(listofLoanInformation.get(position).getlockerId());
                }
            }
        });

        tvReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                putIntoLoanReturn(position);
                //Toast.makeText(context, "You may proceed to locker", Toast.LENGTH_SHORT).show();
                //tvReturn.setVisibility(view.GONE);
            }
        });
        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Pop up confirmation
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Confirmation");
                alert.setMessage("Delete selected booking?");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do your work here
                        dialog.dismiss();

                        //Go into database to update row
                        cancelLoan(position);

                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                alert.show();

            }
        });

        //Check if locker details is available. Available display detail button
        if(listofLoanInformation.get(position).getlockerRequest().equalsIgnoreCase("Yes") && !listofLoanInformation.get(position).getlockerId().equals(null)){
            btnLockerDetails.setVisibility(View.VISIBLE);
        }
        else{
            btnLockerDetails.setVisibility(View.GONE);
        }


        //Check whether loan is pending status so can give user chance to delete request
        if(listofLoanInformation.get(position).getStatus().equalsIgnoreCase("Pending Approval")){
            tvDelete.setVisibility(View.VISIBLE);

        }
        else{
            tvDelete.setVisibility(View.GONE);
        }
        //Check whether loan is pending status so can give user chance to delete request

        //This is to ensure that student can only return an item after receiving it, ie on loan.
        //This is to prevent users from returning items that they haven check out.
        if(listofLoanInformation.get(position).getStatus().equalsIgnoreCase("on Loan")){
            tvReturn.setVisibility(View.VISIBLE);

        }
        else{
            tvReturn.setVisibility(View.GONE);
        }


        if(listofLoanInformation.get(position).getStatus().equalsIgnoreCase("Returned")){
            btnLockerDetails.setVisibility(View.GONE);

        }
        else{
            btnLockerDetails.setVisibility(View.VISIBLE);
        }

        return vi;
    }

    public void putIntoLoanReturn(final int position)
    {
        //insert record into database
        StringRequest send_req = new StringRequest(Request.Method.POST,url + "returnloan.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);

                    //Check if response was success
                    if (jsonObject.getString("status").equals("exist")){

                        Toast.makeText(context, "Item has been returned before", Toast.LENGTH_SHORT).show();

                        //update adapter
                        notifyDataSetChanged();
                    }                    //Check if response was success
                    else if (jsonObject.getString("status").equals("Success")){

                        Toast.makeText(context, "You may proceed to locker", Toast.LENGTH_SHORT).show();

                        //update adapter
                        notifyDataSetChanged();
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
                params.put("lid", listofLoanInformation.get(position).getloanId());
                params.put("inventoryid",listofLoanInformation.get(position).getinventoryId());
                return params;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(context);
        requestQueue.add(send_req);



    }
    public void displaylockerDetails(String lockerId){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle("Locker Details");
        builder1.setMessage("Locker Name : " + hmLockerIdToDetails.get(lockerId).getName() + "\n" + "Locker location : " + hmLockerIdToDetails.get(lockerId).getLocation());
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void displayNoLockerAssigned(){

        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle("Locker Details");
        builder1.setMessage("Sorry no locker has been assigned yet");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void cancelLoan(final int position){

            //Remove loan details from database
            StringRequest send_req = new StringRequest(Request.Method.POST,url + "cancelLoan.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject=new JSONObject(response);

                        //Check if response was success
                        if (jsonObject.getString("status").equals("Successfully cancelled")){

                            Toast.makeText(context, "Successfully cancelled", Toast.LENGTH_SHORT).show();

                            //update adapter
                            notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        //Toast.makeText(context, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
                    params.put("lid", listofLoanInformation.get(position).getloanId());
                    params.put("inventoryId",listofLoanInformation.get(position).getinventoryId());

                    return params;
                }
            };

            RequestQueue requestQueue= Volley.newRequestQueue(context);
            requestQueue.add(send_req);

    }

    public void setStatusColor(String status, TextView tvStatus){

        switch(status){
            case "Approved":
            case "Pending Approval":
                tvStatus.setTextColor(context.getResources().getColor(R.color.orange));
                break;

            case "Checked Out":
                tvStatus.setTextColor(context.getResources().getColor(R.color.green));
                break;

            case "On Loan":
                tvStatus.setTextColor(context.getResources().getColor(R.color.link));

                break;

            case "Rejected":
            case "Cancelled":
                tvStatus.setTextColor(context.getResources().getColor(R.color.red));
                break;
            default:
                break;

        }
    }
}
