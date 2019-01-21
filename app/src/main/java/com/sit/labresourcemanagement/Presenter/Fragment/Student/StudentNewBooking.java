package com.sit.labresourcemanagement.Presenter.Fragment.Student;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
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

public class StudentNewBooking extends Fragment {

    //Declaration for View
    View view;

    //Declaration for widgets
    TextView tvSelectWorkBenchiDtoLocation;
    Spinner spinnerWBAvailable ;
    Spinner spinnerTimeSlots;
    Button btnSubmit;
    EditText etDate;
    EditText etReason;
    EditText etTimeStart;
    EditText etTimeEnd;
    ImageView ivStartTime;
    ImageView ivEndTime;
    ImageView ivCalendar;
    ProgressDialog progressDialog;

    //Variables
    String WorkBench = "";
    String Location = "";
    String WorkBenchToLocation = "";
    String DateSelected = "";
    String url = ApiRoutes.getUrl();
    String selectedWBID = "";
    String selectedWB = "";
    String selectedTimeSlot = "";
    String WorkBenchDetails = "";
    String prevFragment = "";
    ArrayList WorkBenchIDs = new ArrayList();
    ArrayList arrayOfWBToLocation=new ArrayList();
    HashMap<String,String> StartTtoEndT = new HashMap<>();
    HashMap<String,String> clashedStartTtoEndT = new HashMap<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		((MainActivity)getActivity()).disableDrawer();
        Bundle bundle =getArguments();

