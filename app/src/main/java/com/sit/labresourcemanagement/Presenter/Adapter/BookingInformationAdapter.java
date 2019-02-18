package com.sit.labresourcemanagement.Presenter.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.sit.labresourcemanagement.Model.Student.BookingInformationModel;
import com.sit.labresourcemanagement.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 11/6/18.
 */

public class BookingInformationAdapter extends BaseAdapter {

    //Declaration for API Route
    String url = ApiRoutes.getUrl();

    //Fragment's widgets declaration
    TextView textUserID ;
    TextView textWorkBenchName;
    TextView textLocation ;
    TextView textDateandTimeStart;
    TextView textDateandTimeEnd ;
    TextView textStatus;
    TextView textDelete;

    //Fragment's variables
    Context context;
    List<BookingInformationModel> listOfBookingInfomation = new ArrayList<>();
    private static LayoutInflater inflater = null;


    public BookingInformationAdapter(Context context,List<BookingInformationModel>listOfBookingInfomation ){
        this.context = context;
        this.listOfBookingInfomation = listOfBookingInfomation;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listOfBookingInfomation.size();
    }

    @Override
    public Object getItem(int position) {
        return listOfBookingInfomation.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        if(vi == null){
            vi = inflater.inflate(R.layout.card_booking_information,null);
        }

        //Widgets initialization
        textUserID = (TextView) vi.findViewById(R.id.tvUserID);
        textWorkBenchName = (TextView) vi.findViewById(R.id.tvWorkBenchName);
        textLocation = (TextView) vi.findViewById(R.id.tvLocation);
        textDateandTimeStart = (TextView) vi.findViewById(R.id.tvDateAndTimeStart);
        textDateandTimeEnd = (TextView) vi.findViewById(R.id.tvDateAndTimeEnd);
        textStatus = (TextView) vi.findViewById(R.id.tvStatus);
        textDelete = (TextView) vi.findViewById(R.id.tvDelete);


        //Insert content into widgets
        textUserID.setText(listOfBookingInfomation.get(position).getUserID());
        textWorkBenchName.setText(listOfBookingInfomation.get(position).getworkbenchname());
        textLocation.setText(listOfBookingInfomation.get(position).getlocation());
        textDateandTimeStart.setText(listOfBookingInfomation.get(position).getDateandTimeStart());
        textDateandTimeEnd.setText(listOfBookingInfomation.get(position).getDateandTimeEnd());
        textStatus.setText(listOfBookingInfomation.get(position).getStatus());
        //Set the color for the status
        setStatusColor(listOfBookingInfomation.get(position).getStatus());

        //Check if approval status is pending so user is able to cancel it
        if(listOfBookingInfomation.get(position).getApprovalStatus().equals("pending")){


            //Allow widgets to be visible
            textDelete.setVisibility(View.VISIBLE);

            //Set Cancel Booking click event listener
            textDelete.setOnClickListener(new View.OnClickListener() {
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
                            cancelBooking(position);

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
        }
        else if (!listOfBookingInfomation.get(position).getStatus().equals("pending")){
            //Allow widgets to be visible
            textDelete.setVisibility(View.GONE);
        }

        return vi;
    }

    public void cancelBooking(final int position){

        //Insert Booking details into DB
        StringRequest send_req = new StringRequest(Request.Method.POST,url + "cancelBooking.php", new Response.Listener<String>() {
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
                params.put("workbenchId", listOfBookingInfomation.get(position).getWorkbenchId());
                params.put("DateandTimeStart",listOfBookingInfomation.get(position).getDateandTimeStart());
                params.put("Status",listOfBookingInfomation.get(position).getStatus());
                

                return params;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(context);
        requestQueue.add(send_req);
    }

    public void setStatusColor(String status){

        switch(status){
            case "pending":
                textStatus.setTextColor(context.getResources().getColor(R.color.orange));
                break;
            case "approved":
                textStatus.setTextColor(context.getResources().getColor(R.color.green));
                break;
            case "rejected":
                textStatus.setTextColor(context.getResources().getColor(R.color.red));
                break;

        }
    }
}
