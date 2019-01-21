package com.sit.labresourcemanagement.Presenter.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sit.labresourcemanagement.Model.ApiRoutes;
import com.sit.labresourcemanagement.Model.LockerDetails;
import com.sit.labresourcemanagement.Model.PO.PendingLoanModel;
import com.sit.labresourcemanagement.Model.SharedPrefManager;
import com.sit.labresourcemanagement.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class POLoanPendingApprovalAdapter extends RecyclerView.Adapter<POLoanPendingApprovalAdapter.ViewHolder> {
	private static final String ERROR_MESSAGE = "An error occurred!";

    List<PendingLoanModel> pendingApprovalList;
    List<LockerDetails> lockerModelList;
    Context context;
	String url = ApiRoutes.getUrl();
	View view;
	Boolean mLockerAvailable;
	List<String> locationList, boxList;
	List<NumberPicker> numberPickerList;
	ArrayAdapter<String> locationAdapter, boxesAdapter;

    public POLoanPendingApprovalAdapter(List<PendingLoanModel> pendingApprovalList, Context context) {
        this.pendingApprovalList = pendingApprovalList;
        this.context = context;

        mLockerAvailable = true;
        locationList = new ArrayList<>();
        boxList = new ArrayList<>();
        lockerModelList = new ArrayList<>();
        numberPickerList = new ArrayList<>();

        getLockers();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_po_pending_approval_loan_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PendingLoanModel pendingLoanModel = pendingApprovalList.get(position);

        holder.userId_pendingApprov.setText(pendingLoanModel.getUserId_pendingApprov());
        holder.assetNo_pendingApprov.setText(pendingLoanModel.getAssetNo_pendingApprov());
        holder.assetDesc_pendingApprov.setText(pendingLoanModel.getAssetDesc_pendingApprov());
        holder.datefrom_pendingApprov.setText(pendingLoanModel.getDateFrom_pendingApprov());
        holder.dateto_pendingApprov.setText(pendingLoanModel.getDateTo_pendingApprov());
        holder.reason_pendingApprov.setText(pendingLoanModel.getReason_pendingApprov());
        holder.locker_pendingApprov.setText(pendingLoanModel.getLockerRequest_pendingApprov());

        if (pendingLoanModel.getLockerRequest_pendingApprov().equals("Yes")){
        	holder.locker_pendingApprov.setTextColor(context.getResources().getColor(R.color.green));
		} else {
			holder.locker_pendingApprov.setTextColor(context.getResources().getColor(R.color.red));
		}

        holder.ibAccept.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (pendingLoanModel.getLockerRequest_pendingApprov().equals("Yes")){
					if(mLockerAvailable)
						approveDialog(position);
					else
						Snackbar.make(view, "No locker available", Snackbar.LENGTH_SHORT).show();
				} else {
					approveLoan(pendingLoanModel.getLoanid_pendingApprov(), position);
				}
			}
		});

        holder.ibReject.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rejectDialog(position);
			}
		});
    }

    @Override
    public int getItemCount() {
        return pendingApprovalList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView userId_pendingApprov,assetNo_pendingApprov,assetDesc_pendingApprov,datefrom_pendingApprov,
				dateto_pendingApprov,reason_pendingApprov,locker_pendingApprov;
        ImageButton ibAccept, ibReject;

        public ViewHolder(View itemView) {
            super(itemView);

            userId_pendingApprov=itemView.findViewById(R.id.userId_pendingApproval);
            assetNo_pendingApprov=itemView.findViewById(R.id.assetNumber_pendingApproval);
            assetDesc_pendingApprov=itemView.findViewById(R.id.assetDescription_pendingApproval);
            datefrom_pendingApprov=itemView.findViewById(R.id.date_from_pendingApproval);
            dateto_pendingApprov=itemView.findViewById(R.id.date_to_pendingApproval);
            reason_pendingApprov=itemView.findViewById(R.id.reason_pendingApprovalPo);
            locker_pendingApprov=itemView.findViewById(R.id.locker_pendingApprovalPo);

            ibAccept = itemView.findViewById(R.id.imageButtonAccept);
            ibReject = itemView.findViewById(R.id.imageButtonReject);
        }
    }

