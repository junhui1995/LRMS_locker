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
import com.sit.labresourcemanagement.Presenter.Adapter.LoanInformationAdapter;
import com.sit.labresourcemanagement.Model.ApiRoutes;
import com.sit.labresourcemanagement.Model.SharedPrefManager;
import com.sit.labresourcemanagement.Model.Student.LoanInformationModel;
import com.sit.labresourcemanagement.Model.LockerDetails;
import com.sit.labresourcemanagement.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentLoanStatusFragment extends Fragment {

    //Declaration for view
    View view;

    //Declaration for widgets
    ListView lvLoanInfo;

    //Declaration for API Route
    String url = ApiRoutes.getUrl();

    //Variables Declaration
    List<LoanInformationModel> listOfLoanInfomation;
    private HashMap<String,LockerDetails> hmlockIDtoLockerdetails;



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inflate view
        view = inflater.inflate(R.layout.fragment_student_loan_status, container, false);
        lvLoanInfo = view.findViewById(R.id.lvLoanInformation);
        //Dont display divider cause using card view
        lvLoanInfo.setDivider(null);

        //Initialize arraylist
        listOfLoanInfomation = new ArrayList<>();
        hmlockIDtoLockerdetails = new HashMap<String,LockerDetails>();

        //Function to retrieve information using volley
        retrieveLoanInformation();

        return view;
    }

    public void retrieveLoanInformation (){

        //Retrieve loan details
        StringRequest send_req = new StringRequest(Request.Method.POST,url + "getLoanDetails.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);

                    if(jsonObject.getString("status").equals("no value")){
                        Toast.makeText(getActivity(), "Sorry no loan details found", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        JSONArray jsonArrayloanDetails = jsonObject.getJSONArray("loanDetails");
                        JSONArray jsonArrayLockerDetails = jsonObject.getJSONArray("lockerDetails");
                        JSONArray jsonArrayassetDescriptionDetails = jsonObject.getJSONArray("assetDescriptionDetails");

                        //Retrieve loan and asset description details and put them into objects
                        for(int i = 0; i < jsonArrayloanDetails.length(); i++){
                            JSONObject loanObject = jsonArrayloanDetails.getJSONObject(i);
                            String assetDescription = jsonArrayassetDescriptionDetails.getString(i);
                            LoanInformationModel loanInformationModel = new LoanInformationModel(assetDescription,loanObject.getString("lid"),loanObject.getString("inventoryId"),loanObject.getString("dateFrom"),loanObject.getString("dateTo"),loanObject.getString("status"),loanObject.getString("reason"),loanObject.getString("rejectReason"),loanObject.getString("poId"),loanObject.getString("lockerRequest"),loanObject.getString("lockerId"));
                            listOfLoanInfomation.add(loanInformationModel);
                        }

                        //Retrieve information from locker if locker details is not empty
                        for(int j = 0 ; j < jsonArrayLockerDetails.length(); j++){
                            JSONObject LockerObject = jsonArrayLockerDetails.getJSONObject(j);
                            LockerDetails lockerDetails = new LockerDetails(LockerObject.getString("id"),LockerObject.getString("name"),LockerObject.getString("location"),LockerObject.getString("pin"));
                            hmlockIDtoLockerdetails.put(LockerObject.getString("id"),lockerDetails);
                        }


                        //Attach list of booking information into its adapter to be displayed
                        lvLoanInfo.setAdapter(new LoanInformationAdapter(getContext(),listOfLoanInfomation,hmlockIDtoLockerdetails));
                    }


                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "An Error Occurred", Toast.LENGTH_SHORT).show();
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
