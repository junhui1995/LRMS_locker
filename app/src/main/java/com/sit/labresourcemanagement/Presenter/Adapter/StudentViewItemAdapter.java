package com.sit.labresourcemanagement.Presenter.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sit.labresourcemanagement.Model.ApiRoutes;
import com.sit.labresourcemanagement.Model.PO.ItemModel;

import java.util.ArrayList;
import java.util.List;

import com.sit.labresourcemanagement.Presenter.Fragment.Student.StudentNewLoanFragment;
import com.sit.labresourcemanagement.R;

public class StudentViewItemAdapter extends BaseAdapter  {
//extends RecyclerView.Adapter<StudentViewItemAdapter.ViewHolder>

    //Declaration for API Route
    String url = ApiRoutes.getUrl();

    TextView tvitemname,tvassetno,tvid,tvcategory,tvquantity;
    Button btnRequest;
    Context context;
    List<ItemModel> itemModelList = new ArrayList<>();

    Fragment fragment = new StudentNewLoanFragment();


    View view;
    String category,item;

    private static LayoutInflater inflater = null;

    public StudentViewItemAdapter( Context context, List<ItemModel> itemModelList)
    {
        this.itemModelList = itemModelList;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return  itemModelList.size();
    }


    public Object getItem(int position) {
        return itemModelList.get(position);
    }


    public long getItemId(int position) {
        return position;
    }




    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        if (vi == null) {
            vi = inflater.inflate(R.layout.card_item, null);
        }


        tvid = vi.findViewById(R.id.tvId1);
        tvassetno = vi.findViewById(R.id.tvAssetNo);
        tvitemname = vi.findViewById(R.id.tvName);
        tvcategory = vi.findViewById(R.id.tvCategory);
        btnRequest = vi.findViewById(R.id.btnRequestitem);

        tvid.setText(itemModelList.get(position).getId());
        tvassetno.setText(itemModelList.get(position).getAssetNo());
        tvitemname.setText(itemModelList.get(position).getAssetDescription());
        tvcategory.setText(itemModelList.get(position).getCategory());

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make();
                Toast.makeText(context, "Something went wrong here..", Toast.LENGTH_SHORT).show();
            }
        });
        return vi;

    }

    private FragmentManager getFragmentManager() {
        return null;
    }


}
