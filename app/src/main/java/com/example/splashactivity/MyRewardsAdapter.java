package com.example.splashactivity;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.Timestamp;
import java.util.Date;


import java.text.SimpleDateFormat;
import java.util.List;

public class MyRewardsAdapter extends RecyclerView.Adapter<MyRewardsAdapter.Viewholder> {

    private List<RewardModel>rewardModelsList;
    private Boolean useMiniLayout=false;
    private RecyclerView coupensRecyclerView;
    private LinearLayout selectedCoupen;
    private String productOriginalPrice;
    private TextView selectedCoupenTitle,selectedCoupenExpiryDate,selectedCoupenBody,discountedPrice;
    private int cartItemPosition=-1;
    private List<CartItemModel>cartItemModelList;

    public MyRewardsAdapter(List<RewardModel> rewardModelsList, Boolean useMiniLayout) {
        this.rewardModelsList = rewardModelsList;
        this.useMiniLayout=useMiniLayout;
    }

    public MyRewardsAdapter(List<RewardModel> rewardModelsList, Boolean useMiniLayout, RecyclerView coupensRecyclerView, LinearLayout selectedCoupen, String productOriginalPrice, TextView coupenTitle, TextView coupenExpiryDate, TextView coupenBody,TextView discountedPrice) {
        this.rewardModelsList = rewardModelsList;
        this.useMiniLayout=useMiniLayout;
        this.coupensRecyclerView=coupensRecyclerView;
        this.selectedCoupen=selectedCoupen;
        this.productOriginalPrice=productOriginalPrice;
        this.selectedCoupenTitle=coupenTitle;
        this.selectedCoupenExpiryDate=coupenExpiryDate;
        this.selectedCoupenBody=coupenBody;
        this.discountedPrice=discountedPrice;
    }
    public MyRewardsAdapter(int cartItemPosition,List<RewardModel> rewardModelsList, Boolean useMiniLayout, RecyclerView coupensRecyclerView, LinearLayout selectedCoupen, String productOriginalPrice, TextView coupenTitle, TextView coupenExpiryDate, TextView coupenBody,TextView discountedPrice,List<CartItemModel>cartItemModelList) {
        this.rewardModelsList = rewardModelsList;
        this.useMiniLayout=useMiniLayout;
        this.coupensRecyclerView=coupensRecyclerView;
        this.selectedCoupen=selectedCoupen;
        this.productOriginalPrice=productOriginalPrice;
        this.selectedCoupenTitle=coupenTitle;
        this.selectedCoupenExpiryDate=coupenExpiryDate;
        this.selectedCoupenBody=coupenBody;
        this.discountedPrice=discountedPrice;
        this.cartItemPosition=cartItemPosition;
        this.cartItemModelList=cartItemModelList;
    }


    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(useMiniLayout){
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.mini_rewards_item_layout,parent,false);
        }else{
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.rewards_item_layout,parent,false);
        }
       return  new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        String coupenId=rewardModelsList.get(position).getCoupenId();
        String type=rewardModelsList.get(position).getType();
        Timestamp validity=rewardModelsList.get(position).getTimeStamp();
        String body=rewardModelsList.get(position).getCoupenBody();
        String lowerLimit=rewardModelsList.get(position).getLowerLimit();
        String upperLimit=rewardModelsList.get(position).getUpperLimit();
        String discountORamount=rewardModelsList.get(position).getDiscountOrAmount();
        boolean alreadyUsed=rewardModelsList.get(position).getAlreadyUsed();
        holder.setData(coupenId,type,validity,body,upperLimit,lowerLimit,discountORamount,alreadyUsed);
    }

    @Override
    public int getItemCount() {
        return rewardModelsList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{

        private TextView coupenTitle;
        private TextView coupenExpiryDate;
        private TextView coupenBody;


        public Viewholder(@NonNull View itemView) {
            super(itemView);
            coupenTitle=itemView.findViewById(R.id.coupen_title);
            coupenExpiryDate=itemView.findViewById(R.id.coupen_validity);
            coupenBody=itemView.findViewById(R.id.coupen_body);

        }
        private void setData(final String coupenId,final String type, Timestamp validity, String body, String upperLimit, String lowerLimit, String distORamount,boolean alreadyUsed){
            if(type.equals("Discount")){
                coupenTitle.setText(type);

            }else{
                coupenTitle.setText("FLAT Rs."+distORamount+"OFF");
            }
            final  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
            if(alreadyUsed){
                coupenExpiryDate.setText("Already Used");
                coupenExpiryDate.setTextColor(itemView.getContext().getResources().getColor(R.color.btnRed));
                coupenBody.setTextColor(Color.parseColor("#50ffffff"));
                coupenTitle.setTextColor(Color.parseColor("#50ffffff"));
            }else{
                coupenBody.setTextColor(Color.parseColor("#ffffff"));
                coupenTitle.setTextColor(Color.parseColor("#ffffff"));
                coupenExpiryDate.setTextColor(itemView.getContext().getResources().getColor(R.color.coupenPurple));

                coupenExpiryDate.setText(simpleDateFormat.format(validity.toDate()));
            }

            coupenBody.setText(body);

            if(useMiniLayout){
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(!alreadyUsed){


                            selectedCoupenTitle.setText(type);
                            if (validity != null) {
                                selectedCoupenExpiryDate.setText("unknown");
                            }

                            selectedCoupenBody.setText(body);

                            if (Long.valueOf(productOriginalPrice) > Long.valueOf(lowerLimit) && Long.valueOf(productOriginalPrice) < Long.valueOf(upperLimit)) {
                                if (type.equals("Discount")) {
                                    Long discountAmount = Long.valueOf(productOriginalPrice) * Long.valueOf(distORamount) / 100;
                                    discountedPrice.setText("Rs." + String.valueOf(Long.valueOf(productOriginalPrice) - discountAmount) + "/-");
                                } else {
                                    discountedPrice.setText("Rs." + String.valueOf(Long.valueOf(productOriginalPrice) - Long.valueOf(distORamount)) + "/-");
                                }
                                if (cartItemPosition!=-1) {
                                    cartItemModelList.get(cartItemPosition).setSelectedCoupenId(coupenId);
                                }
                            } else {
                                if (cartItemPosition!=-1) {
                                    cartItemModelList.get(cartItemPosition).setSelectedCoupenId(null);
                                }
                                discountedPrice.setText("Invalid");
                                Toast.makeText(itemView.getContext(), "Sorry! Product does not matches the coupen terms.", Toast.LENGTH_SHORT).show();

                            }

                            if (coupensRecyclerView.getVisibility() == View.GONE) {
                                coupensRecyclerView.setVisibility(View.VISIBLE);
                                selectedCoupen.setVisibility(View.GONE);
                            } else {
                                coupensRecyclerView.setVisibility(View.GONE);
                                selectedCoupen.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }

        }
    }

}