// Reject =======================================================================================================================================
	private void rejectDialog(final int position){
		final View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_reason_dialog, null);
		final EditText etReason = dialogView.findViewById(R.id.editTextReason);

		final AlertDialog dialog = new AlertDialog.Builder(context)
				.setView(dialogView)
				.setIcon(R.drawable.ic_cross)
				.setTitle("Rejecting")
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
						if(!etReason.getText().toString().trim().isEmpty()){
							String reason = etReason.getText().toString().trim();
							String inventoryID = pendingApprovalList.get(position).getInventoryid_pendingApprov();
							String loanID = pendingApprovalList.get(position).getLoanid_pendingApprov();

							rejectLoan(loanID, reason, inventoryID, position);

							dialog.dismiss();
						} else {
							Snackbar.make(dialogView, "Please enter a reason.", Snackbar.LENGTH_SHORT).show();
						}
					}
				});
			}
		});

		dialog.show();
	}

	private void rejectLoan(final String loanID, final String reason, final String inventoryID, final int position ) {
		StringRequest decline_req= new StringRequest(Request.Method.POST, url + "rejectLoan.php", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject=new JSONObject(response);
					if (jsonObject.getString("status").equals("Success")){
						Snackbar.make(view, "Loan Rejected", Snackbar.LENGTH_SHORT).show();
						removeFromList(position);
					} else {
						Snackbar.make(view, ERROR_MESSAGE, Snackbar.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					//e.printStackTrace();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Snackbar.make(view, ERROR_MESSAGE, Snackbar.LENGTH_SHORT).show();
				//error.printStackTrace();
			}
		}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String>  params = new HashMap<String, String>();
				params.put("poId", SharedPrefManager.getInstance(context).getUser().getId());
				params.put("loanId",loanID);
				params.put("reason", reason);
				params.put("inventoryId", inventoryID);
				return params;
			}
		};
		RequestQueue decline_que= Volley.newRequestQueue(context);
		decline_que.add(decline_req);
	}

