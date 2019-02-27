package com.sit.labresourcemanagement.Presenter.Fragment.Student;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.sit.labresourcemanagement.Model.PO.ItemModel;
import com.sit.labresourcemanagement.Model.SharedPrefManager;
import com.sit.labresourcemanagement.Presenter.Adapter.StudentViewItemAdapter;
import com.sit.labresourcemanagement.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StudentItemFragment extends Fragment {

    //Declaration for view
    View view;

    //Declaration for widgets
    ListView lvLoanItems;

    //Declaration for API Route
    String url = ApiRoutes.getUrl();

    //Variables Declaration
    List<ItemModel> itemModelList;

    private String inventoryTag = "";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate view
        view = inflater.inflate(R.layout.fragment_student_items, container, false);
        lvLoanItems = view.findViewById(R.id.lvLoanableItems);
        //Dont display divider cause using card view
        //lvLoanItems.setDivider(null);

        //Initialize arraylist
        itemModelList = new ArrayList<>();

        getItems();
        instructiondialog();



        return view;
    }
    private void instructiondialog()
    {
        AlertDialog.Builder instructiondialog = new AlertDialog.Builder(getActivity());
        instructiondialog.setMessage("This is the catalogue of items that are available for loaning."+"\n" + "Instructions for item catalogue: " + "\n" + "1) Browse through the resource catalogue." + "\n" + "2) Click request to select the resource you wish to loan.")
                .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                    }

                }).create();

        instructiondialog.show();

    }



    public void changeFragment(String inventoryAssetDescriptio, String Categor,String inventoryI,String location){

        //Fragment fragment = new StudentNewLoanFragment();
        StudentNewLoanFragment fragment = new StudentNewLoanFragment();
        //StudentViewItemAdapter studentViewItemAdapter = new StudentViewItemAdapter(getContext(), itemModelList, StudentItemFragment.this);
        //Prepare data to be inserted into StudentNewLoanFragment fragment
        Bundle bundleObj = new Bundle();

        //inventoryAssetDescription1 = studentViewItemAdapter.inventoryAssetDescription;
        //Category1 = studentViewItemAdapter.Category;
        //inventoryId1 = studentViewItemAdapter.inventoryID;

        bundleObj.putString("category",Categor);
        bundleObj.putString("assetDescription",inventoryAssetDescriptio);
        bundleObj.putString("InventoryID",inventoryI);
        bundleObj.putString("location",location);
        bundleObj.putString("tag",inventoryTag);
        fragment.setArguments(bundleObj);

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

    }

    public void getItems() {
        //Retrieve loan details
        StringRequest getitems = new StringRequest(Request.Method.POST, url + "getLoanableItems.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArrayitems = jsonObject.getJSONArray("loanitems");

                    //Retrieve loan and asset description details and put them into objects
                    for (int i = 0; i < jsonArrayitems.length(); i++) {
                        JSONObject loanObject = jsonArrayitems.getJSONObject(i);


                        ItemModel itemModel = new ItemModel(loanObject.getString("id"),loanObject.getString("assetNo"), loanObject.getString("assetDescription"), loanObject.getString("category"), loanObject.getString("location"));


                        itemModelList.add(itemModel);
                    }

                    lvLoanItems.setAdapter(new StudentViewItemAdapter(getContext(), itemModelList, StudentItemFragment.this));


                } catch (JSONException e) {
                    //Toast.makeText(getActivity(), "An Error Occurred", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Something went wrong here..", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Detail to be submitted into database to retrieve result
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", SharedPrefManager.getInstance(getActivity()).getUser().getId());
                //params.put("itemstatus", SharedPrefManager.getInstance(getActivity()).getUser().getId());
                //params.put("loanable", SharedPrefManager.getInstance(getActivity()).getUser().getId());


                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(getitems);

    }

}


