package com.sit.labresourcemanagement.Presenter.Fragment.Student;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.sit.labresourcemanagement.Presenter.Activity.MainActivity;
import com.sit.labresourcemanagement.Model.ApiRoutes;
import com.sit.labresourcemanagement.Model.SharedPrefManager;
import com.sit.labresourcemanagement.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StudentNewLoanFragment extends Fragment {

    ImageView ivdateFrom,ivdateTo,ivTimeLoan , ivTimeReturn;


    EditText dateRequest,dateDue , timeLoan , timeReturnLoan;
    EditText reason;
    Spinner categories;
    Spinner item;
    TextView tvItem,tvCategory , tvLocation;

    ArrayList arrayListOfCategories=new ArrayList();
    ArrayList arrayListOfItems = new ArrayList();
    ArrayList arrayIDs = new ArrayList();

    private String retrievedCategory;
    private String retrievedItem;
    private String selected_category,item_name,selectedItemID;
    private String url = ApiRoutes.getUrl();
    private String dateFromAndTimeLoan = "";
    private String dateToAndTimeReturnLoan = "";
    private String dateFrom;
    private String timeLoaning;
    private String dateTo;
    private String timeReturning;
    private String reasonToLoan;
    private String currDateTime;
    private String currentDateTime;
    private String location;
    private String inventoryAssetDescription;
    private String lockerRequest = "Yes";
    private String category = "";
    private String prevFragment = "";
    private String tag = "";
    private Boolean itemData = false;
    private HashMap<String,String> hminventoryIDtoLocation = new HashMap<String,String>();


    Button btn_submit, btn_Learning_Gallery;
    ProgressDialog progressDialog;

    View view;

    public StudentNewLoanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		((MainActivity) getActivity()).disableDrawer();
        Bundle bundle =getArguments();

        if(null!=bundle) {

            //Receives ID and its description from previous fragment if bundle is not null
            selectedItemID = bundle.getString("InventoryID");
            inventoryAssetDescription = bundle.getString("assetDescription");
            category = bundle.getString("category");
            location = bundle.getString("location");
            tag = bundle.getString("tag");




            prevFragment = "Scan";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle =getArguments();
        //Initialize progress dialog
        progressDialog = new ProgressDialog(getContext());

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_student_new_loan, container, false);


        // Declaration of Widgets
        categories = view.findViewById(R.id.categories);
        reason = view.findViewById(R.id.reason_editText);
        item = view.findViewById(R.id.spiItem);
        ivdateFrom = view.findViewById(R.id.ivdateFrom);
        ivdateTo = view.findViewById(R.id.ivdateTo);
        ivTimeLoan = view.findViewById(R.id.ivtimeLoan);
        ivTimeReturn = view.findViewById(R.id.ivTimeReturn);
        timeLoan = view.findViewById(R.id.ettimeLoan);
        timeReturnLoan = view.findViewById(R.id.ettimeReturn);
        dateRequest = view.findViewById(R.id.dateRequest);
        dateDue = view.findViewById(R.id.dateTo);
        btn_submit = view.findViewById(R.id.btn_submit);
        tvCategory = view.findViewById(R.id.category_text);
        tvItem = view.findViewById(R.id.tvItem);
        tvLocation = view.findViewById(R.id.tvLocation);
        btn_Learning_Gallery = view.findViewById(R.id.btn_search_equipment);



/*        //Get current time stamp
        currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());

        //Split it into date and time
        String[] splitDateTimes = currentDateTime.split(" ");
        //Set the date request and time loan to current date time
        dateRequest.setText(splitDateTimes[0]);
        timeLoan.setText(splitDateTimes[1]);

        String dateInString = splitDateTimes[0];  // Start date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dateInString));
            c.add(Calendar.DATE, 3);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date resultdate = new Date(c.getTimeInMillis());
        dateInString = sdf.format(resultdate);
        dateDue.setText(dateInString);
        timeReturnLoan.setText(dateInString);*/
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        dateRequest.setText(mYear + "-" + (mMonth + 1) + "-" + mDay);
        timeLoan.setText(mHour + ":" + mMinute+":"+ "00");
        dateDue.setText(mYear + "-" + (mMonth + 1) + "-" + (mDay+3));
        timeReturnLoan.setText(mHour + ":" + mMinute+ ":" +"00");

        //Check if selected category is empty. If its not empty means previous fragment was StudentScanToLoanAsset,just display new loan widgets normally
        if(TextUtils.isEmpty(selectedItemID)){

            // Start Calendar listener from image calendar listener
            ivdateFrom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Get Current Date
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);

                    //Select Date From
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    dateRequest.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                    dateDue.setText(year + "-" + (monthOfYear+1) + "-" + (dayOfMonth+3));
                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() + 259200000 - 1000);
                    datePickerDialog.show();



                }
            });

            // Set listener for Times
            ivTimeLoan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Get Time loaning item
                    final Calendar c = Calendar.getInstance();
                    int mHour = c.get(Calendar.HOUR_OF_DAY);
                    int mMinute = c.get(Calendar.MINUTE);

                    // Launch Time Picker Dialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minute) {

                                    timeLoan.setText(hourOfDay + ":" + minute + ":" + "00");
                                }
                            }, mHour, mMinute, false);
                    timePickerDialog.show();


                }
            });
                // Set listener for spinners
                categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        selected_category = arrayListOfCategories.get(i).toString();

                        //Make sure item list is empty before retrieving data from db
                        arrayListOfItems.clear();
                        arrayIDs = new ArrayList();
                        //Set text view location to false since you are clearing arrayIDs so it would not display inaccurate details
                        tvLocation.setVisibility(View.INVISIBLE);
                        getItemdata();
