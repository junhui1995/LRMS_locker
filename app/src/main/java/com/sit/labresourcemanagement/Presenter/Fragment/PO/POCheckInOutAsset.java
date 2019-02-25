package com.sit.labresourcemanagement.Presenter.Fragment.PO;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.WriterException;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.WINDOW_SERVICE;
import static android.support.v4.content.FileProvider.getUriForFile;


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
	public final static int QRcodeWidth = 500 ;
	String inputValue;
	Bitmap bitmap;
	String TAG = "GenerateQRCode";
	String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/QRCode/";



	QRGEncoder qrgEncoder;
    boolean save;
    String result;

    String inventoryid;


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
		loadPendingCheckout();
        tabLayout = rootview.findViewById(R.id.tabLayout_checkinout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				int position = tab.getPosition();

				switch (position){
					case 0:
						loadPendingCheckout();
						break;

					case 1:

						loadPendingCheckIn();
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


		//Snackbar.make(rootview, "Error", Snackbar.LENGTH_SHORT).show();

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
					Log.e(">>>>>>WRONG", response);
					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.getString("status").equals("Success")) {

						JSONArray jsonArray = jsonObject.getJSONArray("returnList");
						pendingCheckInList.clear();

						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject1 = jsonArray.getJSONObject(i);

							PendingCheckInModel model = new PendingCheckInModel(

									jsonObject1.getString("userId"),
									jsonObject1.getString("assetNo"),
									jsonObject1.getString("assetDescription"),
									jsonObject1.getString("loanid"),

									jsonObject1.getString("lockername"),
									jsonObject1.getString("pin"),
                                    jsonObject1.getString("inventoryId")

									//jsonObject1.getString("pin")
									//no need time, location, inventory
									//jsonObject1.getString("time"),
									//jsonObject1.getString("location"),

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
					e.printStackTrace();
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
					//checkInAsset(position);
					checkInAsset(pendingCheckInList.get(position).getLoanId());
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
					inventoryid = splitedStr[2];
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


					   generateQRcode(inventoryid);
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

	private void generateQRcode(String inventoryID)
	{
		inputValue = "LRMS-EQP-" + inventoryID;
		WindowManager manager = (WindowManager) getContext().getSystemService(WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		Point point = new Point();
		display.getSize(point);
		int width = point.x;
		int height = point.y;
		int smallerDimension = width < height ? width : height;
		smallerDimension = smallerDimension * 3 / 4;

		qrgEncoder = new QRGEncoder(
				inputValue, null,
				QRGContents.Type.TEXT,
				smallerDimension);
		try {
			bitmap = qrgEncoder.encodeAsBitmap();
			//qrImage.setImageBitmap(bitmap);
			save = QRGSaver.save(savePath, inputValue, bitmap, QRGContents.ImageType.IMAGE_JPEG);
			result = save ? "Image Saved" : "Image Not Saved";

			sendEmail(inputValue + ".jpg");
			Log.i(">>>>>>savepath", savePath );
			//Toast.makeText(rootview, result+save+savePath, Toast.LENGTH_LONG).show();
		} catch (WriterException e) {
			Log.v(TAG, e.toString());
		}


	}
	private void sendEmail(String filename)
	{

		File imagePath = new File(Environment.getExternalStorageDirectory(), "QRCodes");
		File newFile = new File(imagePath, filename);
		Uri contentUri = getUriForFile(getContext(), "com.sit.labresourcemanagement.provider", newFile);

		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL, new String[]{"ronaldohiew95@gmail.com","josephchai100@hotmail.com","2355387t@student.gla.ac.uk"});
		i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
		i.putExtra(Intent.EXTRA_TEXT, "Receipt of Loan: " + "\n" + "LoanID:" + pendingCheckoutList.get(position).getLoanId()+ "\n" + "Userid: " + pendingCheckoutList.get(position).getUserId() + "\n" + "Resource Name: " + pendingCheckoutList.get(position).getAssetDescription() + "\n" + "Resource Number: " + pendingCheckoutList.get(position).getAssetNumber()+ "\n" + "Date loaned from: " + pendingCheckoutList.get(position).getDateFrom() + " to " + pendingCheckoutList.get(position).getDateTo() + "\n" + "\n" + "Please proceed to the designated locker to retrieve your resource. The locker number and location can be found in the LRMS mobile application.");
		i.putExtra(Intent.EXTRA_STREAM,  contentUri);

		try {
			startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Snackbar.make(rootview, "There are no email clients installed.", Snackbar.LENGTH_SHORT).show();
			//Toast.makeText(POCheckInOutAsset.this, "", Toast.LENGTH_SHORT).show();
		}
	}
	private void checkInAsset(final String loanId){
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

						checkInAsset(loanId, remarks);
						dialog.dismiss();
					}
				});
			}
		});

		dialog.show();
	}

	private void checkInAsset(final String loanID, final String remarks) {
		StringRequest checkinReq = new StringRequest(Request.Method.POST, url + "POCheckInAsset.php", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
                Log.i(">>>>wrong", response);
			    try {
					JSONObject jsonObject = new JSONObject(response);


					if (jsonObject.getString("status").equals("Success")) {
						pendingCheckInList.remove(position);
						adapter.notifyDataSetChanged();

						Snackbar.make(rootview, "Item checked in successfully!", Snackbar.LENGTH_SHORT).show();
						//freelocker();
					} else if (jsonObject.getString("status").equals("Fail")) {
						Snackbar.make(rootview, "Error while checking in!", Snackbar.LENGTH_SHORT).show();
					}
					else
					{
						Snackbar.make(rootview, "Unknown Error", Snackbar.LENGTH_SHORT).show();
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
				params.put("loanId", loanID);
				params.put("inventoryId", pendingCheckInList.get(position).getInventoryId());
				//params.put("returnId", pendingCheckInList.get(position).getReturnId());
				params.put("remarks", remarks);
				return params;
			}
		};
		RequestQueue checkoutQueue = Volley.newRequestQueue(getContext());
		checkoutQueue.add(checkinReq);
	}
	private void freelocker() {
		StringRequest checkoutReq = new StringRequest(Request.Method.POST, url + "freelocker.php", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.getString("status").equals("Success")) {
						Snackbar.make(rootview, "Please rmb to lock back the locker!", Snackbar.LENGTH_SHORT).show();
					} else {
						//Snackbar.make(rootview, "Error while checking in!", Snackbar.LENGTH_SHORT).show();
					}

				} catch (JSONException e) {
					//e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("poId",SharedPrefManager.getInstance(getContext()).getUser().getId());
				params.put("loanId", pendingCheckInList.get(position).getLoanId());
				return params;
			}
		};
		RequestQueue checkoutQueue = Volley.newRequestQueue(getContext());
		checkoutQueue.add(checkoutReq);
	}


}
