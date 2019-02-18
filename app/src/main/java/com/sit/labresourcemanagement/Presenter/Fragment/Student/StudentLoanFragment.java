package com.sit.labresourcemanagement.Presenter.Fragment.Student;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.sit.labresourcemanagement.R;


public class StudentLoanFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "Loan Asset";
    private View rootview;
    private ImageButton ibNewLoan, ibLoanStatus, ibReturnLoan , ibScanQR;

    public StudentLoanFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_student_loan, container, false);

        ibNewLoan = rootview.findViewById(R.id.imageButtonNewLoan);
        ibLoanStatus =  rootview.findViewById(R.id.imageButtonLoanStatus);
        ibReturnLoan = rootview.findViewById(R.id.imageButtonReturnLoan);
        ibScanQR = rootview.findViewById(R.id.imageButtonScanQR);

        ibNewLoan.setOnClickListener(this);
        ibLoanStatus.setOnClickListener(this);
        ibReturnLoan.setOnClickListener(this);
        ibScanQR.setOnClickListener(this);

        return rootview;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Fragment fragment = null;
        String tag = TAG;

        switch (id) {
            case R.id.imageButtonNewLoan:
                fragment = new StudentNewLoanFragment();
                break;

            case R.id.imageButtonLoanStatus:
                fragment = new StudentLoanStatusFragment();
                break;

            case R.id.imageButtonReturnLoan:
            	fragment = new StudentReturnLoanFragment();
                break;
            case R.id.imageButtonScanQR:
                fragment = new StudentScanQR();
                tag = "Scan";

        }

		if (fragment!=null){
			FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.commit();
		}
    }
}
