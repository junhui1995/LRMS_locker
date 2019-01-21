package com.sit.labresourcemanagement.Presenter.Fragment.PO;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sit.labresourcemanagement.Presenter.Activity.QrCodeScannerActivity;
import com.sit.labresourcemanagement.Presenter.Adapter.POPendingCheckInAdapter;
import com.sit.labresourcemanagement.Presenter.Adapter.POPendingCheckoutAdapter;
import com.sit.labresourcemanagement.Model.ApiRoutes;
import com.sit.labresourcemanagement.Model.PO.PendingCheckInModel;
import com.sit.labresourcemanagement.Model.PO.PendingCheckoutModel;
import com.sit.labresourcemanagement.Model.SharedPrefManager;
import com.sit.labresourcemanagement.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class POCheckInOutAsset extends Fragment {

	//Declare widgets
	TabLayout tabLayout;
	RecyclerView recyclerView;
	RecyclerView.Adapter adapter;
	List<PendingCheckoutModel> pendingCheckoutList;
	List<PendingCheckInModel> pendingCheckInList;

	//Declare variables
	View rootview;
	String url = ApiRoutes.getUrl();
	int position = -1;
	String job;


    public POCheckInOutAsset() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		pendingCheckoutList = new ArrayList<>();
		pendingCheckInList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Initialize view
        rootview = inflater.inflate(R.layout.fragment_po_checkinout, container, false);

        tabLayout = rootview.findViewById(R.id.tabLayout_checkinout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				int position = tab.getPosition();

				switch (position){
					case 0:
						loadPendingCheckIn();
						break;

					case 1:
						loadPendingCheckout();
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

        recyclerView = rootview.findViewById(R.id.recyclerViewPendingCheckout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadPendingCheckIn();

        return rootview;
    }


//=================================================================================================================================

	private void loadPendingCheckout(){

		StringRequest pendingLoanReq = new StringRequest(Request.Method.POST, url + "getPendingCheckoutPO.php", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.getString("status").equals("Success")) {

						JSONArray jsonArray = jsonObject.getJSONArray("pendingList");
						pendingCheckoutList.clear();

						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject1 = jsonArray.getJSONObject(i);

							PendingCheckoutModel model = new PendingCheckoutModel(
									jsonObject1.getString("userId"),
									jsonObject1.getString("assetNo"),
									jsonObject1.getString("assetDescription"),
									jsonObject1.getString("dateFrom"),
									jsonObject1.getString("dateTo"),
									jsonObject1.getString("lockerRequest"),
									jsonObject1.getString("lockerId"),
									jsonObject1.getString("inventoryId"),
									jsonObject1.getString("lid")
							);
							pendingCheckoutList.add(model);
						}
						adapter = new POPendingCheckoutAdapter(pendingCheckoutList, getContext(), POCheckInOutAsset.this);
						recyclerView.setAdapter(adapter);
					} else if (jsonObject.getString("status").equals("No Record Found")){
						recyclerView.setAdapter(null);
						Snackbar.make(rootview, "No Pending Request", Snackbar.LENGTH_SHORT).show();
					} else if (jsonObject.getString("status").equals("Fail")){
						recyclerView.setAdapter(null);
						Snackbar.make(rootview, "Error getting data", Snackbar.LENGTH_SHORT).show();
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
				params.put("poId",SharedPrefManager.getInstance(getContext()).getUser().getId());
				return params;
			}
		};
		RequestQueue pendingLoanQueue = Volley.newRequestQueue(getContext());
		pendingLoanQueue.add(pendingLoanReq);
	}
//=================================================================================================================================

	private void loadPendingCheckIn(){

		StringRequest pendingReturnReq = new StringRequest(Request.Method.POST, url + "getPendingCheckInPO.php", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.getString("status").equals("Success")) {

						JSONArray jsonArray = jsonObject.getJSONArray("returnList");
						pendingCheckInList.clear();

						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject1 = jsonArray.getJSONObject(i);

							PendingCheckInModel model = new PendingCheckInModel(
									jsonObject1.getString("id"),
									jsonObject1.getString("lid"),
									jsonObject1.getString("userId"),
									jsonObject1.getString("assetNo"),
									jsonObject1.getString("assetDescription"),
									jsonObject1.getString("time"),
									jsonObject1.getString("location"),
									jsonObject1.getString("inventoryId")
							);
							pendingCheckInList.add(model);
						}
						adapter = new POPendingCheckInAdapter(pendingCheckInList, getContext(), POCheckInOutAsset.this);
						recyclerView.setAdapter(adapter);
					} else if (jsonObject.getString("status").equals("No Record Found")){
						recyclerView.setAdapter(null);
						Snackbar.make(rootview, "No Pending Request", Snackbar.LENGTH_SHORT).show();
					} else if (jsonObject.getString("status").equals("Fail")){
						recyclerView.setAdapter(null);
						Snackbar.make(rootview, "Error getting data", Snackbar.LENGTH_SHORT).show();
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
				params.put("poId",SharedPrefManager.getInstance(getContext()).getUser().getId());
				return params;
			}
		};
		RequestQueue pendingReturnQueue = Volley.newRequestQueue(getContext());
		pendingReturnQueue.add(pendingReturnReq);
	}

