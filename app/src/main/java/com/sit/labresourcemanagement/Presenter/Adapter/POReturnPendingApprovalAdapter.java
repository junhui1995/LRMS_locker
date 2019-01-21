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
import android.widget.Button;
import android.widget.EditText;
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
import com.sit.labresourcemanagement.Model.PO.PendingReturnModel;
import com.sit.labresourcemanagement.Model.SharedPrefManager;
import com.sit.labresourcemanagement.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class POReturnPendingApprovalAdapter extends RecyclerView.Adapter<POReturnPendingApprovalAdapter.ViewHolder> {
	private static final String ERROR_MESSAGE = "An error occurred!";

	List<PendingReturnModel> pendingReturnList;
	Context context;
	String url = ApiRoutes.getUrl();
	View view;

	public POReturnPendingApprovalAdapter(List<PendingReturnModel> pendingReturnList, Context context) {
		this.pendingReturnList = pendingReturnList;
		this.context = context;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		view = LayoutInflater.from(context).inflate(R.layout.card_po_pending_approval_return_layout, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		final PendingReturnModel model = pendingReturnList.get(position);

		holder.tvUserId.setText(model.getUserId());
		holder.tvAssetNumber.setText(model.getAssetNumber());
		holder.tvAssetDetail.setText(model.getAssetDescription());
		holder.tvDateTime.setText(model.getDateTime());
		holder.tvLocation.setText(model.getLocation());

		holder.ibAccept.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				approveRequest(position);
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
		return pendingReturnList.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {
		TextView tvUserId, tvAssetNumber, tvAssetDetail, tvDateTime, tvLocation;
		ImageButton ibAccept, ibReject;

		public ViewHolder(View itemView) {
			super(itemView);

			tvUserId = itemView.findViewById(R.id.tvreturnuserid);
			tvAssetNumber = itemView.findViewById(R.id.tvreturnassetno);
			tvAssetDetail = itemView.findViewById(R.id.tvreturnitemname);
			tvDateTime = itemView.findViewById(R.id.tvreturnitemdate);
			tvLocation = itemView.findViewById(R.id.tvreturnstatus);

			ibAccept = itemView.findViewById(R.id.imageButtonAccept);
			ibReject = itemView.findViewById(R.id.imageButtonReject);
		}
	}

//	 Reject =======================================================================================================================================
	private void rejectDialog(final int position) {
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
				Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
				button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!etReason.getText().toString().trim().isEmpty()) {
							String reason = etReason.getText().toString().trim();
							rejectRequest(reason, position);
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

	private void rejectRequest(final String reason, final int position) {
		StringRequest decline_req = new StringRequest(Request.Method.POST, url + "rejectReturn.php", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					Log.e("response", response);
					JSONObject jsonObject = new JSONObject(response);
					if (jsonObject.getString("status").equals("Success")) {
						Snackbar.make(view, "Request Rejected", Snackbar.LENGTH_SHORT).show();
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
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("poId", SharedPrefManager.getInstance(context).getUser().getId());
				params.put("returnId", pendingReturnList.get(position).getReturnId());
				params.put("reason", reason);
				return params;
			}
		};
		RequestQueue decline_que = Volley.newRequestQueue(context);
		decline_que.add(decline_req);
	}

//	Approve =======================================================================================================================================
	private void approveRequest(final int position) {
		StringRequest approve_req = new StringRequest(Request.Method.POST, url + "approveReturn.php", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject=new JSONObject(response);
					if (jsonObject.getString("status").equals("Success")){
						Snackbar.make(view, "Request approved!", Snackbar.LENGTH_SHORT).show();
						removeFromList(position);
					}
					else {
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
				params.put("returnId", pendingReturnList.get(position).getReturnId());
				params.put("loanId", pendingReturnList.get(position).getLoanId());
				return params;
			}
		};
		RequestQueue approve_que= Volley.newRequestQueue(context);
		approve_que.add(approve_req);
	}

//	=======================================================================================================================================
	private void removeFromList(final int position){
		pendingReturnList.remove(position);
		notifyItemRemoved(position);
		notifyItemRangeChanged(position, getItemCount());
	}
}
