package com.paperflywings.user.paperflyv0.DeliveryApp.DeliverySupervisor.ListFragmentContent.DeliverySuperVisorDp2Done;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.paperflywings.user.paperflyv0.Databases.BarcodeDbHelper;
import com.paperflywings.user.paperflyv0.R;

import java.util.ArrayList;
import java.util.List;

public class DeliverySupDp2DoneAdapter extends RecyclerView.Adapter<DeliverySupDp2DoneAdapter.ViewHolder>implements Filterable {

    private List<DeliverySupDp2DoneModel> listfull;
    private List<DeliverySupDp2DoneModel> list;

    private int currentPosition = -1;
    private Context context;
    BarcodeDbHelper db;
    private OnItemClickListener mListner;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listner) {
        this.mListner = listner;
    }

    public DeliverySupDp2DoneAdapter(List<DeliverySupDp2DoneModel>list, Context context){
        this.list = list;
        this.context = context;
        listfull = new ArrayList<>(list);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public TextView item_ordId;
        public TextView item_merOrderRef;
        public TextView item_merchantName;
        public TextView item_pickMerchantName;
      //  public TextView item_orderdate;
        public TextView item_dp2_time;
        public TextView item_dp2_by;
        public TextView item_packagePrice;
        public TextView item_productBrief;
        public TextView item_sla_miss;
        public TextView item_cust_name;
        public TextView item_cust_address;
        public Button item_dp2_done_button;
        public CardView card_view;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            item_ordId=itemView.findViewById(R.id.sup_orderId_dp2done);
            item_merOrderRef=itemView.findViewById(R.id.m_order_ref_dp2done);
            item_merchantName=itemView.findViewById(R.id.sup_m_name_dp2_done);
//            item_pickMerchantName=itemView.findViewById(R.id.pick_m_name);
           // item_orderdate=itemView.findViewById(R.id.sup_order_date);
            item_dp2_time=itemView.findViewById(R.id.sup_dp2_time);
            item_dp2_by=itemView.findViewById(R.id.sup_dp2_by);
            item_packagePrice=itemView.findViewById(R.id.price_dp2_done);
            item_productBrief=itemView.findViewById(R.id.package_brief_dp2_done);
            item_sla_miss = itemView.findViewById(R.id.sla_deliverytime);
            item_dp2_done_button = itemView.findViewById(R.id.btn_dp2_done);
            item_cust_name = itemView.findViewById(R.id.custname);
            item_cust_address = itemView.findViewById(R.id.custAddress);

            item_dp2_done_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListner != null) {
                        // Position of the item will be saved in this variable
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListner.onItemClick(itemView, position);
                        }
                    }
                }
            });


        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.deliverysupervisor_dp2_done,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.item_ordId.setText(list.get(i).getOrderid());
        viewHolder.item_merOrderRef.setText(list.get(i).getMerOrderRef());
        viewHolder.item_cust_name.setText("Customer Name: "+list.get(i).getCustname());
        viewHolder.item_cust_address.setText("Address: "+list.get(i).getCustaddress());
        viewHolder.item_merOrderRef.setText(list.get(i).getMerOrderRef());

        String pickMerchantName = list.get(i).getPickMerchantName();
        int DeliveryTime = list.get(i).getSlaMiss();

        if(pickMerchantName.equals("")){
            viewHolder.item_merchantName.setText(list.get(i).getMerchantName());
        } else if (!pickMerchantName.equals("")) {
            viewHolder.item_merchantName.setText(list.get(i).getPickMerchantName());
        }

        // viewHolder.item_pickMerchantName.setText("Pick Merchant Name: "+list.get(i).getPickMerchantName());
       // viewHolder.item_orderdate.setText(" Order Date: "+list.get(i).getOrderDate());
        viewHolder.item_dp2_time.setText(" Dp2 Time: "+list.get(i).getDp2Time());
        viewHolder.item_dp2_by.setText(" Dp2 By: "+list.get(i).getDp2By());

        if(DeliveryTime<0) {
            viewHolder.item_sla_miss.setText(String.valueOf(list.get(i).getSlaMiss()));
            viewHolder.item_sla_miss.setBackgroundResource(R.color.red);
            viewHolder.item_sla_miss.setTextColor(Color.WHITE);
        }
        else if (DeliveryTime>=0){
            try{
                viewHolder.item_sla_miss.setText(String.valueOf(list.get(i).getSlaMiss()));
                viewHolder.item_sla_miss.setBackgroundResource(R.color.green);
                viewHolder.item_sla_miss.setTextColor(Color.WHITE); }
            catch (Exception e){
                Toast.makeText(context, "DeliveryOnholdAdapter "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        viewHolder.item_packagePrice.setText(list.get(i).getPackagePrice()+" Taka");
        viewHolder.item_productBrief.setText("Product Brief:  "+list.get(i).getProductBrief());
    }


    @Override
    public int getItemCount() {
        return list.size();
    }



    @Override
    public Filter getFilter() {
        return NamesFilter;
    }
    private Filter NamesFilter = new Filter() {


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<DeliverySupDp2DoneModel>filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(listfull);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(DeliverySupDp2DoneModel item: listfull){
                    if (item.getOrderid().toLowerCase().contains(filterPattern) || item.getMerOrderRef().toLowerCase().contains(filterPattern) || item.getMerchantName().toLowerCase().contains(filterPattern) || item.getPickMerchantName().toLowerCase().contains(filterPattern) || item.getCustname().toLowerCase().contains(filterPattern) || item.getCustphone().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((List) results.values);
            notifyDataSetChanged();
        }


    };
}
