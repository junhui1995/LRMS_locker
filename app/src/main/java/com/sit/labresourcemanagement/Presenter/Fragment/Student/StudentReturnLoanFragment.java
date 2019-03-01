package com.sit.labresourcemanagement.Presenter.Fragment.Student;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sit.labresourcemanagement.Model.ApiRoutes;
import com.sit.labresourcemanagement.Model.LockerDetails;
import com.sit.labresourcemanagement.Model.SharedPrefManager;
import com.sit.labresourcemanagement.Model.Student.StudentReturnModel;
import com.sit.labresourcemanagement.Model.Student.StudentReturnRequestModel;
import com.sit.labresourcemanagement.Presenter.Adapter.StudentReturnAdapter;
import com.sit.labresourcemanagement.Presenter.Adapter.StudentReturnRequestAdapter;
import com.sit.labresourcemanagement.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.os.Environment.DIRECTORY_PICTURES;
import static android.support.v4.content.FileProvider.getUriForFile;


//Done by Hiew Jun Hui
public class StudentReturnLoanFragment extends Fragment {

    //Declaring view and wigdets
    View view;
    FloatingActionButton fabreturn;
    ListView itemReturn;
    String url = ApiRoutes.getUrl();

    //Variables for this fragment
    List<StudentReturnModel> listStudItemReturn;
    //this is to get the locker for student to return item
    //private HashMap<String,LockerDetails> hmlockIDtoLockerdetails;

    public StudentReturnLoanFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_student_loan_return, container, false);
        fabreturn = view.findViewById(R.id.addItemReturn);

        itemReturn = view.findViewById(R.id.lvItemReturn);
        itemReturn.setDivider(null);

        //Prepare the list student return request and load all return request data in
        listStudItemReturn = new ArrayList<>();
        //Get all the items currently on return
        loadCurrentReturnReqData();

        //hmlockIDtoLockerdetails = new HashMap<String,LockerDetails>();

        fabreturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Transit to the new fragment
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, new StudentScanToReturnItem());
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();*/
            }
        });

        return view;
    }

    private void loadCurrentReturnReqData() {
        StringRequest currentReturnReq = new StringRequest(Request.Method.POST, url + "getStudentReturnLoan.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("ret", jsonObject.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("loanReturnDetails");


                    if (jsonObject.getString("status").equals("Success")) {

                        //If success get the details out
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);


                            StudentReturnModel studentReturnModel = new StudentReturnModel(jsonObject1.getString("loanId"), jsonObject1.getString("userId"), jsonObject1.getString("inventoryId"), jsonObject1.getString("poId"), jsonObject1.getString("status"));
                            listStudItemReturn.add(studentReturnModel);


                        }

                        itemReturn.setAdapter(new StudentReturnAdapter(getContext(), listStudItemReturn, StudentReturnLoanFragment.this));
                    } else if (jsonObject.getString("status").equals("Fail")) {
                        Toast.makeText(getActivity(), "Return request Fail", Toast.LENGTH_SHORT).show();
                    }
                    //Set the adapter for display


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
                params.put("userId", SharedPrefManager.getInstance(getContext()).getUser().getId());
                return params;
            }
        };
        RequestQueue currentRetReqQ = Volley.newRequestQueue(getContext());
        currentRetReqQ.add(currentReturnReq);
    }

    static final int REQUEST_CAPTURE_IMAGE = 100;
    String imageFilePath;
    File image;



    public void openCameraIntent(String loanid) {
        imageFilePath = "loadID"+loanid;
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        image = new File(Environment.getExternalStorageDirectory(), "QRCodes");

        File actualimage = new File(image.getAbsolutePath() + imageFilePath+".jpg");
        Uri fileProvider = FileProvider.getUriForFile(getActivity(), "com.sit.labresourcemanagement.provider", actualimage);
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        if (pictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            startActivityForResult(pictureIntent,
                    REQUEST_CAPTURE_IMAGE);


        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        if (requestCode == REQUEST_CAPTURE_IMAGE) {

            sendEmail(imageFilePath+".jpg");
        }
    }

    public void sendEmail(String filename)
    {
        File imagePath = new File(Environment.getExternalStorageDirectory(), "QRCodes");
        File newFile = new File(imagePath, filename);
        Uri contentUri = getUriForFile(getContext(), "com.sit.labresourcemanagement.provider", newFile);
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"weeyeong.loo@singaporetech.edu.sg"});
        i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
        i.putExtra(Intent.EXTRA_TEXT, "");
        i.putExtra(Intent.EXTRA_STREAM,  contentUri);

        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Snackbar.make(view, "There are no email clients installed.", Snackbar.LENGTH_SHORT).show();
            //Toast.makeText(POCheckInOutAsset.this, "", Toast.LENGTH_SHORT).show();
        }
    }
}
