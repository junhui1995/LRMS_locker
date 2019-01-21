package com.sit.labresourcemanagement.Presenter.Fragment.PO;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.sit.labresourcemanagement.Presenter.Activity.MainActivity;
import com.sit.labresourcemanagement.R;

public class POHomeFragment extends Fragment implements View.OnClickListener {

    private Menu menu;
    private View rootview;
    private ImageButton ibCheckout, ibPending, ibRecent;

    public POHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_po_home, container, false);

        ibCheckout = (ImageButton) rootview.findViewById(R.id.imageButtonCheckoutAsset);
        ibPending = (ImageButton) rootview.findViewById(R.id.imageButtonPending);
        //ibRecent = (ImageButton) rootview.findViewById(R.id.imageButtonRecentCheckout);

        ibCheckout.setOnClickListener(this);
        ibPending.setOnClickListener(this);
        //ibRecent.setOnClickListener(this);

        menu = ((MainActivity)getActivity()).getNavigationView().getMenu();

        return rootview;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        NavigationView navigationView = ((MainActivity)getActivity()).getNavigationView();

        switch (id) {
            case R.id.imageButtonCheckoutAsset:
                ((MainActivity)getActivity()).onNavigationItemSelected(menu.findItem(R.id.nav_checkout_asset));
                navigationView.getMenu().getItem(1).setChecked(true);
                break;

            case R.id.imageButtonPending:
                ((MainActivity)getActivity()).onNavigationItemSelected(menu.findItem(R.id.nav_pending_loan));
                navigationView.getMenu().getItem(2).setChecked(true);
                break;

//            case R.id.imageButtonRecentCheckout:
//                ((MainActivity)getActivity()).onNavigationItemSelected(menu.findItem(R.id.nav_recent_checkout));
//                navigationView.getMenu().getItem(3).setChecked(true);
//                break;
        }
    }

}