/*
                        for (int j=0;j<arrayListOfCategories.size();j++)
                        {
                            if (arrayListOfCategories.get(j).toString().equals(category))

                            categories.setSelection(j);break;
                        }
*/




                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });


            item.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    item_name=arrayListOfItems.get(i).toString();
                    selectedItemID = arrayIDs.get(i).toString();

                    //Set location of item to text
                    tvLocation.setVisibility(View.VISIBLE);
                    tvLocation.setText("Location of item : " + hminventoryIDtoLocation.get(selectedItemID));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            getSpinnerData();
        }
        else{

            //Since there is selected item set itemData to true
            itemData = true;

            //Get current time stamp
            currDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());

            //Split it into date and time

            String[] splitDateTime = currDateTime.split(" ");

            //If its not empty mean user has already scan an equipment that he/she wants to loan therefore don need to give user a choice to choose equipment
            ivdateFrom.setVisibility(View.INVISIBLE);
            ivTimeLoan.setVisibility(View.INVISIBLE);

            //Set the date request and time loan to current date time
            dateRequest.setText(splitDateTime[0]);
            timeLoan.setText(splitDateTime[1]);

            //disable dateRequest and time loan edit text to prevent user from editing them
            dateRequest.setEnabled(false);
            timeLoan.setEnabled(false);

            //Inside Equipment into category's spinner and the ID with assetdescription into the item spinner
            categories.setVisibility(View.INVISIBLE);
            item.setVisibility(View.INVISIBLE);

            tvLocation.setVisibility(View.VISIBLE);
            tvItem.setVisibility(View.VISIBLE);

            //Show button to lead user to learning gallery tagged to the equipment if available.
			/*if (!tag.equals("null")){
				btn_Learning_Gallery.setVisibility(View.VISIBLE);

				btn_Learning_Gallery.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						transitToLearningGallery(tag);
					}
				});
			}*/

            tvCategory.setText("Category selected : " + category);
            tvItem.setText("Item selected : " + inventoryAssetDescription);
            tvLocation.setText("Location of item : " + location);


        }

        //Set date to loan to
        ivdateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                //Select Date From
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dateDue.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() + 259200000 - 1000);
                datePickerDialog.show();

            }
        });


        //Set time to return
        ivTimeReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get Time returning item
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                timeReturnLoan.setText(hourOfDay + ":" + minute + ":" + "00");
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();

            }
        });

        // Set listener for submit loan button
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Validation
                dateFrom=dateRequest.getText().toString();
                timeLoaning=timeLoan.getText().toString();
                dateTo=dateDue.getText().toString();
                timeReturning=timeReturnLoan.getText().toString();
                reasonToLoan=reason.getText().toString();


                //Validation for Dates and Time.(Cannot be empty)
                if (TextUtils.isEmpty(dateFrom)){
                    dateRequest.setError("Please enter date to loan item");
                    dateRequest.requestFocus();
                    return;

                }
                if(TextUtils.isEmpty(timeLoaning)){
                    timeLoan.setError("Please enter time to loan item");
                    timeLoan.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(dateTo)){
                    dateDue.setError("Please enter date to return loan");
                    dateDue.requestFocus();
                    return;

                }
                if(TextUtils.isEmpty(timeReturning)){
                    timeReturnLoan.setError("Please time to return loan item");
                    timeReturnLoan.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(reasonToLoan)){
                    reason.setError("Please enter reason for loaning of item");
                    reason.requestFocus();
                    return;
                }
                //Validate Start Date Time and End Date Time
                try {
                    if(validateStartDTtoEndDT()){
                        dateRequest.setError("Please check Start Date/Time again");
                        dateRequest.requestFocus();
                        return;
                    }
                } catch (ParseException e) {
                    //e.printStackTrace();
                }

                //Check whether there is any item available to loan
                if(itemData == false) {
                    Toast.makeText(getActivity(), "Sorry there are no item available to be loaned", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Start the progress dialog so user cannot spam submit
                progressDialog.setMessage("Submitting your request");
                progressDialog.show();

                //Before sending data into the server check loan eligibility
                checkLoanEQPeligibilty();


            }
        });


        return view;


    }

	private void sendDataToServer() throws ParseException {

        //Using volley to send loan details into database
        StringRequest send_req = new StringRequest(Request.Method.POST,url + "makeLoanRequest.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(LoanRequestActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    //Check if response was success
                    if (jsonObject.getString("status").equals("Success")){

                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Successfully Submitted", Toast.LENGTH_SHORT).show();
                        if(prevFragment.equals("Scan")){
                            getFragmentManager().popBackStack();

                        }
                        getFragmentManager().popBackStack();
                    }
                    else if(jsonObject.getString("status").equals("Fail")){
                        Toast.makeText(getActivity(), "Something went wrong..", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "An error has occurred", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Details to be submitted into database
                Map<String, String>  params = new HashMap<String, String>();
                params.put("userId", SharedPrefManager.getInstance(getActivity()).getUser().getId());
                params.put("dateFrom",dateFromAndTimeLoan);
                params.put("dateto",dateToAndTimeReturnLoan);
                params.put("reason",reasonToLoan);
                params.put("lockerRequest",lockerRequest);
                params.put("faculty", SharedPrefManager.getInstance(getActivity()).getUser().getFaculty());
                //params.put("assetDescription",item_name);
                params.put("inventoryId",selectedItemID);

                // need to find out how to retrieve the inventory Id -- need to change the api to get the inventory id from the getInventoryitems << I don know what simone is trying to say
                // add in inventory id there << I don know what simone is trying to say

                return params;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(send_req);
    }

    //Retrieve data for Category Spinner
    private void getSpinnerData() {

        //Go into database and retrieve category of item to loan
        StringRequest spinner_req=new StringRequest(Request.Method.GET,url+ "getInventoryCategory.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("categories");
                    for (int i=0;i<jsonArray.length();i++){
                        retrievedCategory = jsonArray.getString(i);
                        arrayListOfCategories.add(retrievedCategory);
                    }

                    //Connect retrieved data to spinner
                    categories.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,arrayListOfCategories));

                    Log.d("array",jsonArray.toString());
                } catch (JSONException e) {
                    //e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "An error has occurred", Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue spinner_que= Volley.newRequestQueue(getActivity());
        spinner_que.add(spinner_req);
    }

    //Retrieve data for Item Spinner
    private void getItemdata() {

        //Go into database and retrieve inventory items for loan
        StringRequest itemReq =new StringRequest(Request.Method.POST,url+ "getLoanableInventoryItems.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("assetDescription");
                    JSONArray idArray =jsonObject.getJSONArray("id");
                    JSONArray locationArray =jsonObject.getJSONArray("assetLocation");

                    for (int i=0;i<jsonArray.length();i++) {
						retrievedItem = jsonArray.getString(i);
						arrayListOfItems.add(retrievedItem);
					}

                    for (int j=0; j<idArray.length(); j++){
                        String id =idArray.getString(j);
                        arrayIDs.add(id);
                        hminventoryIDtoLocation.put(id,locationArray.getString(j));
                    }


                    //Check if id is null,if its null don't set adapter, itemDate is true as there is item
                    if(arrayIDs.size() != 0){

                        //If its not null, set adapter
                        item.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,arrayListOfItems));
                        item.setVisibility(View.VISIBLE);
                        itemData = true;
                    }
                    else{
                        //if its null means theres is no item so itemData is false
                        item.setVisibility(View.GONE);
                        itemData = false;
                    }


                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "An Error has occured", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "An Error has occured", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Details to be submitted into database to retrieve inventory items
                Map<String, String>  params = new HashMap<String, String>();
                params.put("category",selected_category);
                Log.d("category_id",selected_category.toString());
                return params;
            }
        };
        RequestQueue requestQueue=Volley.newRequestQueue(getActivity());
        requestQueue.add(itemReq);

    }

    private boolean validateStartDTtoEndDT() throws ParseException {

        //Concatanate date time and transform it into DateTime Object type for database's format
        dateFromAndTimeLoan = dateFrom + " " + timeLoaning;
        dateToAndTimeReturnLoan = dateTo + " " + timeReturning;

        //Use date object to compare time
        String timeFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);

        Date selectedtimefrom = sdf.parse(dateFromAndTimeLoan);
        Date selectedtimeTo = sdf.parse(dateToAndTimeReturnLoan);

        if(selectedtimefrom.after(selectedtimeTo)){
            return true;
        }

        return false;

    }

    private void checkLoanEQPeligibilty(){

        //Using volley to send loan details into database
        StringRequest send_req = new StringRequest(Request.Method.POST,url + "checkLoanEglibility.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(LoanRequestActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    //Check if response was success
                    if (jsonObject.getString("status").equals("Success")){

                        //If eligible to loan , update data in the server
                        sendDataToServer();

                    }
                    else if(jsonObject.getString("status").equals("Fail")){

                        progressDialog.dismiss();

                        //If fail tell user they are eligible to loan item
                        displayLoanEligibility();


                    }
                } catch (JSONException e) {
//                    e.printStackTrace();
                } catch (ParseException e) {
//                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Details to be submitted into database
                Map<String, String>  params = new HashMap<String, String>();
                params.put("userId", SharedPrefManager.getInstance(getActivity()).getUser().getId());
                params.put("inventoryId",selectedItemID);

                return params;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(send_req);

    }

    public void displayLoanEligibility(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setMessage("Sorry to inform you that you are not eligible to loan this item");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        //Go back to home
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


	private void transitToLearningGallery(String tag) {
    	//set up the learning gallery
		((MainActivity)getActivity()).enableDrawer();
		((MainActivity)getActivity()).getNavigationView().getMenu().getItem(3).setChecked(true);
		((MainActivity)getActivity()).setToolbarTitle("Learning Gallery");

		//Pass the tag to LG then search by tag from there.
		StudentLearningGalleryFragment fragment = new StudentLearningGalleryFragment();
		Bundle bundleObj = new Bundle();
		bundleObj.putString("tag",tag);
		fragment.setArguments(bundleObj);

		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.content_frame, fragment);
		transaction.addToBackStack("ScanToLG");

		// Commit the transaction
		transaction.commit();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		((MainActivity) getActivity()).enableDrawer();
	}
}
