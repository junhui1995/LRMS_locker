package com.sit.labresourcemanagement.Presenter.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.sit.labresourcemanagement.Model.Student.StudentReturnRequestModel;
import com.sit.labresourcemanagement.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 6/6/18.
 */

public class StudentReturnRequestAdapter extends BaseAdapter {

    //Declaration for API Route
    String url = ApiRoutes.getUrl();

    List<StudentReturnRequestModel> ReturnReq_list = new ArrayList<>();
    Context context;
    private LayoutInflater inflater = null;

    //Declare widgets for view later
    TextView tvInventoryId;
    TextView tvTime;
    TextView tvPlace ;
    TextView tvStatus;
    TextView tvrejReason;
    TextView tvpoId;
    TextView tvRemarks;
    TextView tvassetDescription;
    TextView tvDelete;

    public StudentReturnRequestAdapter(Context context,List<StudentReturnRequestModel> ReturnReq_list) {
        this.ReturnReq_list = ReturnReq_list;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return ReturnReq_list.size();
    }

    @Override
    public Object getItem(int position) {
        return ReturnReq_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        if(vi == null){
            vi = inflater.inflate(R.layout.card_student_return_request,null);
        }

        //Widgets initialization
        tvTime = (TextView) vi.findViewById(R.id.date_from_currentReturnReq);
        tvPlace = (TextView) vi.findViewById(R.id.tvPlace);
        tvStatus = (TextView) vi.findViewById(R.id.tvStatus);
        tvrejReason = (TextView) vi.findViewById(R.id.tvRejReason);
        tvRemarks = (TextView) vi.findViewById(R.id.tvRemarks);
        tvpoId = (TextView) vi.findViewById(R.id.tvPoID);
        tvassetDescription = (TextView) vi.findViewById(R.id.assetDescription_currentReturnReq);
        tvDelete = (TextView) vi.findViewById(R.id.tvDelete);
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

                        //Go into database to delete row
                        cancelReturnReq(position);

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

        //Insert content into widgets
        tvTime.setText(ReturnReq_list.get(position).getTime());
        tvPlace.setText(ReturnReq_list.get(position).getPlace());
        tvStatus.setText(ReturnReq_list.get(position).getStatus());

        //Hang on for a moment first
        setStatusColor(ReturnReq_list.get(position).getStatus(),tvStatus);

        tvpoId.setText(ReturnReq_list.get(position).getPoId());
        tvrejReason.setText(ReturnReq_list.get(position).getRejReason());
        tvRemarks.setText(ReturnReq_list.get(position).getRemarks());
        tvassetDescription.setText(ReturnReq_list.get(position).getAssetDescription());

        //Check whether loan is pending status so can give user chance to delete request
        if(ReturnReq_list.get(position).getStatus().equalsIgnoreCase("Pending Approval")){
            tvDelete.setVisibility(View.VISIBLE);
        }
        else{
            tvDelete.setVisibility(View.GONE);
        }


        return vi;
    }

    public void cancelReturnReq(final int position){

        //Insert Booking details into DB
        StringRequest send_req = new StringRequest(Request.Method.POST,url + "cancelReturnRequest.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);

                    //Check if response was success
                    if (jsonObject.getString("status").equals("Successfully cancelled")){

                        Toast.makeText(context, "Successfully cancelled", Toast.LENGTH_SHORT).show();

                        //Remove position from listOfInformation
                        ReturnReq_list.remove(position);

                        //update adapter
                        notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    Toast.makeText(context, "An Error Occurred", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Details to post to delete a particular row
                Map<String, String>  params = new HashMap<String, String>();
                params.put("loanId", ReturnReq_list.get(position).getLoanId());


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
