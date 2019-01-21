package com.sit.labresourcemanagement.Presenter.Adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sit.labresourcemanagement.Model.ApiRoutes;
import com.sit.labresourcemanagement.Model.PO.PendingBookingModel;
import com.sit.labresourcemanagement.Model.SharedPrefManager;
import com.sit.labresourcemanagement.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class POBookingPendingApprovalAdapter extends RecyclerView.Adapter<POBookingPendingApprovalAdapter.ViewHolder>{
	List<PendingBookingModel> pendingBookingList;
	Context context;
	View view;
	String url = ApiRoutes.getUrl();
	ArrayList<String> bookingIDs;

	public POBookingPendingApprovalAdapter(List<PendingBookingModel> pendingBookingList, Context context) {
		this.pendingBookingList = pendingBookingList;
		this.context = context;
		this.bookingIDs = new ArrayList<>();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_po_pending_approval_booking_layout, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		final PendingBookingModel pendingBookingModel = pendingBookingList.get(position);

		bookingIDs.add(pendingBookingModel.getBookingId());

		holder.tvUserid.setText(pendingBookingModel.getUserId());
		holder.tvLabid.setText(pendingBookingModel.getLabId());
		holder.tvWorkbenchid.setText(pendingBookingModel.getWorkbenchId());
		holder.tvDate.setText(pendingBookingModel.getDate());
		holder.tvTime.setText(pendingBookingModel.getTimeFrom() + " - " + pendingBookingModel.getTimeTo());
		holder.tvReason.setText(pendingBookingModel.getReason());
		holder.ibAccept.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				acceptRequest(bookingIDs.get(position));
				pendingBookingList.remove(position);
				bookingIDs.remove(position);
				notifyItemRemoved(position);
				notifyItemRangeChanged(position, getItemCount());
			}
		});
		holder.ibReject.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rejectRequest(bookingIDs.get(position));
				pendingBookingList.remove(position);
				bookingIDs.remove(position);
				notifyItemRemoved(position);
				notifyItemRangeChanged(position, getItemCount());
			}
		});
	}


	@Override
	public int getItemCount() {
		return pendingBookingList.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		TextView tvUserid, tvLabid, tvWorkbenchid, tvDate, tvTime, tvReason;
		ImageButton ibAccept, ibReject;

		public ViewHolder(View itemView) {
			super(itemView);

			tvUserid = itemView.findViewById(R.id.textViewUid);
			tvLabid = itemView.findViewById(R.id.textViewLabid);
			tvWorkbenchid = itemView.findViewById(R.id.textViewBenchid);
			tvDate = itemView.findViewById(R.id.textViewDate);
			tvTime = itemView.findViewById(R.id.textViewTime);
			tvReason = itemView.findViewById(R.id.textViewReason);
			ibAccept = itemView.findViewById(R.id.imageButtonAccept);
			ibReject = itemView.findViewById(R.id.imageButtonReject);
		}
	}

	private void acceptRequest(final String bookingID){
		StringRequest decline_req= new StringRequest(Request.Method.POST, url + "approveBooking.php", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject=new JSONObject(response);
					if (jsonObject.getString("status").equals("Success")){
						Snackbar.make(view, "Booking was Approved", Snackbar.LENGTH_SHORT).show();
					} else {
						Snackbar.make(view, "An error occurred", Snackbar.LENGTH_SHORT).show();
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
				params.put("poId", SharedPrefManager.getInstance(context).getUser().getId());
				params.put("bid",bookingID);
				return params;
			}
		};
		RequestQueue decline_que= Volley.newRequestQueue(context);
		decline_que.add(decline_req);
	}

	private void rejectRequest(final String bookingID){
		StringRequest decline_req= new StringRequest(Request.Method.POST, url + "rejectBooking.php", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject=new JSONObject(response);
					if (jsonObject.getString("status").equals("Success")){
						Snackbar.make(view, "Booking was Rejected", Snackbar.LENGTH_SHORT).show();
					}else {
						Snackbar.make(view, "An error occurred", Snackbar.LENGTH_SHORT).show();
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
				params.put("poId", SharedPrefManager.getInstance(context).getUser().getId());
				params.put("bid",bookingID);
				return params;
			}
		};
		RequestQueue decline_que= Volley.newRequestQueue(context);
		decline_que.add(decline_req);
	}
}