// Approve =======================================================================================================================================

	private void approveDialog(final int position) {
		final View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_locker_dialog, null);

		final Spinner spinnerLocation = dialogView.findViewById(R.id.spinnerLocation);
		final Spinner spinnerLocker = dialogView.findViewById(R.id.spinnerLocker);

		final TextView tvPin = dialogView.findViewById(R.id.tvPin);


		//Set items for location spinner
		locationAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, locationList);
		spinnerLocation.setAdapter(locationAdapter);

		//Set items for locker spinner
		getBoxes(locationList.get(0));
		boxesAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, boxList);
		spinnerLocker.setAdapter(boxesAdapter);


		spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				getBoxes(parent.getItemAtPosition(position).toString());
				boxesAdapter.notifyDataSetChanged();
				spinnerLocker.setAdapter(boxesAdapter);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		spinnerLocker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				Random r = new Random();
				int pin = r.nextInt(9999) + 0;
				if (pin < 1000) {
					tvPin.setText("0" + pin);
				} else {
					tvPin.setText("" + pin);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});


		final AlertDialog dialog = new AlertDialog.Builder(context)
				.setView(dialogView)
				.setIcon(R.drawable.ic_tick)
				.setTitle("Locker")
				.setCancelable(false)
				.setNegativeButton(android.R.string.cancel, null)
				.setPositiveButton("Confirm", null)
				.create();

		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(final DialogInterface dialog) {
				Button btnConfirm = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
				btnConfirm.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String pin = "";
						String lockerId = getLockerId(spinnerLocation.getSelectedItem().toString(), spinnerLocker.getSelectedItem().toString());

/*
						for(int i = 0; i < numberPickerList.size(); i++)
							pin += numberPickerList.get(i).getValue();
*/
						pin = tvPin.getText().toString();

						approveLoan(pendingApprovalList.get(position).getLoanid_pendingApprov(), position, lockerId, pin);

						dialog.dismiss();
					}
				});
			}
		});
		dialog.show();
	}

	private void approveLoan(final String loanID, final int position){
    	approveLoan(loanID, position, "No Locker", "0000");
	}

	private void approveLoan(final String loanID, final int position, final String lockerId, final String pin) {
		StringRequest approve_req=new StringRequest(Request.Method.POST, url+"approveLoan.php", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					Log.e("response", response);

					JSONObject jsonObject=new JSONObject(response);
					if (jsonObject.getString("status").equals("Success")){
						if(!lockerId.equals("No Locker")){
							boxList.remove(lockerId);
						}
						Snackbar.make(view, "Loan approved!", Snackbar.LENGTH_SHORT).show();
						removeFromList(position);
					}
					else {
						getLockers();
						Snackbar.make(view, ERROR_MESSAGE, Snackbar.LENGTH_SHORT).show();
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
				params.put("poId",SharedPrefManager.getInstance(context).getUser().getId());
				params.put("loanId",loanID);
				params.put("lockerId", lockerId);
				params.put("pin", pin);
				return params;
			}
		};
		RequestQueue approve_que= Volley.newRequestQueue(context);
		approve_que.add(approve_req);
	}

	private void removeFromList(final int position){
		pendingApprovalList.remove(position);
		notifyItemRemoved(position);
		notifyItemRangeChanged(position, getItemCount());
	}

// Lockers =======================================================================================================================================
	private void getLockers(){
    	StringRequest location_req = new StringRequest(Request.Method.POST, url + "getLockers.php", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject responseObject = new JSONObject(response);

					if (responseObject.getString("status").equals("Success")){
						mLockerAvailable = true;
						JSONArray locationArray = responseObject.getJSONArray("lockerList");
						lockerModelList.clear();

						for(int i = 0; i < locationArray.length(); i++){
							JSONObject locationObject = locationArray.getJSONObject(i);
							LockerDetails model = new LockerDetails(
									locationObject.getString("id"),
									locationObject.getString("name"),
									locationObject.getString("location")
							);
							lockerModelList.add(model);
						}

					} else if (responseObject.getString("status").equals("No Record Found")) {
						mLockerAvailable = false;
					}

				} catch (JSONException e){
					//e.printStackTrace();
				}
			}
		}, new Response.ErrorListener(){
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
    	RequestQueue locker_queue = Volley.newRequestQueue(context);
    	locker_queue.add(location_req);

    	locker_queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<StringRequest>() {
			@Override
			public void onRequestFinished(Request<StringRequest> request) {
				getLocation();
			}
		});
	}

	private void getLocation() {
    	locationList.clear();
    	for(int i = 0; i < lockerModelList.size(); i++){
    		String location = lockerModelList.get(i).getLocation();
    		if(!locationList.contains(location))
    			locationList.add(location);
		}
	}

	private void getBoxes(String location){
    	LockerDetails tempLockerModel;
    	boxList.clear();
		for(int i = 0; i < lockerModelList.size(); i++){
			tempLockerModel = lockerModelList.get(i);
			String box = tempLockerModel.getName();

			if(tempLockerModel.getLocation().equals(location) && !boxList.contains(box))
				boxList.add(box);
		}
	}

	private String getLockerId(String location, String box){
		LockerDetails tempLockerModel;
    	for(int i = 0; i < lockerModelList.size(); i++){
    		tempLockerModel = lockerModelList.get(i);
    		if(tempLockerModel.getLocation().equals(location) && tempLockerModel.getName().equals(box))
    			return tempLockerModel.getId();
		}
		return "";
	}

}
