package com.sit.labresourcemanagement.Presenter.Fragment.Student;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.sit.labresourcemanagement.R;

public class StudentBookingFragment extends Fragment implements View.OnClickListener{
    public static final String TAG = "Booking";

    private View rootview;
    private ImageButton ibScanQR, ibBookWorkbench, ibViewPastBookings;

    public StudentBookingFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_student_booking, container, false);

        ibScanQR = (ImageButton) rootview.findViewById(R.id.imageButtonScanQR);
        ibBookWorkbench = (ImageButton) rootview.findViewById(R.id.imageButtonBookWorkbench);
        ibViewPastBookings = (ImageButton) rootview.findViewById(R.id.imageButtonViewBookingStatus);

        ibScanQR.setOnClickListener(this);
        ibBookWorkbench.setOnClickListener(this);
        ibViewPastBookings.setOnClickListener(this);

        return rootview;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Fragment fragment = null;
        String tag = TAG;

        switch (id) {
            case R.id.imageButtonScanQR:
                tag = "Scan";
                fragment = new StudentScanWorkBenchFragment();
                break;

            case R.id.imageButtonBookWorkbench:
                fragment = new StudentNewBooking();
                break;

            case R.id.imageButtonViewBookingStatus:
                fragment = new StudentBookingStatusFragment();
                break;
        }

        if (fragment!=null){
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.commit();
        }
    }
}
