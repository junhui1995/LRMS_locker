package com.sit.labresourcemanagement.Model;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

import com.sit.labresourcemanagement.R;

public class RejectDialog {

	EditText etReason;
	Button btnReject, btnCancel;

	public void showDialog (Context context){
		final Dialog dialog = new Dialog(context);

		dialog.setContentView(R.layout.custom_reason_dialog);
		//dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);

		etReason = (EditText) dialog.findViewById(R.id.editTextReason);
//		btnReject = (Button) dialog.findViewById(R.id.buttonReject);
//		btnCancel = (Button) dialog.findViewById(R.id.buttonCancel);
//
//		btnReject.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Snackbar.make(v, "Request Rejected!", Snackbar.LENGTH_SHORT).show();
//			}
//		});
//		btnCancel.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				dialog.dismiss();
//			}
//		});

		dialog.show();
	}
}
