package com.sit.labresourcemanagement.Presenter.Fragment.Student;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
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
import com.sit.labresourcemanagement.Model.Student.StudentReturnModel;
import com.sit.labresourcemanagement.Model.Student.StudentReturnRequestModel;
import com.sit.labresourcemanagement.Presenter.Adapter.StudentReturnAdapter;
import com.sit.labresourcemanagement.Presenter.Adapter.StudentReturnRequestAdapter;
import com.sit.labresourcemanagement.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Done by Hiew Jun Hui
public class StudentReturnLoanFragment extends Fragment {

    //Declaring view and wigdets
    View view;
    FloatingActionButton fabreturn;
    ListView itemReturn;
    String url = ApiRoutes.getUrl();

    //Variables for this fragment
    List<StudentReturnModel> listStudItemReturn;

    public StudentReturnLoanFragment(){}



    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_student_loan_return, container, false);
        fabreturn = view.findViewById(R.id.addItemReturn);

        itemReturn = view.findViewById(R.id.lvReturnRequest);
        itemReturn.setDivider(null);

        //Prepare the list student return request and load all return request data in
        listStudItemReturn = new ArrayList<>();
        //Get all the items currently on return
        loadCurrentReturnReqData();

        fabreturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Transit to the new fragment
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, new StudentScanToReturnItem());
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });

        return view;
    }
    private void loadCurrentReturnReqData() {
        StringRequest currentReturnReq=new StringRequest(Request.Method.POST,url+ "getAllReturnRequestStudent.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    Log.d("ret",jsonObject.toString());
                    JSONArray jsonArray=jsonObject.getJSONArray("userloanDetails");
                    JSONArray jsonArrayInvDetails = jsonObject.getJSONArray("intDetails");

                    if(jsonObject.getString("status").equals("Success")){

                        //If success get the details out
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject1=jsonArray.getJSONObject(i);
                            String assetDesc = jsonArrayInvDetails.getString(i);

                            StudentReturnModel studentReturnModel=new StudentReturnModel(jsonObject1.getString("loanId"),jsonObject1.getString("userid"),jsonObject1.getString("assetnumber"),jsonObject1.getString("poId"),jsonObject1.getString("remarks"),assetDesc);
                            listStudItemReturn.add(studentReturnModel);
                        }

                        //Set the adapter for display
                        itemReturn.setAdapter(new StudentReturnAdapter(getContext(),listStudItemReturn));

                    }
                    else if(jsonObject.getString("status").equals("Fail")){
                        Toast.makeText(getActivity(), "Return request Fail", Toast.LENGTH_SHORT).show();
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
                Map<String, String>  params = new HashMap<String, String>();
                params.put("userId", SharedPrefManager.getInstance(getContext()).getUser().getId());
                return params;
            }
        };
        RequestQueue currentRetReqQ= Volley.newRequestQueue(getContext());
        currentRetReqQ.add(currentReturnReq);
    }
}
