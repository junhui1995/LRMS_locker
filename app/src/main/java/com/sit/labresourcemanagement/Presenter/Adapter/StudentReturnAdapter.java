package com.sit.labresourcemanagement.Presenter.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sit.labresourcemanagement.Model.Student.StudentReturnModel;
import com.sit.labresourcemanagement.R;


import java.util.List;

public class StudentReturnAdapter extends BaseAdapter {

    Context context;
    private LayoutInflater inflater = null;

    //Declare widgets for view later
    TextView tvInventoryId;
    TextView tvTime;
    TextView tvPlace ;
    TextView tvStatus;
    TextView tvrejReason;
    TextView tvpoId;
    TextView tvRemarks;
    TextView tvassetDescription;
    TextView tvDelete;

    TextView tvReturn;


    List<StudentReturnModel> Return_list;
    public StudentReturnAdapter(Context context, List<StudentReturnModel> Return_list) {
        this.Return_list = Return_list;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return Return_list.size();
    }

    @Override
    public Object getItem(int position) {
        return Return_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        View vi = convertView;
        if(vi == null){
            vi = inflater.inflate(R.layout.card_student_item_return,null);
        }
        //Widgets initialization
        tvTime =  vi.findViewById(R.id.date_from_currentReturnReq);
        tvPlace =  vi.findViewById(R.id.tvPlace);
        tvStatus =  vi.findViewById(R.id.tvStatus);
        tvrejReason =  vi.findViewById(R.id.tvRejReason);
        tvRemarks =  vi.findViewById(R.id.tvRemarks);
        tvpoId =  vi.findViewById(R.id.tvPoID);

        tvReturn = vi.findViewById(R.id.tvReturn);

        //if (status == approved) then set visibility to true
if (Return_list.get(position).getLoanId() == "Approved") {


    tvReturn.setVisibility(vi.GONE);
}
else
{
    tvReturn.setVisibility(vi.VISIBLE);
}

        return vi;
    }
}
