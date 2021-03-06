package com.paperflywings.user.paperflyv0.PickupModule.PickupSupervisor;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.paperflywings.user.paperflyv0.PickupModule.PickupSupervisor.FulfillmentAssignSupervisor.FulfillmentAssignPickup_Supervisor;
import com.paperflywings.user.paperflyv0.PickupModule.PickupSupervisor.InventorySupervisor.AssignInventorySupervisor;
import com.paperflywings.user.paperflyv0.PickupModule.PickupSupervisor.LogisticAssignSupervisor.AssignPickup_Supervisor;
import com.paperflywings.user.paperflyv0.PickupModule.PickupSupervisor.PickupTodaySupervisor.PickupsToday_Supervisor;
import com.paperflywings.user.paperflyv0.R;


public class RecyclerAdapterSupervisor extends RecyclerView.Adapter<RecyclerAdapterSupervisor.ViewHolder> {

//    private String[] titles = { "Assign(Logistic)","Assign(Fulfillment)","AjkerDeal(Direct Delivery)","Pickups Today"};
    private String[] titles = { "Assign(Logistic)","Assign(Fulfillment)","Assign(Inventory)","Pickups Today"};
//    private int[] images = { R.drawable.assignpickup,R.drawable.pickupstoday,R.drawable.ajkerdeal , R.drawable.pickupstoday};
    private int[] images = { R.drawable.assignpickup,R.drawable.pickupstoday,R.drawable.inventoryspace,R.drawable.summary};

    class ViewHolder extends RecyclerView.ViewHolder{

        public int currentItem;
        public ImageView itemImage;
        public TextView itemTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            itemImage = (ImageView)itemView.findViewById(R.id.item_image_supervisor);
            itemTitle = (TextView)itemView.findViewById(R.id.item_title_supervisor);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position == 0) {
                       Intent intent_assign = new Intent(v.getContext(), AssignPickup_Supervisor.class);
                        v.getContext().startActivity(intent_assign);
                    } else if (position == 1) {
                        Intent intent_assign_fulfillment = new Intent(v.getContext(), FulfillmentAssignPickup_Supervisor.class);
                        v.getContext().startActivity(intent_assign_fulfillment);
                    }
                    else if (position == 2) {
                        Intent intent_assign_inv = new Intent(v.getContext(), AssignInventorySupervisor.class);
                        v.getContext().startActivity(intent_assign_inv);
                    }
                    else {
                        Intent intent = new Intent(v.getContext(), PickupsToday_Supervisor.class);
                        v.getContext().startActivity(intent);
                    }
                               /* Snackbar.make(v, "Click detected on item " + position,
                                        Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();*/

                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.supervisor_card_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.itemTitle.setText(titles[i]);
        //viewHolder.itemDetail.setText(details[i]);
        viewHolder.itemImage.setImageResource(images[i]);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

}
