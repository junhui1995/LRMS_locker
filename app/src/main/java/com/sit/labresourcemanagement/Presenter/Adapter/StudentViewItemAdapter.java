package com.sit.labresourcemanagement.Presenter.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;

import com.sit.labresourcemanagement.Model.ApiRoutes;
import com.sit.labresourcemanagement.Model.PO.ItemModel;

import java.util.ArrayList;
import java.util.List;

import com.sit.labresourcemanagement.Presenter.Fragment.Student.StudentItemFragment;
import com.sit.labresourcemanagement.R;

public class StudentViewItemAdapter extends BaseAdapter {

//extends RecyclerView.Adapter<StudentViewItemAdapter.ViewHolder>

    //Declaration for API Route
    String url = ApiRoutes.getUrl();

    TextView tvitemname, tvassetno, tvid, tvcategory, tvquantity, tvlocation;
    ImageView itemImage;
    Button btnRequest;
    Context context;
    List<ItemModel> itemModelList = new ArrayList<>();

    StudentItemFragment fragment;

    public String inventoryAssetDescription,Category,inventoryID,location;


    View view;

    private static LayoutInflater inflater = null;

    public StudentViewItemAdapter(Context context, List<ItemModel> itemModelList, StudentItemFragment fragment) {
        this.itemModelList = itemModelList;
        this.context = context;
        this.fragment = fragment;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return itemModelList.size();
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
        tvlocation = vi.findViewById(R.id.tvitemlocation);
        itemImage = vi.findViewById(R.id.ivItem);
        btnRequest = vi.findViewById(R.id.btnRequestitem);

        tvid.setText(itemModelList.get(position).getAssetDescription());
        tvitemname.setText(itemModelList.get(position).getId());
        tvassetno.setText(itemModelList.get(position).getAssetNo());
        tvcategory.setText(itemModelList.get(position).getCategory());
        tvlocation.setText(itemModelList.get(position).getLocation());
        String itemname = "i" + itemModelList.get(position).getId();
        int resID = context.getResources().getIdentifier(itemname,"drawable",context.getPackageName());
        itemImage.setImageResource(resID);

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make();
                Toast.makeText(context, "Something went wrong here..", Toast.LENGTH_SHORT).show();

                inventoryAssetDescription = itemModelList.get(position).getAssetDescription();
                Category =  itemModelList.get(position).getCategory();
                inventoryID = itemModelList.get(position).getId();
                location = itemModelList.get(position).getLocation();

                //change fragment to new loan

                fragment.changeFragment(inventoryAssetDescription,Category,inventoryID,location);
            }
        });
        return vi;

    }
}

