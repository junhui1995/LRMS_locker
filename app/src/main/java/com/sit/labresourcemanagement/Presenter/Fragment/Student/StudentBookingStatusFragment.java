package com.sit.labresourcemanagement.Presenter.Fragment.Student;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sit.labresourcemanagement.Presenter.Adapter.BookingInformationAdapter;
import com.sit.labresourcemanagement.Model.ApiRoutes;
import com.sit.labresourcemanagement.Model.SharedPrefManager;
import com.sit.labresourcemanagement.Model.Student.BookingInformationModel;
import com.sit.labresourcemanagement.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentBookingStatusFragment extends Fragment {

    //Declaration for view
    View view;

    //Declaration for widgets
    ListView lvBookingInfo;

    //Declaration for API Route
    String url = ApiRoutes.getUrl();

    //Variables Declaration
    List<BookingInformationModel> listOfBookingInfomation;
    BookingInformationAdapter bookingInformationAdapter;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inflate view
        view = inflater.inflate(R.layout.fragment_student_booking_status, container, false);
        lvBookingInfo = view.findViewById(R.id.lvBookingInformation);
        //Dont display divider cause using card view
        lvBookingInfo.setDivider(null);

        //Initialize arraylist
        listOfBookingInfomation = new ArrayList<>();

        //Function to retrieve information using volley
        retrieveBookingInformation();

        return view;
    }

    public void retrieveBookingInformation (){
        //Insert Booking details into DB
        StringRequest send_req = new StringRequest(Request.Method.POST,url + "getBookingInformation.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);

                    if(jsonObject.getString("status").equals("Nothing inside")){
                        Toast.makeText(getActivity(), "Sorry no booking details found", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        JSONArray jsonArraybookingDetails = jsonObject.getJSONArray("BookingDetails");
                        JSONArray jsonArrayWKBDetails = jsonObject.getJSONArray("WKBLocationAndNAMEDetails");

                        for(int i = 0; i < jsonArraybookingDetails.length(); i++){
                            JSONObject jsonObjectbooking = jsonArraybookingDetails.getJSONObject(i);
                            JSONObject jsonObjectWKbenchdetails = jsonArrayWKBDetails.getJSONObject(i);
                            BookingInformationModel bookingInformationModel = new BookingInformationModel(jsonObjectbooking.getString("userid"),jsonObjectbooking.getString("workbenchId"),jsonObjectWKbenchdetails.getString("name"),jsonObjectWKbenchdetails.getString("location"),jsonObjectbooking.getString("timeFrom"),jsonObjectbooking.getString("timeTo"),jsonObjectbooking.getString("approvalPo"),jsonObjectbooking.getString("status"));
                            listOfBookingInfomation.add(bookingInformationModel);
                        }

                        //Attach list of booking information into its adapter to be displayed
                        lvBookingInfo.setAdapter(new BookingInformationAdapter(getContext(),listOfBookingInfomation));
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

                //Detail to be submitted into database to retrieve result
                Map<String, String>  params = new HashMap<String, String>();
                params.put("userId", SharedPrefManager.getInstance(getActivity()).getUser().getId());

                return params;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(send_req);
    }



}
