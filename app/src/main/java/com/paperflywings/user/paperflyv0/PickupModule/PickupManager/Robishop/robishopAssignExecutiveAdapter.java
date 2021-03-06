package com.paperflywings.user.paperflyv0.PickupModule.PickupManager.Robishop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import com.paperflywings.user.paperflyv0.Databases.Database;
import com.paperflywings.user.paperflyv0.R;

import java.util.ArrayList;
import java.util.List;

public class robishopAssignExecutiveAdapter extends RecyclerView.Adapter<robishopAssignExecutiveAdapter.ViewHolder> implements Filterable {
    private List<RobishopAssignManager_Model> robishop_modelList;
    private List<RobishopAssignManager_Model> robishop_modelListFull;
    Database database;

    private Context context;
    private robishopAssignExecutiveAdapter.OnItemClickListener mListener;

    public robishopAssignExecutiveAdapter() {

    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(robishopAssignExecutiveAdapter.OnItemClickListener listner) {
        this.mListener = listner;
    }

    public robishopAssignExecutiveAdapter(List<RobishopAssignManager_Model> robishop_modelList, Context context) {
        this.robishop_modelList = robishop_modelList;
        this.context = context;
        robishop_modelListFull = new ArrayList<>(robishop_modelList);
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView itemMerchantName;
        public TextView itemMerchantAddress;
        public TextView itemPickupMerchantName;
        public Button itembtnAssign;
        public ImageButton itemViewAssign;
        public TextView item_call;
        public TextView item_product_name;
        public ImageButton itemUpdateAssign;
        public CardView cardview;

        @SuppressLint("ResourceAsColor")
        public ViewHolder(View itemView) {
            super(itemView);
            database = new Database(context);
            itemMerchantName=itemView.findViewById(R.id.merchant_name_fulfillment);
            itemMerchantAddress=itemView.findViewById(R.id.m_add_fulfillment);
            itembtnAssign = itemView.findViewById(R.id.btn_assign_fulfillment);
            item_product_name = itemView.findViewById(R.id.product_name);
            item_call = itemView.findViewById(R.id.call_merchant_fulfillment);
            itemPickupMerchantName = itemView.findViewById(R.id.childMerchantName_fulfillment);

            itemViewAssign = itemView.findViewById(R.id.view_assign_fulfillment);
            itemUpdateAssign = itemView.findViewById(R.id.update_assigns_fulfillment);
            cardview = itemView.findViewById(R.id.card_view_assign_robishop);

            itembtnAssign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mListener!=null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            mListener.onItemClick(view, position);

                        }
                    }
                }


            });
        }
    }

    @Override
    public robishopAssignExecutiveAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.assign_robishop_pickup_layout, viewGroup, false);
        robishopAssignExecutiveAdapter.ViewHolder viewHolder = new robishopAssignExecutiveAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(robishopAssignExecutiveAdapter.ViewHolder viewHolder, int i) {
        RobishopAssignManager_Model RobishopAssignManager_Model = robishop_modelList.get(i);
        viewHolder.itemMerchantName.setText(RobishopAssignManager_Model.getMerchantName());
        final String companyPhone = RobishopAssignManager_Model.getPickupMerchantPhone();

        if(companyPhone.length() == 0)
        {
            viewHolder.itemPickupMerchantName.setText("No contact number available");
        }
        else{
            viewHolder.itemPickupMerchantName.setText("Contact: " +companyPhone);
        }
        final String p_address = RobishopAssignManager_Model.getPickMerchantAddress();
        if(p_address.length()==0)
        {
            viewHolder.itemMerchantAddress.setText("No Pickup Address");
        }
        else
        {
            viewHolder.itemMerchantAddress.setText("Add: "+p_address);
        }


       viewHolder.item_product_name.setText("Brief: "+RobishopAssignManager_Model.getProductBrief());
       viewHolder.item_call.setText(RobishopAssignManager_Model.getMerOrderRef());
    }



    @Override
    public int getItemCount() {
        return robishop_modelList.size();
    }

    //search/filter list
    @Override
    public Filter getFilter() {
        return NamesFilter;
    }

    private Filter NamesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<RobishopAssignManager_Model> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(robishop_modelListFull);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                /*for (AjkerDealAssignManager_Model item : ajkerdeal_modelListFull){
                    if (item.getApiOrderID().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }*/
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            robishop_modelList.clear();
            robishop_modelList.addAll((List) results.values);
            notifyDataSetChanged();

        }
    };
}
