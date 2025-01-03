package com.example.splashactivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.Viewholder> {



    private List<MyOrderItemModel> myOrderItemModelList;
    private static Dialog loadingDialog;
    public MyOrderAdapter(List<MyOrderItemModel> myOrderItemModelList,Dialog loadingDialog) {
        this.myOrderItemModelList = myOrderItemModelList;
   this.loadingDialog=loadingDialog;

    }

    @NonNull
    @Override
    public MyOrderAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_order_item_layout,viewGroup,false);

                return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderAdapter.Viewholder holder, int position) {
        String productID = myOrderItemModelList.get(position).getProductId();
        String resource = myOrderItemModelList.get(position).getProductImage();
        int rating =myOrderItemModelList.get(position).getRating();
        String title = myOrderItemModelList.get(position).getProductTitle();
        String orderStatus =myOrderItemModelList.get(position).getOrderStatus();
        Date date;
        switch (orderStatus)
        {
            case "Ordered":
                date = myOrderItemModelList.get(position).getOrderedDate().toDate(); // Convert Timestamp to Date
                break;
            case "Packed":
                date = myOrderItemModelList.get(position).getPackedDate().toDate(); // Convert Timestamp to Date
                break;
            case "Shipped":
                date = myOrderItemModelList.get(position).getShippedDate().toDate(); // Convert Timestamp to Date
                break;
            case "Delivered":
                date = myOrderItemModelList.get(position).getDeliveredDate().toDate(); // Convert Timestamp to Date
                break;
            case "Cancelled":
                date = myOrderItemModelList.get(position).getCancelledDate().toDate(); // Convert Timestamp to Date
                break;
            default:
                date = myOrderItemModelList.get(position).getCancelledDate().toDate(); // Convert Timestamp to Date

        }

        holder.setData(resource,title,orderStatus,date,rating,productID,position);


    }

    @Override
    public int getItemCount() {
        return myOrderItemModelList.size();
    }
    static class Viewholder extends RecyclerView.ViewHolder
    {
        private ImageView productImage;
        private ImageView orderIndicator;
        private TextView productTitle;
        private TextView deliveryStatus;
        private LinearLayout ratenowcontainer;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productimage);
            productTitle = itemView.findViewById(R.id.producttitle);
            orderIndicator = itemView.findViewById(R.id.order_indicator);
            deliveryStatus = itemView.findViewById(R.id.order_delivered_date);
            ratenowcontainer = itemView.findViewById(R.id.ratenow);

        }

        private void  setData(String resource,String title,String orderStatus,Date date,int rating,String productID,int position)
        {
            Glide.with(itemView.getContext()).load(resource).into(productImage);
            productTitle.setText(title);

            if(orderStatus.equals("Cancelled")) {//in video it was deliveredDate
                orderIndicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(com.google.android.material.R.color.design_default_color_primary)));
            }else {
                orderIndicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(com.google.android.material.R.color.material_blue_grey_800)));
            }
            SimpleDateFormat simpleDateFormat = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                simpleDateFormat = new SimpleDateFormat("EEE,dd MMM YYYY hh:mm aa");
            }

            deliveryStatus.setText(orderStatus+String.valueOf(simpleDateFormat.format(date)));

            itemView.setOnClickListener(new  View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent orderDetailsIntent=new Intent(itemView.getContext(),OrderDetailsActivity.class);
                    orderDetailsIntent.putExtra("Position",position);
                    itemView.getContext().startActivity(orderDetailsIntent);

                }


            });
            ///rating layout
            setRating(rating);
            for(int x=0;x<ratenowcontainer.getChildCount();x++){
                final int starposition=x;
                ratenowcontainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadingDialog.show();
                        setRating(starposition);
                        DocumentReference documentReference= FirebaseFirestore.getInstance().collection("PRODUCTS").document(productID);
                      FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<Object>() {
                          @Nullable
                          @Override
                          public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                              DocumentSnapshot documentSnapshot=transaction.get(documentReference);
                              if (rating!=0){
                                  Long increase=documentSnapshot.getLong(starposition+1+"_star")+1;
                                  Long decrease=documentSnapshot.getLong(rating+1+"_star")-1;
                                  transaction.update(documentReference,starposition+1+"_star",increase);
                                  transaction.update(documentReference,rating+1+"_star",decrease);
                              }else{
                                  Long increase=documentSnapshot.getLong(starposition+1+"_star")+1;
                                  transaction.update(documentReference,starposition+1+"_star",increase);
                              }
                              return null;
                          }
                      }).addOnSuccessListener(new OnSuccessListener<Object>() {
                          @Override
                          public void onSuccess(Object o) {

                              Map<String, Object> myRating = new HashMap<>();

                              if (DBqueries.myRatedIds.contains(productID)) {

                                  myRating.put("rating_" + DBqueries.myRatedIds.indexOf(productID), (long) starposition + 1);


                              } else {
                                  myRating.put("list_size", (long) DBqueries.myRatedIds.size() + 1);
                                  myRating.put("product_ID_" + DBqueries.myRatedIds.size(), productID);
                                  myRating.put("rating_" + DBqueries.myRatedIds.size(), (long) starposition + 1);

                              }

                              FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_RATINGS").update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                                                             @Override
                                                                                                                                                                                             public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                                                                 if (task.isSuccessful()) {

                                                                                                                                                                                                     DBqueries.myOrderItemModelList.get(position).setRating(starposition);
                                                                                                                                                                                                     if (DBqueries.myRatedIds.contains(productID)){
                                                                                                                                                                                                         DBqueries.myRating.set(DBqueries.myRatedIds.indexOf(productID),Long.parseLong(String.valueOf(starposition+1)));
                                                                                                                                                                                                     }else{
                                                                                                                                                                                                         DBqueries.myRatedIds.add(productID);
                                                                                                                                                                                                         DBqueries.myRating.add(Long.parseLong(String.valueOf(starposition+1)));
                                                                                                                                                                                                     }


                                                                                                                                                                                                 }else {
                                                                                                                                                                                                     String error=task.getException().getMessage();
                                                                                                                                                                                                     Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                                                                                                                                                                                 }
                                                                                                                                                                                                 loadingDialog.dismiss();
                                                                                                                                                                                             }
                                                                                                                                                                                         });


                          }
                      }).addOnFailureListener(new OnFailureListener() {
                          @Override
                          public void onFailure(@NonNull Exception e) {
                              loadingDialog.show();
                          }
                      });
                    }


                });

            }
            //rating layout
        }
        private void setRating(int starposition) {
            for(int x=0;x<ratenowcontainer.getChildCount();x++){
                ImageView starBtn=(ImageView) ratenowcontainer.getChildAt(x);
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));
                if(x<=starposition){
                    starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffbb00")));
                }
            }
        }
    }
}
