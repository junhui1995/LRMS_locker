package com.sit.labresourcemanagement.Presenter.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission_group.CAMERA;

public class QrCodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView mScannerView;
    private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;
    String QRCodeResult;
    String calledFrom;
	AlertDialog alertDialog;


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check which fragment called this QRScanner Activity

        Intent intent = getIntent();
        calledFrom = intent.getStringExtra("calledFrom");

        //Note to user
        noteToUserbeforeQRuse(calledFrom);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                //Do nothing
            } else {
                requestPermission();
            }
        }
    }

    private boolean checkPermission() {
        return ( ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(QrCodeScannerActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if(mScannerView == null) {
                    mScannerView = new ZXingScannerView(this);
                    setContentView(mScannerView);
                }
                mScannerView.setResultHandler(this);
                mScannerView.startCamera(camId);
            } else {
                requestPermission();
            }
        }

    }


    @Override
    public void onDestroy() {
		if(alertDialog != null)
			alertDialog.dismiss();

        mScannerView.stopCamera();
        mScannerView = null;
        super.onDestroy();

    }

    @Override
    public void handleResult(Result rawResult) {

        //Set QrCodeResult
        QRCodeResult = rawResult.getText();

        //Create intent to go back to previous activity
        Intent intent = new Intent();

        //Set the string to passed to previous activity
        intent.putExtra("QrResult",QRCodeResult);
        setResult(RESULT_OK,intent);

        //End current activity
        finish();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //Create an intent to tell previous fragment where it was returned from
        Intent intent = new Intent();

        intent.putExtra("QrResult","onBackPressed");

        //End current activity
        finish();


    }

    public void noteToUserbeforeQRuse(String calledFrom){

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Welcome to QRScanner");
        builder1.setCancelable(true);

        switch (calledFrom){
            case "Attendance" :
                builder1.setMessage("Please scan your attendance");
                break;

            case "POCheckInOutAsset":
                builder1.setMessage("Please scan an item");
                break;

            case "StudentScanEQ":
                builder1.setMessage("Please scan an item/locker");
                break;

            case "StudentScanWorkBench":
                builder1.setMessage("Please scan a workbench");
                break;

            case "StudentScanToReturnRequest":
                builder1.setMessage("Please scan an equipment that you want to return");
                break;

			case "Universal":
				builder1.setMessage("Please scan the QR code");
				break;

            default:
                break;
        }

		builder1.setPositiveButton(
				"OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
        alertDialog = builder1.create();
        alertDialog.show();
    }

	@Override
	protected void onStop() {
		super.onStop();
	}
}
