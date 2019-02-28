package com.sit.labresourcemanagement.Presenter.Fragment.Student;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.sit.labresourcemanagement.Presenter.Activity.MainActivity;
import com.sit.labresourcemanagement.Presenter.Activity.QrCodeScannerActivity;
import com.sit.labresourcemanagement.R;

import static android.app.Activity.RESULT_OK;

public class StudentHomeFragment extends Fragment implements View.OnClickListener {
	private static final String UNIVERSAL_TAG = "Universal";

    private Menu menu;
    private View rootview;
    private ImageButton ibAttendance, ibBooking, ibLearningGallery, ibLoan;

	AlertDialog dialog;

    public StudentHomeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_student_home, container, false);

        ibAttendance =  rootview.findViewById(R.id.imageButtonAttendance);
        ibBooking =  rootview.findViewById(R.id.imageButtonBooking);
        ibLearningGallery = rootview.findViewById(R.id.imageButtonLearningGallery);
        ibLoan =  rootview.findViewById(R.id.imageButtonLoan);

        ibAttendance.setOnClickListener(this);
        ibBooking.setOnClickListener(this);
        ibLearningGallery.setOnClickListener(this);
        ibLoan.setOnClickListener(this);

        menu = ((MainActivity)getActivity()).getNavigationView().getMenu();

        return rootview;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        NavigationView navigationView = ((MainActivity)getActivity()).getNavigationView();

        //The switch case helps us select the item in the navigation drawer as well as transact to the desired fragment.
		//Calling functions from the main activity. Not sure if its correct though.. but it works.
        switch (id) {
            case R.id.imageButtonAttendance:
                ((MainActivity)getActivity()).onNavigationItemSelected(menu.findItem(R.id.nav_attendance));
                navigationView.getMenu().getItem(1).setChecked(true);
                break;

            case R.id.imageButtonBooking:
                ((MainActivity)getActivity()).onNavigationItemSelected(menu.findItem(R.id.nav_bookings));
                navigationView.getMenu().getItem(2).setChecked(true);
                break;

            case R.id.imageButtonLearningGallery:
                ((MainActivity)getActivity()).onNavigationItemSelected(menu.findItem(R.id.nav_learning_gallery));
                navigationView.getMenu().getItem(3).setChecked(true);
                break;

            case R.id.imageButtonLoan:
                ((MainActivity)getActivity()).onNavigationItemSelected(menu.findItem(R.id.nav_loan_assets));
                navigationView.getMenu().getItem(4).setChecked(true);
                break;
/*			case R.id.imageButtonAttendance:
				((MainActivity)getActivity()).onNavigationItemSelected(menu.findItem(R.id.nav_attendance));
				navigationView.getMenu().getItem(1).setChecked(true);
				break;*/
        }
    }

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		//We decided to add a universal scan to scan lockers, assets and workbenches for UX

		//Scan qr icon
		MenuItem scan = menu.findItem(R.id.app_bar_universal_scan);
		scan.setVisible(true);

		scan.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				scanQR();
				return true;
			}
		});
	}

	private void scanQR() {
    	//Calls the scanner activity
		Intent i = new Intent(getActivity(), QrCodeScannerActivity.class);
		i.putExtra("calledFrom","Universal");
		startActivityForResult(i, 1);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	//When we get the result, we check for a valid QR.
		//If valid,
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1) {
			if(resultCode == RESULT_OK) {
				String QRresult = data.getStringExtra("QrResult");
				checkQRformat(QRresult);
			}
		}
	}

	public void checkQRformat(String QrResult){
		String[] splitedStr = QrResult.split("-");
		if(splitedStr.length == 3){
			if(splitedStr[0].equalsIgnoreCase("LRMS")){
				String type = splitedStr[1].toUpperCase();
				Fragment fragment;
				Bundle args = new Bundle();
				args.putString(UNIVERSAL_TAG, QrResult);

				switch(type){
					case "LKR":
					case "EQP":
					case "ACC":
					case "TOL":
						fragment = new StudentScanQR();
						break;

					case "WKB":
						fragment = new StudentScanWorkBenchFragment();
						break;

					default:
						fragment = null;
						invalidCode();
						break;

				}
				fragment.setArguments(args);
				if(fragment != null)
					getFragmentManager()
							.beginTransaction()
							.replace(R.id.content_frame, fragment)
							.addToBackStack(UNIVERSAL_TAG)
							.commit();
			}
		} else {
			invalidCode();
		}
	}

	private void invalidCode(){
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
		mBuilder.setTitle("Error")
				.setIcon(R.drawable.ic_error)
				.setMessage("The code you scanned was invalid")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		dialog = mBuilder.create();
		dialog.show();
	}

}
