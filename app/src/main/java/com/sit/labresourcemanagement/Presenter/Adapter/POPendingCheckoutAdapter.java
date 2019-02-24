package com.sit.labresourcemanagement.Presenter.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sit.labresourcemanagement.Presenter.Fragment.PO.POCheckInOutAsset;
import com.sit.labresourcemanagement.Model.ApiRoutes;
import com.sit.labresourcemanagement.Model.PO.PendingCheckoutModel;
import com.sit.labresourcemanagement.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class POPendingCheckoutAdapter extends RecyclerView.Adapter<POPendingCheckoutAdapter.ViewHolder> {

	List<PendingCheckoutModel> pendingCheckoutList;
	Context context;
	POCheckInOutAsset fragment;
	View view;
	String url = ApiRoutes.getUrl();

	TextView tvCampus, tvLocker, tvPin;

	public POPendingCheckoutAdapter(List<PendingCheckoutModel> pendingCheckoutList, Context context, POCheckInOutAsset fragment) {
		this.pendingCheckoutList = pendingCheckoutList;
		this.context = context;
		this.fragment = fragment;
	}

	class ViewHolder extends RecyclerView.ViewHolder{
		TextView userId, assetNumber, assetDescription, dateFrom, dateTo, lockerDetails, confirm;

		public ViewHolder(View itemView) {
			super(itemView);

			userId = itemView.findViewById(R.id.textView_checkout_userId);
			assetNumber = itemView.findViewById(R.id.textView_checkout_assetNumber);
			assetDescription = itemView.findViewById(R.id.textView_checkout_assetDesc);
			dateFrom = itemView.findViewById(R.id.textView_checkout_dateFrom);
			dateTo = itemView.findViewById(R.id.textView_checkout_dateTo);
			lockerDetails = itemView.findViewById(R.id.textViewLockerDetails);
			confirm = itemView.findViewById(R.id.textViewConfirm);
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		view = LayoutInflater.from(context).inflate(R.layout.card_po_checkout_detail, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		final PendingCheckoutModel model = pendingCheckoutList.get(position);

		holder.userId.setText(model.getUserId());
		holder.assetNumber.setText(model.getAssetNumber());
		holder.assetDescription.setText(model.getAssetDescription());
		holder.dateFrom.setText(model.getDateFrom());
		holder.dateTo.setText(model.getDateTo());

		if(model.getLocker()){
			holder.lockerDetails.setVisibility(View.VISIBLE);

			//getLockerDetails

			holder.lockerDetails.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					lockerDialog(model.getLockerId());
				}
			});
		} else {
			holder.lockerDetails.setVisibility(View.GONE);
		}

		holder.confirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				fragment.setPosition(position, "checkout");
				fragment.scanQR();
			}
		});
	}

	@Override
	public int getItemCount() {
		return pendingCheckoutList.size();
	}
//===========================================================================================================================================

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
