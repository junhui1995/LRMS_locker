package com.sit.labresourcemanagement.Presenter.Fragment.PO;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.sit.labresourcemanagement.Presenter.Adapter.MenuAdapter;
import com.sit.labresourcemanagement.Model.MenuModel;
import com.sit.labresourcemanagement.R;

import java.util.ArrayList;
import java.util.List;

public class PORecentCheckout extends Fragment {

    private View rootview;

    GridView gridView;
    List<MenuModel> menuModelList;
    MenuAdapter menuAdapter;

    public PORecentCheckout() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuModelList = new ArrayList<>();

        MenuModel item1 = new MenuModel(R.drawable.ic_attendance, getString(R.string.attendance));
		MenuModel item2 = new MenuModel(R.drawable.ic_booking, getString(R.string.booking));
		MenuModel item3 = new MenuModel(R.drawable.ic_learning_gallery, getString(R.string.learning_gallery));
		MenuModel item4 = new MenuModel(R.drawable.ic_loan, getString(R.string.loan));

		menuModelList.add(item1);
		menuModelList.add(item2);
		menuModelList.add(item3);
		menuModelList.add(item4);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_menu, container, false);

        gridView = rootview.findViewById(R.id.gridView_menu);
        menuAdapter = new MenuAdapter(menuModelList, getContext());
        gridView.setAdapter(menuAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Snackbar.make(getView(), position + "", Snackbar.LENGTH_SHORT).show();
			}
		});

        return rootview;
    }
}
