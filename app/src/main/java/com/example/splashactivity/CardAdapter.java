package com.example.splashactivity;

import static com.example.splashactivity.DBqueries.firebaseFirestore;
import static com.example.splashactivity.MyCartFragment.cartAdapter;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Distribution;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.C;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CardAdapter extends RecyclerView.Adapter {


    private List<CartItemModel> cartItemModelList;
    private int lastPosition=-1;
    private TextView cartTotalAmount;
    private boolean showDeleteBtn;

    public CardAdapter(List<CartItemModel> cartItemModelList,TextView cartTotalAmount,boolean showDeleteBtn) {
        this.cartItemModelList = cartItemModelList;
        this.cartTotalAmount = cartTotalAmount;
        this.showDeleteBtn = showDeleteBtn;
    }


    @Override
    public int getItemViewType(int position) {
       switch (cartItemModelList.get(position).getType()){
           case 0:
               return CartItemModel.CART_ITEM;
           case 1:
               return CartItemModel.TOTAL_AMOUNT;
           default:
               return -1;
       }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case CartItemModel.CART_ITEM:
                View cardItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
                return new CartItemViewHolder(cardItemView);
            case CartItemModel.TOTAL_AMOUNT:
                View cartTotalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_total_amount_layout, parent, false);
                return new CartTotalAmountViewHolder(cartTotalView);
            default:
                return null;
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (cartItemModelList.get(holder.getAdapterPosition()).getType()){
            case CartItemModel.CART_ITEM:
                String productID = cartItemModelList.get(holder.getAdapterPosition()).getProductID();
                String resource=cartItemModelList.get(holder.getAdapterPosition()).getProductImage();
                String title=cartItemModelList.get(holder.getAdapterPosition()).getProductTitle();
                long freeCoupens=cartItemModelList.get(holder.getAdapterPosition()).getFreeCoupens();
                String productPrice=cartItemModelList.get(holder.getAdapterPosition()).getProductPrice();
                String cuttedPrice=cartItemModelList.get(holder.getAdapterPosition()).getCuttedPrice();
                long offersApplied=cartItemModelList.get(holder.getAdapterPosition()).getOffersApplied();
                boolean inStock = cartItemModelList.get(holder.getAdapterPosition()).isInStock();
                Long productQuantity=cartItemModelList.get(holder.getAdapterPosition()).getProductQuantity();
                Long maxQuantity=cartItemModelList.get(holder.getAdapterPosition()).getMaxQuantity();
                boolean qtyError=cartItemModelList.get(holder.getAdapterPosition()).isQtyError();
                List<String>qtyIds=cartItemModelList.get(holder.getAdapterPosition()).getQtyIDs();
                long stockQty=cartItemModelList.get(holder.getAdapterPosition()).getStockQuantity();
                boolean COD=cartItemModelList.get(position).isCOD();
                ((CartItemViewHolder)holder).setItemDetails(productID,resource,title,freeCoupens,productPrice,cuttedPrice,offersApplied,position,inStock,String.valueOf(productQuantity),maxQuantity,qtyError,qtyIds,stockQty,COD);
                break;
            case CartItemModel.TOTAL_AMOUNT:
                int totalItems = 0;
                int totalItemPrice=0;
                String deliveryPrice;
                int totalAmount;
                int savedAmount=0;

                for (int x=0;x<cartItemModelList.size();x++){
                    if(cartItemModelList.get(x).getType()==CartItemModel.CART_ITEM && cartItemModelList.get(x).isInStock()){
                        int quantity=Integer.parseInt(String.valueOf(cartItemModelList.get(x).getProductQuantity()));
                        totalItems=totalItems+quantity;
                        if (TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCoupenId())) {
                            totalItemPrice = totalItemPrice + Integer.parseInt(cartItemModelList.get(x).getProductPrice())*quantity;
                        }else{
                            totalItemPrice = totalItemPrice + Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice())*quantity;
                        }

                        if (!TextUtils.isEmpty(cartItemModelList.get(x).getCuttedPrice())){
                            savedAmount=savedAmount+(Integer.parseInt(cartItemModelList.get(x).getCuttedPrice())-Integer.parseInt(cartItemModelList.get(x).getProductPrice())) *quantity;
                            if (!TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCoupenId())){
                                savedAmount=savedAmount+(Integer.parseInt(cartItemModelList.get(x).getProductPrice())-Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice()))*quantity;
                            }
                        }else{
                            if (!TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCoupenId())){
                                savedAmount=savedAmount+(Integer.parseInt(cartItemModelList.get(x).getProductPrice())-Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice()))*quantity;
                            }
                        }
                    }
                }
                if (totalItemPrice>500){
                    deliveryPrice="FREE";
                    totalAmount=totalItemPrice;
                }else{
                    deliveryPrice="60";
                    totalAmount=totalItemPrice+60;
                }
                 cartItemModelList.get(position).setTotalItems(totalItems);
                cartItemModelList.get(position).setTotalItemsPrice(totalItemPrice);
                cartItemModelList.get(position).setDeliveryPrice(deliveryPrice);
                cartItemModelList.get(position).setTotalAmoount(totalAmount);
                cartItemModelList.get(position).setSavedAmount(savedAmount);
                ((CartTotalAmountViewHolder)holder).setTotalAmount(totalItems,totalItemPrice,deliveryPrice,totalAmount,savedAmount);
                break;
            default:
                return;
        }

        if (lastPosition < holder.getAdapterPosition()) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return cartItemModelList.size();
    }

    class CartItemViewHolder extends RecyclerView.ViewHolder{


        private ImageView productImage;
        private TextView productTitle;
        private TextView freeCoupens;
        private TextView productPrice;
        private TextView cuttedPrice;
        private TextView offersApplied;
        private TextView coupensApplied;
        private TextView productQuantity;
        private ImageView freeCoupensIcon;
        private LinearLayout deleteBtn;
        private LinearLayout coupenRedeemptionLayout;
        private TextView coupenRedemptionBody;
        private Button redeemBtn;
        private ImageView codIndicator;
        //coupendialog
        private TextView coupenTitle;
        private TextView coupenExpiryDate;
        private TextView coupenBody;
        private RecyclerView coupensRecyclerView;
        private LinearLayout selectedCoupen;
        private TextView discountedPrice;
        private TextView originalPrice;
        private  Button removeCoupenBtn,applyCoupenBtn;
        private  LinearLayout applyORremoveBtnContainer;
        private TextView fooferText;
        private  String productOriginalPrice;

        //coupendialog


        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage=itemView.findViewById(R.id.product_image);
            productTitle=itemView.findViewById(R.id.product_title);
            freeCoupensIcon=itemView.findViewById(R.id.free_coupen_icon);
            freeCoupens=itemView.findViewById(R.id.tv_free_coupen);
            productPrice=itemView.findViewById(R.id.product_price);
            cuttedPrice=itemView.findViewById(R.id.cutted_price);
            offersApplied=itemView.findViewById(R.id.offers_applied);
            coupensApplied=itemView.findViewById(R.id.coupens_applied);
            productQuantity=itemView.findViewById(R.id.product_quantity);
            coupenRedeemptionLayout = itemView.findViewById(R.id.coupen_redemption_layout);
            codIndicator=itemView.findViewById(R.id.cod_indicator);
            redeemBtn=itemView.findViewById(R.id.coupen_redeemption_btn);
            coupenRedemptionBody=itemView.findViewById(R.id.tv_coupen_redeemption);


            deleteBtn = itemView.findViewById(R.id.remove_item_btn);
        }
        private void setItemDetails(String productID,String resource,String title,long freeCoupensNo,String productPriceText,String cuttedPriceText,long offerAppliedNumber,int position,boolean inStock,String quantity,long maxQuantity ,boolean qtyError,List<String> qtyIds,long stockQty,boolean COD){

            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.img)).into(productImage);
            productTitle.setText(title);


            final Dialog checkCoupenPriceDialog = new Dialog(itemView.getContext());
            checkCoupenPriceDialog.setContentView(R.layout.coupen_redeem_dialog);
            checkCoupenPriceDialog.setCancelable(false);
            checkCoupenPriceDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