        if(null!=bundle) {

            //Receives ID from previous fragment if bundle is not null
            WorkBenchDetails=bundle.getString("WorkBenchDetails");
            prevFragment = "Scan";
        }
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Initialize progress dialog
        progressDialog = new ProgressDialog(getContext());

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_student_new_booking, container, false);

        tvSelectWorkBenchiDtoLocation = view.findViewById(R.id.tvSelectWB);
        spinnerWBAvailable = view.findViewById(R.id.spinnerSelectWB);
        etDate = view.findViewById(R.id.etDateSelected);
        etReason = view.findViewById(R.id.etReason);
        etTimeStart = view.findViewById(R.id.etTimeStart);
        etTimeEnd = view.findViewById(R.id.etTimeEnd);
        ivStartTime = view.findViewById(R.id.ivStartTime);
        ivEndTime = view.findViewById(R.id.ivEndTime);
        ivCalendar = view.findViewById(R.id.ivCalendar);
        btnSubmit = view.findViewById(R.id.btnSubmit);


        //Check if there is any workbench details
        if(WorkBenchDetails.equals("")) {

            //Make spinner visible so user can select
            spinnerWBAvailable.setVisibility(View.VISIBLE);

            //Retrieve WorkBench available for Spinner
            getSpinnerDataForWB();
        }
        else{

            //Spilt Workbench details
            String[] spiltWorkbenchDetails = WorkBenchDetails.split(",");
            WorkBenchToLocation = spiltWorkbenchDetails[1] + "," + spiltWorkbenchDetails[2];
            selectedWBID = spiltWorkbenchDetails[0];


            tvSelectWorkBenchiDtoLocation.setText("Selected Workbench and its Location :" + " " + WorkBenchToLocation);
        }

        //Activate Calendar listener to wait for user to click on the image calendar
        ivCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                //Select Date From
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                //Make sure the list are all clean
                                cleanList();

                                //User Selected Date
                                DateSelected = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                etDate.setText(DateSelected);

                                //Retrieve all time slots booked on a particular day
                                StartTtoEndT = getTimeSlotsBookOnSelectedDay();



                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() + 259200000 - 1000);
                datePickerDialog.show();
            }
        });


        //Activate onItemClick Listeners for Spinners
        spinnerWBAvailable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Selected Workbench
                selectedWB = arrayOfWBToLocation.get(i).toString();
                selectedWBID = WorkBenchIDs.get(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Activate image start&end time listener
        ivStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get Time booking workbench
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                if(hourOfDay < 9 || (hourOfDay >= 21 && minute > 0)){
                                    Toast.makeText(getActivity(), "Sorry you are only to book a workbench in between 9am to 9pm", Toast.LENGTH_SHORT).show();
                                    etTimeStart.setText("09" + ":" + "00" + ":" + "00");
                                }else{
                                    etTimeStart.setText(hourOfDay + ":" + minute + ":" + "00");
                                }
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();

            }
        });
        ivEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get Time returning workbench
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                if(hourOfDay < 9 || (hourOfDay >= 21 && minute > 0)){
                                    Toast.makeText(getActivity(), "Sorry you are only to book a workbench in between 9am to 9pm", Toast.LENGTH_SHORT).show();
                                    etTimeEnd.setText("09" + ":" + "00" + ":" + "00");

                                }else{
                                    etTimeEnd.setText(hourOfDay + ":" + minute + ":" + "00");

                                }
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });


        //Activate submit button listener
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Validation. No Empty Fields
                if (TextUtils.isEmpty(etDate.getText().toString())){
                    etDate.setError("Please select date");
                    etDate.requestFocus();
                    return;

                }
                if (TextUtils.isEmpty(etTimeStart.getText().toString())){
                    etTimeStart.setError("Please enter time start");
                    etTimeStart.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(etTimeEnd.getText().toString())){
                    etTimeEnd.setError("Please enter time end");
                    etTimeEnd.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(etReason.getText().toString())){
                    etReason.setError("Please enter reason");
                    etReason.requestFocus();
                    return;

                }

                //Check if Start Time is earlier than End time
                try {
                    if(StartDatesmallerthanEndDate()){

                        //If return true good. Proceed
                    }
                    else{
                        etTimeStart.setError("Please check again your time start");
                        etTimeStart.requestFocus();
                        return;
                    }
                } catch (ParseException e) {
                  //  e.printStackTrace();
                }

                try {
                    //Check if there is any time slot on that date selected
                    if(!StartTtoEndT.isEmpty()){
                        if(timeClash()){
                            showTimeThatClash();
                        }
                        else{
                            insertBookingDetailsIntoDB();
                        }
                    }
                    else{
                        insertBookingDetailsIntoDB();
                    }
                } catch (ParseException e) {
                   // e.printStackTrace();
                }


            }
        });
        return view;
    }

    public void showTimeThatClash(){

        String Message = "";

        for(HashMap.Entry<String,String> entry : clashedStartTtoEndT.entrySet()) {

            Message = Message + entry.getKey() + " to " + entry.getValue() + "\n";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle("Time Clashed");
        builder.setMessage(Message);
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Flush clashed list
                clashedStartTtoEndT = new HashMap<>();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean timeClash() throws ParseException {

        //Use date object to compare time
        String timeFormat = "HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);

        Date selectedtimefrom = sdf.parse(etTimeStart.getText().toString());
        Date selectedtimeTo = sdf.parse(etTimeEnd.getText().toString());

        //Iterate through Hashmap of time slots
        for(HashMap.Entry<String,String> entry : StartTtoEndT.entrySet())
        {
            Date instanceTimeFrom = sdf.parse(entry.getKey());
            Date instancetimeTo = sdf.parse(entry.getValue());

            //Check if selected time to is in between instance time from & instance time to ||
            //Check if selected time from is in between instance time from & instance time to ||
            //Check if selected time from is before instance time from & selected time to is after instance time to
            if((selectedtimeTo.after(instanceTimeFrom) && selectedtimeTo.before(instancetimeTo)) || (selectedtimefrom.after(instanceTimeFrom) && selectedtimefrom.before(instancetimeTo)) || (selectedtimefrom.before(instanceTimeFrom) && selectedtimeTo.after(instancetimeTo)) || (selectedtimefrom.equals(instanceTimeFrom) || selectedtimeTo.equals(instancetimeTo))){

                //Convert instance date back into string put it into a list of clashed time slots
                String clashedTimeFrom = sdf.format(instanceTimeFrom);
                String clashedTimeTo = sdf.format(instancetimeTo);
                clashedStartTtoEndT.put(clashedTimeFrom,clashedTimeTo);

            }

        }

        if(!clashedStartTtoEndT.isEmpty()){
            return true;
        }

        return false;
    }

    public void insertBookingDetailsIntoDB(){

        //Format into DateTime format for database's timeFrom and timeTo column
        final String timeFrom = etDate.getText().toString() + " " + etTimeStart.getText().toString();
        final String timeTo = etDate.getText().toString() + " " + etTimeEnd.getText().toString();

        //Start the progress dialog so user cannot spam submit
        progressDialog.setMessage("Submitting your request");
        progressDialog.show();


        //Insert Booking details into DB
        StringRequest send_req = new StringRequest(Request.Method.POST,url + "makeNewBooking.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);

                    //Check if response was success
                    if (jsonObject.getString("status").equals("Success")){
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Successfully Added", Toast.LENGTH_SHORT).show();
                        //backtoBookingFragment();

                        if(prevFragment.equals("Scan")){
                            getFragmentManager().popBackStack();

                        }
                        getFragmentManager().popBackStack();
                    }
                    else if(jsonObject.getString("status").equals("Fail")){
                        Toast.makeText(getActivity(), "Something went wrong..", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Something went wrong here..", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Details to be submitted into database
                Map<String, String>  params = new HashMap<String, String>();
                params.put("userId", SharedPrefManager.getInstance(getActivity()).getUser().getId());
                params.put("IDOfWorkBench", selectedWBID);
                params.put("timeFrom",timeFrom);
                params.put("timeTo",timeTo);
                params.put("date",etDate.getText().toString());
                params.put("reason",etReason.getText().toString());
                params.put("title",SharedPrefManager.getInstance(getActivity()).getUser().getName());
                params.put("faculty",SharedPrefManager.getInstance(getActivity()).getUser().getFaculty());

                return params;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(send_req);
    }

    public void cleanList(){
        StartTtoEndT = new HashMap<>();
        clashedStartTtoEndT = new HashMap<>();
    }

    //Retrieve data for Workbench to Lab Spinner
    private void getSpinnerDataForWB() {

        //Go into database and retrieve category of item to loan
        StringRequest spinner_items=new StringRequest(Request.Method.GET,url+ "getWorkBenchAndLocation.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArrayOfBenches=jsonObject.getJSONArray("WorkBenchs");
                    JSONArray jsonArrayOfLocation=jsonObject.getJSONArray("Location");
                    JSONArray jsonArrayOfBenchIDs = jsonObject.getJSONArray("WorkBenchID");

                    for (int i=0;i<jsonArrayOfBenches.length();i++){
                        WorkBench = jsonArrayOfBenches.getString(i);
                        Location = jsonArrayOfLocation.getString(i);

                        //Pair workbench to location
                        WorkBenchToLocation = WorkBench + "," + Location;
                        arrayOfWBToLocation.add(WorkBenchToLocation);

                        //Add workbenchid into an array
                        WorkBenchIDs.add(jsonArrayOfBenchIDs.getString(i));

                    }

                    //Connect retrieved data to spinner
                    spinnerWBAvailable.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,arrayOfWBToLocation));
                } catch (JSONException e) {
                    //e.printStackTrace();
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue spinner_que= Volley.newRequestQueue(getActivity());
        spinner_que.add(spinner_items);
    }

    //Retrieve time slots book on the particular day
    private HashMap<String,String> getTimeSlotsBookOnSelectedDay(){
        final HashMap<String,String> temphmStartTtoEndT = new HashMap<>();

        //Get Booking time slots from DB
        StringRequest send_req = new StringRequest(Request.Method.POST,url + "getWorkBenchTimeBookedfromSelectedDate.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(LoanRequestActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("TimeSlotsBooked");

                    if(jsonObject.getString("status").equals("Success")){
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject1=jsonArray.getJSONObject(i);
                            String datetimeFrom = jsonObject1.getString("timeFrom");
                            String datetimeTo = jsonObject1.getString("timeTo");

                            //Since timeFrom and timeTo is in DateTime format , have to break it up because i only wanted the time
                            String[] splitedDTfrom = datetimeFrom.split("\\s+");
                            String[] splitedDTto = datetimeTo.split("\\s+");
                            String timeFrom = splitedDTfrom[1];
                            String timeTo = splitedDTto[1];

                            temphmStartTtoEndT.put(timeFrom,timeTo);

                        }
                    }
                    else if(jsonObject.getString("status").equals("Null")){
                        Toast.makeText(getActivity(), "Sorry there isnt any available workbench to book", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Something went wrong here..", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Details to be submitted into database
                Map<String, String>  params = new HashMap<String, String>();
                params.put("date",DateSelected);
                params.put("workbenchId", selectedWBID);
                return params;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
        requestQueue.add(send_req);

        return temphmStartTtoEndT;
    }

    public boolean StartDatesmallerthanEndDate() throws ParseException {

        //Use date object to compare time
        String timeFormat = "HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);

        Date selectedtimefrom = sdf.parse(etTimeStart.getText().toString());
        Date selectedtimeTo = sdf.parse(etTimeEnd.getText().toString());


        //Check if selected time from is after selected time to.
        if(selectedtimefrom.after(selectedtimeTo)){
            return false;
        }

        return true;

    }

	@Override
	public void onDestroy() {
		super.onDestroy();
		((MainActivity)getActivity()).enableDrawer();
	}
}
