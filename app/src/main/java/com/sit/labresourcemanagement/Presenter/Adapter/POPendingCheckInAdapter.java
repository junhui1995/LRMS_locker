package com.sit.labresourcemanagement.Presenter.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sit.labresourcemanagement.Presenter.Fragment.PO.POCheckInOutAsset;
import com.sit.labresourcemanagement.Model.PO.PendingCheckInModel;
import com.sit.labresourcemanagement.R;

import java.util.List;

public class POPendingCheckInAdapter extends RecyclerView.Adapter<POPendingCheckInAdapter.ViewHolder> {

	List<PendingCheckInModel> pendingCheckinList;
	Context context;
	POCheckInOutAsset fragment;
	View view;

	public POPendingCheckInAdapter(List<PendingCheckInModel> pendingCheckinList, Context context, POCheckInOutAsset fragment) {
		this.pendingCheckinList = pendingCheckinList;
		this.context = context;
		this.fragment = fragment;
	}

	class ViewHolder extends RecyclerView.ViewHolder{
		TextView userId, assetNumber, assetDescription, locker, loanid, checkIn, pin;

		public ViewHolder(View itemView) {
			super(itemView);

			userId = itemView.findViewById(R.id.textView_check_in_userId);
			assetNumber = itemView.findViewById(R.id.textView_check_in_assetNumber);
			assetDescription = itemView.findViewById(R.id.textView_check_in_assetDescription);
			loanid = itemView.findViewById(R.id.textView_checkin_loanid);
			locker = itemView.findViewById(R.id.textView_check_in_locker);
			pin = itemView.findViewById(R.id.textView_checkin_pin);
			checkIn = itemView.findViewById(R.id.textView_check_in_asset);
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		view = LayoutInflater.from(context).inflate(R.layout.card_po_checkin_detail, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		final PendingCheckInModel model = pendingCheckinList.get(position);

		holder.userId.setText(model.getUserId());
		holder.assetNumber.setText(model.getAssetNumber());
		holder.assetDescription.setText(model.getAssetDescription());
		holder.loanid.setText(model.getLoanId());
		holder.locker.setText(model.getLocker());
		holder.pin.setText(model.getPin());
		//holder.dateTime.setText(model.getDateTime());
		//holder.location.setText(model.getLocation());


		holder.checkIn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fragment.setPosition(position, "checkin");
				fragment.scanQR();
			}
		});
	}

	@Override
	public int getItemCount() {
		return pendingCheckinList.size();
	}
}
