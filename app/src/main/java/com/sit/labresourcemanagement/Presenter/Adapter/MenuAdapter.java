package com.sit.labresourcemanagement.Presenter.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sit.labresourcemanagement.Model.MenuModel;
import com.sit.labresourcemanagement.R;

import java.util.List;

public class MenuAdapter extends BaseAdapter {

	List<MenuModel> menuModelList;
	Context context;

	public MenuAdapter(List<MenuModel> menuModelList, Context context) {
		this.menuModelList = menuModelList;
		this.context = context;
	}

	@Override
	public int getCount() {
		return menuModelList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MenuModel model = menuModelList.get(position);

		if (convertView == null) {
			final LayoutInflater layoutInflater = LayoutInflater.from(context);
			convertView = layoutInflater.inflate(R.layout.card_menu, null);
		}

		ImageView imageView = convertView.findViewById(R.id.imageViewIcon);
		imageView.setImageResource(model.getResource());

		TextView textView = convertView.findViewById(R.id.textViewTitle);
		textView.setText(model.getTitle());

		return convertView;
	}
}
