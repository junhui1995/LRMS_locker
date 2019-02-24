package com.sit.labresourcemanagement.Presenter.Fragment.PO;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sit.labresourcemanagement.Presenter.Adapter.POBookingPendingApprovalAdapter;
import com.sit.labresourcemanagement.Presenter.Adapter.POReturnPendingApprovalAdapter;
import com.sit.labresourcemanagement.Model.ApiRoutes;
import com.sit.labresourcemanagement.Presenter.Adapter.POLoanPendingApprovalAdapter;
import com.sit.labresourcemanagement.Model.PO.PendingBookingModel;
import com.sit.labresourcemanagement.Model.PO.PendingLoanModel;
import com.sit.labresourcemanagement.Model.PO.PendingReturnModel;
import com.sit.labresourcemanagement.Presenter.Fragment.Student.StudentNewLoanFragment;
import com.sit.labresourcemanagement.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class POPendingRequest extends Fragment{

    private View rootview;

    TabLayout tabLayout;
    RecyclerView recyclerView;
    List<PendingLoanModel> pendingLoanList;
    List<PendingBookingModel> pendingBookingList;
    List<PendingReturnModel> pendingReturnList;
    RecyclerView.Adapter adapter;
    String url = ApiRoutes.getUrl();

    public POPendingRequest() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_po_pending_request, container, false);

        tabLayout = rootview.findViewById(R.id.tabLayoutPendingRequest);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				int position = tab.getPosition();

				switch (position){
					case 0:
						loadLoan();
						break;

					case 1:
						loadBooking();
						break;

					case 2:
						loadReturn();
						break;
				}
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {

			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});

		recyclerView = rootview.findViewById(R.id.recyclerView_PO_pending);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadLoan();

        return rootview;
    }

    private void loadLoan() {

        StringRequest pendingLoanReq = new StringRequest(Request.Method.POST, url + "getPendingApproveLoanPO.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					String status = jsonObject.getString("status");
					if (status.equals("Success")) {
						JSONArray jsonArray = jsonObject.getJSONArray("loanList");
						pendingLoanList = new ArrayList<>();

						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject1 = jsonArray.getJSONObject(i);
							PendingLoanModel modelClass = new PendingLoanModel(
									jsonObject1.getString("userId"),
									jsonObject1.getString("assetDescription"),
									jsonObject1.getString("dateFrom"),
									jsonObject1.getString("dateTo"),
									jsonObject1.getString("reason"),
									jsonObject1.getString("status"),
									jsonObject1.getString("lockerRequest"),
									jsonObject1.getString("lid"),
									jsonObject1.getString("inventoryId"),
									jsonObject1.getString("assetNo")
							);
							pendingLoanList.add(modelClass);
						}
						adapter = new POLoanPendingApprovalAdapter(pendingLoanList, rootview.getContext(), POPendingRequest.this);
						recyclerView.setAdapter(adapter);
					} else {
						String msg;
						if(status.equals("No Record Found"))
							msg = "No pending loan";
						else
							msg = "Error occured";
						Snackbar.make(getView(), msg, Snackbar.LENGTH_SHORT).show();
						recyclerView.setAdapter(null);
					}


				} catch (JSONException e) {
					//e.printStackTrace();
				}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue pendingLoanQueue = Volley.newRequestQueue(getContext());
        pendingLoanQueue.add(pendingLoanReq);
    }



    //direct PO straight to check out asset
	public void transitIntoCheckoutFragment(){
		//This function is for the PO to directly go to checkout asset

		// Create fragment that you would want to go to
		POCheckInOutAsset poCheckInOutAsset = new POCheckInOutAsset();

		//Transit to the new fragment
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.content_frame, poCheckInOutAsset);
		transaction.addToBackStack(null);


		// Commit the transaction
		transaction.commit();
	}

    private void loadBooking() {
		pendingBookingList = new ArrayList<>();
		StringRequest pendingBookingReq = new StringRequest(Request.Method.POST, url + "getPendingApproveBookingPO.php", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					String status = jsonObject.getString("status");
					if (status.equals("Success")) {
						JSONArray jsonArray = jsonObject.getJSONArray("bookingList");

						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject1 = jsonArray.getJSONObject(i);
							PendingBookingModel modelClass = new PendingBookingModel(
									jsonObject1.getString("bid"),
									jsonObject1.getString("userid"),
									jsonObject1.getString("labId"),
									jsonObject1.getString("workbenchId"),
									jsonObject1.getString("timeFrom"),
									jsonObject1.getString("timeTo"),
									jsonObject1.getString("date"),
									jsonObject1.getString("reason")
							);
							pendingBookingList.add(modelClass);
						}
						adapter = new POBookingPendingApprovalAdapter(pendingBookingList, rootview.getContext());
						recyclerView.setAdapter(adapter);
					} else {
						String msg;
						if(status.equals("No Record Found"))
							msg = "No pending booking";
						else
							msg = "Error occured";
						Snackbar.make(getView(), msg, Snackbar.LENGTH_SHORT).show();
						recyclerView.setAdapter(null);
					}

				} catch (JSONException e) {
					//e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		RequestQueue pendingBookingQueue = Volley.newRequestQueue(getContext());
		pendingBookingQueue.add(pendingBookingReq);
	}

	private void loadReturn() {
		pendingReturnList = new ArrayList<>();
		StringRequest pendingReturnReq = new StringRequest(Request.Method.POST, url + "getPendingReturnRequestPO.php", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					String status = jsonObject.getString("status");
					if (status.equals("Success")) {
						JSONArray jsonArray = jsonObject.getJSONArray("returnList");

						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject1 = jsonArray.getJSONObject(i);
							PendingReturnModel model = new PendingReturnModel(
									jsonObject1.getString("userId"),
									jsonObject1.getString("assetNo"),
									jsonObject1.getString("assetDescription"),
									jsonObject1.getString("time"),
									jsonObject1.getString("location"),
									jsonObject1.getString("loanId"),
									jsonObject1.getString("id")
							);
							pendingReturnList.add(model);
						}
						adapter = new POReturnPendingApprovalAdapter(pendingReturnList, rootview.getContext());
						recyclerView.setAdapter(adapter);
					} else {
						String msg;
						if(status.equals("No Record Found"))
							msg = "No pending return";
						else
							msg = "Error occured";

						Snackbar.make(getView(), msg, Snackbar.LENGTH_SHORT).show();
						recyclerView.setAdapter(null);
					}

				} catch (JSONException e) {
					//e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		RequestQueue pendingReturnQueue = Volley.newRequestQueue(getContext());
		pendingReturnQueue.add(pendingReturnReq);
	}

}
