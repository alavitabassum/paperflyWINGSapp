package com.example.user.paperflyv0;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class pickuplistForExecutiveAdapter extends RecyclerView.Adapter<pickuplistForExecutiveAdapter.ViewHolder> {


    private List<PickupList_Model_For_Executive> list;
    private Context context;

 /*   final CharSequence[] status_options = {"Cancel","Pending"};
    int selection = 1;*/

    public pickuplistForExecutiveAdapter(List<PickupList_Model_For_Executive> list, Context context) {
        this.list = list;
        this.context = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView item_m_pul;
        public TextView item_add_pul;
        public TextView itme_a_pul;
        public TextView item_p_pul;
        public TextView item_scanCount;

      //  public TextView item_scanTxtButton;
        public TextView item_phnNum;


        @SuppressLint("ResourceAsColor")
        public ViewHolder(final View itemView) {
            super(itemView);
            item_m_pul=itemView.findViewById(R.id.m_name_pul);
            item_add_pul=itemView.findViewById(R.id.m_add_pul);
            itme_a_pul=itemView.findViewById(R.id.a_qty_pul);
            item_p_pul=itemView.findViewById(R.id.p_qty_pul);
            item_scanCount=itemView.findViewById(R.id.scan_qty_pul);
            item_phnNum = itemView.findViewById(R.id.m_phn_num);
           // item_scanTxtButton=itemView.findViewById(R.id.txt_btn_scan);

            //underline phoneNumber
            item_phnNum.setPaintFlags(item_phnNum.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            item_phnNum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent =new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:01781278896"));
                    if (ActivityCompat.checkSelfPermission(v.getContext(),
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    v.getContext().startActivity(callIntent);
                }
            });

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.my_pickups_exe_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        viewHolder.item_m_pul.setText(list.get(i).getMerchant_name());
        viewHolder.item_add_pul.setText(list.get(i).getAddress());
        viewHolder.itme_a_pul.setText(list.get(i).getAssined_qty());
        viewHolder.item_p_pul.setText(list.get(i).getPicked_qty());
        viewHolder.item_scanCount.setText(list.get(i).getScan_count());
        viewHolder.item_phnNum.setText(list.get(i).getPhone_no());



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