if(COD)
{
    codIndicator.setVisibility(View.VISIBLE);
}
else
{
    codIndicator.setVisibility(View.INVISIBLE);
}
            if (inStock) {

                if(freeCoupensNo > 0){
                    freeCoupensIcon.setVisibility(View.VISIBLE);
                    freeCoupens.setVisibility(View.VISIBLE);
                    if(freeCoupensNo==1) {
                        freeCoupens.setText("free " + freeCoupensNo + " Coupen");
                    }else{
                        freeCoupens.setText("free " + freeCoupensNo + " Coupens");
                    }

                }else{
                    freeCoupensIcon.setVisibility(View.INVISIBLE);
                    freeCoupens.setVisibility(View.INVISIBLE);
                }

                productPrice.setText("Rs."+productPriceText+"/-");
                productPrice.setTextColor(Color.parseColor("#000000"));
                cuttedPrice.setText("Rs."+cuttedPriceText+"/-");
                coupenRedeemptionLayout.setVisibility(View.VISIBLE);

                //coupen dialog

                ImageView toggleRecyclerView = checkCoupenPriceDialog.findViewById(R.id.toggle_recyclerview);
                coupensRecyclerView = checkCoupenPriceDialog.findViewById(R.id.coupens_recyclerview);
                selectedCoupen = checkCoupenPriceDialog.findViewById(R.id.selected_coupen);
                coupenTitle = checkCoupenPriceDialog.findViewById(R.id.coupen_title);
                coupenExpiryDate = checkCoupenPriceDialog.findViewById(R.id.coupen_validity);
                coupenBody = checkCoupenPriceDialog.findViewById(R.id.coupen_body);
                removeCoupenBtn=checkCoupenPriceDialog.findViewById(R.id.remove_btn);
                applyCoupenBtn=checkCoupenPriceDialog.findViewById(R.id.apply_btn);
                fooferText=checkCoupenPriceDialog.findViewById(R.id.footer_text);
                applyORremoveBtnContainer=checkCoupenPriceDialog.findViewById(R.id.apply_orremove_btns_container);
                fooferText.setVisibility(View.GONE);
                applyORremoveBtnContainer.setVisibility(View.VISIBLE);


                originalPrice = checkCoupenPriceDialog.findViewById(R.id.original_price);
                discountedPrice = checkCoupenPriceDialog.findViewById(R.id.discounted_price);



                LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                coupensRecyclerView.setLayoutManager(layoutManager);
                originalPrice.setText(productPrice.getText());
                productOriginalPrice=productPriceText;
                MyRewardsAdapter myRewardsAdapter = new MyRewardsAdapter(position,DBqueries.rewardModelList, true,coupensRecyclerView,selectedCoupen,productOriginalPrice,coupenTitle,coupenExpiryDate,coupenBody,discountedPrice,cartItemModelList);
                coupensRecyclerView.setAdapter(myRewardsAdapter);
                myRewardsAdapter.notifyDataSetChanged();

                applyCoupenBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCoupenId())) {

                            for (RewardModel rewardModel : DBqueries.rewardModelList) {
                                if (rewardModel.getCoupenId().equals(cartItemModelList.get(position).getSelectedCoupenId())) {
                                    rewardModel.setAlreadyUsed(true);
                                    coupenRedeemptionLayout.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.reward__gradient_background));
                                    coupenRedemptionBody.setText(rewardModel.getCoupenBody());
                                    redeemBtn.setText("Coupen");
                                }
                            }
                            coupensApplied.setVisibility(View.VISIBLE);
                            cartItemModelList.get(position).setDiscountedPrice(discountedPrice.getText().toString().substring(3,discountedPrice.getText().length()-2));
                            productPrice.setText(discountedPrice.getText());
                            String offerDiscountedAmount = String.valueOf(Long.valueOf(productPriceText)-Long.valueOf(discountedPrice.getText().toString().substring(3,discountedPrice.getText().length()-2)));
                            coupensApplied.setText("Coupen applied -Rs."+offerDiscountedAmount+"/-");
                            notifyItemChanged(cartItemModelList.size()-1);
                            checkCoupenPriceDialog.dismiss();
                        }
                    }
                });
                removeCoupenBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (RewardModel rewardModel: DBqueries.rewardModelList) {
                            if (rewardModel.getCoupenId().equals(cartItemModelList.get(position).getSelectedCoupenId())){
                                rewardModel.setAlreadyUsed(false);
                            }
                        }
                        coupenTitle.setText("Coupen");
                        coupenExpiryDate.setText("Validity");
                        coupenBody.setText("Tap the icon on the top right corner to select your coupen");
                        coupensApplied.setVisibility(View.INVISIBLE);
                        coupenRedeemptionLayout.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.coupen_red));
                        coupenRedemptionBody.setText("Apply your coupen here.");
                        redeemBtn.setText("Redeem");
                        cartItemModelList.get(position).setSelectedCoupenId(null);
                        productPrice.setText("Rs."+productPriceText+"/-");
                        notifyItemChanged(cartItemModelList.size()-1);
                        checkCoupenPriceDialog.dismiss();
                    }
                });

                toggleRecyclerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialogRecyclerView();
                    }
                });



                if (!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCoupenId())) {

                    for (RewardModel rewardModel : DBqueries.rewardModelList) {
                        if (rewardModel.getCoupenId().equals(cartItemModelList.get(position).getSelectedCoupenId())) {
                            coupenRedeemptionLayout.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.reward__gradient_background));
                            coupenRedemptionBody.setText(rewardModel.getCoupenBody());
                            redeemBtn.setText("Coupen");

                            coupenBody.setText(rewardModel.getCoupenBody());
                            if(rewardModel.getType().equals("Discount")){
                                coupenTitle.setText(rewardModel.getType());

                            }else{
                                coupenTitle.setText("FLAT Rs."+rewardModel.getDiscountOrAmount()+"OFF");
                            }
                            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
                            coupenExpiryDate.setText(simpleDateFormat.format(rewardModel.getTimeStamp().toDate()));
                        }
                    }
                    discountedPrice.setText("Rs."+cartItemModelList.get(position).getDiscountedPrice()+"/-");
                    coupensApplied.setVisibility(View.VISIBLE);
                    productPrice.setText("Rs."+cartItemModelList.get(position).getDiscountedPrice()+"/-");
                    String offerDiscountedAmount = String.valueOf(Long.valueOf(productPriceText)-Long.valueOf(cartItemModelList.get(position).getDiscountedPrice()));
                    coupensApplied.setText("Coupen applied -Rs."+offerDiscountedAmount+"/-");
                }else{
                    coupensApplied.setVisibility(View.INVISIBLE);
                    coupenRedeemptionLayout.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.coupen_red));
                    coupenRedemptionBody.setText("Apply your coupen here.");
                    redeemBtn.setText("Redeem");
                }

                //coupen dialog

                productQuantity.setText("Qty: " + quantity);
               if(!showDeleteBtn) {
                   if (qtyError) {
                       productQuantity.setTextColor(itemView.getContext().getResources().getColor(R.color.btnRed));
                       productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.btnRed)));

                   } else {
                       productQuantity.setTextColor(itemView.getContext().getResources().getColor(android.R.color.black));
                       productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(android.R.color.black)));

                   }
               }

                productQuantity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog quantityDialog = new Dialog(itemView.getContext());
                        quantityDialog.setContentView(R.layout.quantity_dialog);
                        quantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        quantityDialog.setCancelable(false);
                        EditText quantityNo=quantityDialog.findViewById(R.id.quantity_no);
                        Button cancelBtn = quantityDialog.findViewById(R.id.cancel_btn);
                        Button okBtn = quantityDialog.findViewById(R.id.ok_btn);
                        quantityNo.setHint("Max "+String.valueOf(maxQuantity));
                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                quantityDialog.dismiss();
                            }
                        });

                        okBtn.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                if(!TextUtils.isEmpty(quantityNo.getText())) {
                                    if (Long.valueOf(quantityNo.getText().toString()) <= maxQuantity && Long.valueOf(quantityNo.getText().toString()) != 0 ) {

                                        if(itemView.getContext() instanceof MainActivity)
                                        {
                                            cartItemModelList.get(position).setProductQuantity(Long.parseLong(quantityNo.getText().toString()));

                                        }
                                        else {
                                            if (DeliveryActivity.fromCart) {
                                                cartItemModelList.get(position).setProductQuantity(Long.parseLong(quantityNo.getText().toString()));
                                            } else {
                                                DeliveryActivity.cartItemModelList.get(position).setProductQuantity(Long.parseLong(quantityNo.getText().toString()));
                                            }
                                        }

                                        productQuantity.setText("Qty: " + quantityNo.getText());
                                        notifyItemChanged(cartItemModelList.size()-1);


                                        if(!showDeleteBtn){
                                            DeliveryActivity.loadingDialog.show();
                                            DeliveryActivity.cartItemModelList.get(position).setQtyError(false);
                                            int initialQty= Integer.parseInt(quantity);
                                            int finalQty=Integer.parseInt(quantityNo.getText().toString());
                                            FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
                                            if(finalQty > initialQty){


                                            for(int y=0;y<finalQty-initialQty;y++)
                                            {

                                                String quantityDocumentName= UUID.randomUUID().toString().substring(0,20);
                                                Map<String,Object> timestamp=new HashMap<>();
                                                timestamp.put("time", FieldValue.serverTimestamp());

                                               final int finalY = y;

                                                firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY").document(quantityDocumentName).set(timestamp)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                               qtyIds.add(quantityDocumentName);

                                                                if(finalY +1 == finalQty-initialQty)
                                                                {


                                                                    firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).limit(stockQty).get()
                                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                    if(task.isSuccessful())
                                                                                    {
                                                                                        List<String> serverQuantity=new ArrayList<>();
                                                                                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult())
                                                                                        {
                                                                                            serverQuantity.add(queryDocumentSnapshot.getId());

                                                                                        }

                                                                                        long availableQty=0;

                                                                                        for(  String qtyId : qtyIds){
                                                                                            if(!serverQuantity.contains(qtyId))
                                                                                            {
                                                                                                    DeliveryActivity.cartItemModelList.get(position).setQtyError(true);
                                                                                                    DeliveryActivity.cartItemModelList.get(position).setMaxQuantity(availableQty);
                                                                                                    Toast.makeText(itemView.getContext(), "Sorry ! all products may not be available  in required quantity...", Toast.LENGTH_SHORT).show();

                                                                                                }else{
                                                                                                availableQty++;




                                                                                            }

                                                                                        }
                                                                                        DeliveryActivity.cartAdapter.notifyDataSetChanged();
                                                                                    }
                                                                                    else
                                                                                    {
                                                                                        String error=task.getException().getMessage();
                                                                                        Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                                                                    }

                                                                                    DeliveryActivity.loadingDialog.dismiss();
                                                                                }

                                                                            });

                                                                }


                                                            }
                                                        });
                                            }
                                        }else if(initialQty > finalQty){
                                                for (int x=0;x<initialQty-finalQty;x++) {

                                                   String qtyId= qtyIds.get(qtyIds.size()-1-x);


                                                    int finalX = x;
                                                    firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY").document(qtyId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                           qtyIds.remove(qtyId);
                                                            DeliveryActivity.cartAdapter.notifyDataSetChanged();
                                                            if(finalX+1 ==initialQty-finalQty){
                                                                DeliveryActivity.loadingDialog.dismiss();
                                                            }

                                                        }
                                                    });
                                                }
                                        }
                                        }
                                    }
                                    else {

                                    Toast.makeText(itemView.getContext(), "Max quantity : "+String.valueOf(maxQuantity), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                    quantityDialog.dismiss();
                            }
                        });
                        quantityDialog.show();
                    }
                });
                if(offerAppliedNumber>0){
                    offersApplied.setVisibility(View.VISIBLE);
                    String offerDiscountedAmount = String.valueOf(Long.valueOf(cuttedPriceText)-Long.valueOf(productPriceText));
                    offersApplied.setText("Offer applied - Rs."+offerDiscountedAmount+"/-");
                }else{
                    offersApplied.setVisibility(View.INVISIBLE);
                }
            }else{
                productPrice.setText("Out of Stock");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(com.google.android.material.R.color.design_default_color_primary));
                cuttedPrice.setText("");
                coupenRedeemptionLayout.setVisibility(View.GONE);
                freeCoupens.setVisibility(View.INVISIBLE);
                productQuantity.setVisibility(View.INVISIBLE);
                coupensApplied.setVisibility(View.GONE);
                offersApplied.setVisibility(View.GONE);
                freeCoupensIcon.setVisibility(View.INVISIBLE);
            }

            if(showDeleteBtn){
                deleteBtn.setVisibility(View.VISIBLE);
            }else{
                deleteBtn.setVisibility(View.GONE);
            }


            redeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (RewardModel rewardModel: DBqueries.rewardModelList) {
                    if (rewardModel.getCoupenId().equals(cartItemModelList.get(position).getSelectedCoupenId())){
                        rewardModel.setAlreadyUsed(false);
                    }
                }
                checkCoupenPriceDialog.show();
            }
            });
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCoupenId())){
                        for (RewardModel rewardModel: DBqueries.rewardModelList) {
                            if (rewardModel.getCoupenId().equals(cartItemModelList.get(position).getSelectedCoupenId())){
                                rewardModel.setAlreadyUsed(false);
                            }
                        }
                    }

                    if(!ProductDetailsActivity.running_cart_query){
                        ProductDetailsActivity.running_cart_query=true;
                        DBqueries.removeFromCart(position,itemView.getContext(),cartTotalAmount);
                    }
                }
            });
        }
        private void showDialogRecyclerView() {
            if (coupensRecyclerView.getVisibility() == View.GONE) {
                coupensRecyclerView.setVisibility(View.VISIBLE);
                selectedCoupen.setVisibility(View.GONE);
            } else {
                coupensRecyclerView.setVisibility(View.GONE);
                selectedCoupen.setVisibility(View.VISIBLE);
            }
        }
    }

    class CartTotalAmountViewHolder extends RecyclerView.ViewHolder{

        private TextView totalItems;
        private TextView totalItemsPrice;
        private TextView deliveryPrice;
        private TextView totalAmount;
        private TextView savedAmount;

        public CartTotalAmountViewHolder(@NonNull View itemView) {
            super(itemView);

            totalItems=itemView.findViewById(R.id.total_items);
            totalItemsPrice=itemView.findViewById(R.id.total_items_price);
            deliveryPrice=itemView.findViewById(R.id.delivery_price);
            totalAmount=itemView.findViewById(R.id.total_price);
            savedAmount=itemView.findViewById(R.id.saved_amount);

        }
        private  void setTotalAmount(int totalItemText,int totalItemPriceText,String deliveryPriceText,int totalAmountText,int savedAmountText){

            totalItems.setText("Price("+totalItemText+" items)");
            totalItemsPrice.setText("Rs."+totalItemPriceText+"/-");
            if(deliveryPriceText.equals("FREE")) {
                deliveryPrice.setText(deliveryPriceText);
            }else{
                deliveryPrice.setText("Rs."+deliveryPriceText+"/-");
            }
            totalAmount.setText("Rs."+totalAmountText+"/-");
            cartTotalAmount.setText("Rs."+totalAmountText+"/-");
            savedAmount.setText("You saved Rs."+savedAmountText+"/- on this order.");

            LinearLayout parent = (LinearLayout)cartTotalAmount.getParent().getParent();
            if (totalItemPriceText==0){
                if(DeliveryActivity.fromCart) {
           cartItemModelList.remove(cartItemModelList.size() - 1);
                    DeliveryActivity.cartItemModelList.remove(DeliveryActivity.cartItemModelList.size() - 1);
                }
                if(showDeleteBtn){
                    cartItemModelList.remove(cartItemModelList.size() - 1);
                }
                parent.setVisibility(View.GONE);
            }else{
                parent.setVisibility(View.VISIBLE);
            }


        }
    }
}
