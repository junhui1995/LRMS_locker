package com.sit.labresourcemanagement.Presenter.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.sit.labresourcemanagement.Model.LockerDetails;
import com.sit.labresourcemanagement.Model.SharedPrefManager;
import com.sit.labresourcemanagement.Model.Student.StudentReturnModel;
import com.sit.labresourcemanagement.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentReturnAdapter extends BaseAdapter {

    Context context;
    private LayoutInflater inflater = null;

    //Declare widgets for view later
/*    TextView tvInventoryId;
    TextView tvTime;
    TextView tvPlace ;
    TextView tvStatus;
    TextView tvrejReason;
    TextView tvpoId;
    TextView tvRemarks;
    TextView tvassetDescription;
    TextView tvDelete;*/
    TextView tvCampus, tvLocker, tvPin;
    TextView tvuserid, tvinventoryid, tvstatus, tvpoId, tvloanid, lockerid;
    Button btnGetLocker;


    //Declaration for API Route
    String url = ApiRoutes.getUrl();
    List<StudentReturnModel> returnModelList;
    //HashMap<String,LockerDetails> hmLockerIdToDetails = new HashMap<String,LockerDetails>();


    public StudentReturnAdapter(Context context, List<StudentReturnModel> returnModelList) {
        this.returnModelList = returnModelList;
        this.context = context;
        //this.hmLockerIdToDetails = hmLockerIdToDetails;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return returnModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return returnModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        View vi = convertView;
        if (vi == null) {
            vi = inflater.inflate(R.layout.card_student_item_return, null);
        }
        //Widgets initialization
        /*tvTime = vi.findViewById(R.id.date_from_currentReturnReq);
        tvPlace = vi.findViewById(R.id.tvPlace);
        tvStatus = vi.findViewById(R.id.tvStatus);
        tvrejReason = vi.findViewById(R.id.tvRejReason);
        tvRemarks = vi.findViewById(R.id.tvRemarks);
        tvpoId = vi.findViewById(R.id.tvPoID);*/

        tvuserid = vi.findViewById(R.id.tvreturnuserid);
        tvinventoryid = vi.findViewById(R.id.tvreturnassetno);
        tvpoId = vi.findViewById(R.id.tvReturnPoId);
        tvloanid = vi.findViewById(R.id.tvreturnloanid);
        tvstatus = vi.findViewById(R.id.tvreturnstatus);

        tvloanid.setText(returnModelList.get(position).getLoanId());
        tvuserid.setText(returnModelList.get(position).getUserId());
        tvinventoryid.setText(returnModelList.get(position).getInventoryId());
        tvpoId.setText(returnModelList.get(position).getPoId());
        tvstatus.setText(returnModelList.get(position).getStatus());


        btnGetLocker = vi.findViewById(R.id.btnGetLocker);

        //if (status == approved) then set visibility to true
        /*if (Return_list.get(position).getLoanId() == "Approved") {


            tvReturn.setVisibility(vi.GONE);
        } else {
            tvReturn.setVisibility(vi.VISIBLE);
        }*/

        btnGetLocker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                //set the locker

                checklocker(position);




            }
        });

        return vi;
    }
    /*public void displaylockerDetails(String lockerId){
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
    }*/
    private void checklocker(final int position)
    {
        GetLocker(position);
    }
    private void GetLocker(final int position) {
        StringRequest send_req = new StringRequest(Request.Method.POST, url + "getlocker.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    //JSONArray jsonArray = jsonObject.getJSONArray("lockerList");
                    Log.d("setlocker",jsonObject.toString());
                    if (jsonObject.getString("status").equals("No Locker"))
                    {
                        Toast.makeText(context, "No Locker Available", Toast.LENGTH_SHORT).show();

                    }

                    else if (jsonObject.getString("status").equals("Success")){

                        String lockerid = jsonObject.getString("lockerid");
                        lockerDialog(lockerid);
                        Toast.makeText(context, "Locker has been assigned", Toast.LENGTH_SHORT).show();
                        btnGetLocker.setVisibility(View.GONE);
                        //update adapter
                        notifyDataSetChanged();
                    }
                    else if(jsonObject.getString("status").equals("before"))
                    {

                        Toast.makeText(context, "Locker has been assigned BEFORE", Toast.LENGTH_SHORT).show();
                        String lockerid = jsonObject.getString("lockerid");
                        lockerDialog(lockerid);
                    }

                    else
                    {
                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {


                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getActivity(), "Something went wrong here..", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Detail to be submitted into database to retrieve result
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", SharedPrefManager.getInstance(context).getUser().getId());
                params.put("lid", returnModelList.get(position).getLoanId());

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(send_req);
    }
    private void lockerDialog(String lockerId){
        View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_checkout_locker_details, null);
        tvCampus = dialogView.findViewById(R.id.textViewCampus);
        tvLocker = dialogView.findViewById(R.id.textViewLocker);
        tvPin = dialogView.findViewById(R.id.textViewPin);

        getLockerDetails(lockerId);

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setIcon(R.drawable.ic_locker)
                .setTitle("Locker Details")
                .create();

        dialog.show();
    }

    private void getLockerDetails(final String lockerId){
        StringRequest location_req = new StringRequest(Request.Method.POST, url + "getLockerDetail.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);

                    if (responseObject.getString("status").equals("Success")){
                        JSONObject lockerDetail = responseObject.getJSONObject("detail");

                        if(tvCampus != null && tvLocker != null && tvPin != null){
                            tvCampus.setText(lockerDetail.getString("location"));
                            tvLocker.setText(lockerDetail.getString("name"));
                            tvPin.setText(lockerDetail.getString("pin"));
                        }

                    } else if (responseObject.getString("status").equals("Fail")) {
                        //error
                    }

                } catch (JSONException e){
                    //e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("lockerId", lockerId);
                return params;
            }
        };
        RequestQueue locker_queue = Volley.newRequestQueue(context);
        locker_queue.add(location_req);
    }
}