//=================================================================================================================================

	public void scanQR() {
		Intent i = new Intent(getActivity(), QrCodeScannerActivity.class);
		i.putExtra("calledFrom","POCheckInOutAsset");
		startActivityForResult(i, 1);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1) {
			if(resultCode == RESULT_OK) {
				String QRresult = data.getStringExtra("QrResult");

				//Check whether equipment is in a valid format
				Boolean validQRformat = checkQRformat(QRresult);

				Log.e("res", job);

				if(validQRformat && job.equals("checkin")){
					checkInAsset(position);
				} else if (validQRformat && job.equals("checkout")){
					checkoutAsset(pendingCheckoutList.get(position).getLoanId());
				} else {
					Snackbar.make(rootview, "Item mismatch.", Snackbar.LENGTH_SHORT).show();
				}

			}
		}
	}

	private boolean checkQRformat(String QrResult){
		String[] splitedStr = QrResult.split("-");
		if(splitedStr.length == 3){
			if(splitedStr[0].equalsIgnoreCase("LRMS") && (splitedStr[1].equalsIgnoreCase("EQP")
					|| splitedStr[1].equalsIgnoreCase("TOL") || splitedStr[1].equalsIgnoreCase("ACC")) && position > -1){

				if(job.equals("checkin")){
					return(splitedStr[2].equals(pendingCheckInList.get(position).getInventoryId()));
				} else if (job.equals("checkout")){
					return(splitedStr[2].equals(pendingCheckoutList.get(position).getAssetId()));
				}

			}
		}
		return false;
	}
//=================================================================================================================================
	public void setPosition(int position, String job){
		this.position = position;
		this.job = job;
	}

	private void checkoutAsset(final String loanId) {
		StringRequest checkoutReq = new StringRequest(Request.Method.POST, url + "POCheckOutAsset.php", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.getString("status").equals("Success")) {
						pendingCheckoutList.remove(position);
						adapter.notifyDataSetChanged();

						Snackbar.make(rootview, "Item checked out successfully!", Snackbar.LENGTH_SHORT).show();
					} else {
						Snackbar.make(rootview, "Error while checking out!", Snackbar.LENGTH_SHORT).show();
					}

				} catch (JSONException e) {
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
				params.put("loanId", loanId);
				return params;
			}
		};
		RequestQueue checkoutQueue = Volley.newRequestQueue(getContext());
		checkoutQueue.add(checkoutReq);
	}

	private void checkInAsset(final int position){
		final View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.custom_reason_dialog, null);

		TextView tvReason = dialogView.findViewById(R.id.textView_reason);
		tvReason.setText("Remarks");

		final EditText etReason = dialogView.findViewById(R.id.editTextReason);
		etReason.setHint("**OPTIONAL**");

		final AlertDialog dialog = new AlertDialog.Builder(getContext())
				.setView(dialogView)
				.setIcon(R.drawable.ic_action_info)
				.setTitle("Remarks")
				.setCancelable(false)
				.setNegativeButton(android.R.string.cancel, null)
				.setPositiveButton("Confirm", null)
				.create();

		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(final DialogInterface dialog) {
				Button button = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
				button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String remarks;
						if(!etReason.getText().toString().trim().isEmpty()){
							remarks = etReason.getText().toString().trim();
						} else {
							remarks = "No Remarks";
						}

						checkInAsset(position, remarks);
						dialog.dismiss();
					}
				});
			}
		});

		dialog.show();
	}

	private void checkInAsset(final int position, final String remarks) {
		StringRequest checkoutReq = new StringRequest(Request.Method.POST, url + "POCheckInAsset.php", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.getString("status").equals("Success")) {
						pendingCheckInList.remove(position);
						adapter.notifyDataSetChanged();

						Snackbar.make(rootview, "Item checked in successfully!", Snackbar.LENGTH_SHORT).show();
					} else {
						Snackbar.make(rootview, "Error while checking in!", Snackbar.LENGTH_SHORT).show();
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
				params.put("loanId", pendingCheckInList.get(position).getLoanId());
				params.put("inventoryId", pendingCheckInList.get(position).getInventoryId());
				params.put("returnId", pendingCheckInList.get(position).getReturnId());
				params.put("remarks", remarks);
				return params;
			}
		};
		RequestQueue checkoutQueue = Volley.newRequestQueue(getContext());
		checkoutQueue.add(checkoutReq);
	}

}
