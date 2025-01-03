package com.example.splashactivity;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {
    private boolean fromSearch;
    private List<WishlistModel> wishlistModelList;
    private Boolean wishlist;
    private int lastPosition = -1;



    public WishlistAdapter(List<WishlistModel> wishlistModelList, Boolean wishlist) {
        this.wishlistModelList = wishlistModelList;
        this.wishlist = wishlist;
    }

    public boolean isFromSearch() {
        return fromSearch;
    }

    public void setFromSearch(boolean fromSearch) {
        this.fromSearch = fromSearch;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return wishlistModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productTitle;
        private TextView freeCoupens;
        private ImageView coupenIcon;
        private TextView rating;
        private TextView totalRatings;
        private View priceCut;
        private TextView productPrice;
        private TextView cuttedPrice;
        private TextView paymentmethod;
        private ImageButton deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.Product_Image);
            productTitle = itemView.findViewById(R.id.Product_title);
            freeCoupens = itemView.findViewById(R.id.free_coupen);
            coupenIcon = itemView.findViewById(R.id.Cupon_icon);
            rating = itemView.findViewById(R.id.tv_product_rating_miniview);
            totalRatings = itemView.findViewById(R.id.total_ratings);
            priceCut = itemView.findViewById(R.id.price_cut);
            productPrice = itemView.findViewById(R.id.product_price);
            cuttedPrice = itemView.findViewById(R.id.cutted_price);
            paymentmethod = itemView.findViewById(R.id.payment_method);
            deleteBtn = itemView.findViewById(R.id.imageButtondelete_button);
        }

        public void bind(int position) {
            String productId = wishlistModelList.get(position).getProductId();
            String resource = wishlistModelList.get(position).getProductImage();
            String title = wishlistModelList.get(position).getProductTitle();
            long freeCoupensNo = wishlistModelList.get(position).getFreeCoupens();
            String averageRate = wishlistModelList.get(position).getRating();
            long totalRatingsNo = wishlistModelList.get(position).getTotalRating();
            String productPriceValue = wishlistModelList.get(position).getProductPrice();
            String cuttedPriceValue = wishlistModelList.get(position).getCuttedPrice();
            boolean paymentMethod = wishlistModelList.get(position).isCOD();
            boolean isStock=wishlistModelList.get(position).isInStock();

            setData(productId, resource, title, freeCoupensNo, averageRate, totalRatingsNo, productPriceValue, cuttedPriceValue, paymentMethod, position,isStock);

            if (lastPosition < position) {
                Animation animation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.fade_in);
                itemView.setAnimation(animation);
                lastPosition = position;
            }
        }

        private void setData(String productId, String resource, String title, long freeCoupensNo, String averageRate, long totalRatingsNo, String price, String cuttedPriceValue, boolean COD, int index,boolean isStock) {
            Glide.with(itemView.getContext())
                    .load(resource)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.img))
                    .into(productImage);

            productTitle.setText(title);
            if (freeCoupensNo != 0 && isStock) {
                coupenIcon.setVisibility(View.VISIBLE);
                if (freeCoupensNo == 1) {
                    freeCoupens.setText("free " + freeCoupensNo + " coupon");
                } else {
                    freeCoupens.setText("free " + freeCoupensNo + " coupons");
                }

            } else {
                coupenIcon.setVisibility(View.INVISIBLE);
                freeCoupens.setVisibility(View.VISIBLE);
            }
            LinearLayout linearLayout=(LinearLayout) rating.getParent();
            if(isStock) {
                paymentmethod.setVisibility(View.VISIBLE);
                rating.setVisibility(View.VISIBLE);
                totalRatings.setVisibility(View.VISIBLE);
                productPrice.setTextColor(Color.parseColor("#000000"));
                cuttedPrice.setVisibility(View.VISIBLE);
                rating.setText(averageRate);
                totalRatings.setText("(" + totalRatingsNo + ")ratings");
                productPrice.setText("Rs." + price + "/-");
                cuttedPrice.setText("Rs." + cuttedPriceValue + "/-");
                if (COD) {
                    paymentmethod.setVisibility(View.VISIBLE);
                } else {
                    paymentmethod.setVisibility(View.INVISIBLE);
                }
                if (wishlist) {
                    deleteBtn.setVisibility(View.VISIBLE);
                } else {
                    deleteBtn.setVisibility(View.GONE);
                }
            }else{

                linearLayout.setVisibility(View.INVISIBLE);
                rating.setVisibility(View.INVISIBLE);
                totalRatings.setVisibility(View.INVISIBLE);
                productPrice.setText("Out of stock");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.btnRed));
                cuttedPrice.setVisibility(View.INVISIBLE);
                paymentmethod.setVisibility(View.INVISIBLE);
            }




            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!ProductDetailsActivity.running_wishlist_query) {
                        ProductDetailsActivity.running_wishlist_query = true;
                        DBqueries.removeFromWishList(index, itemView.getContext());
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(fromSearch){
                     ProductDetailsActivity.fromSearch=true;
                    }
                    Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                    productDetailsIntent.putExtra("PRODUCT_ID", productId);
                    itemView.getContext().startActivity(productDetailsIntent);
                }
            });
        }
    }


    public List<WishlistModel> getWishlistModelList() {
        return wishlistModelList;
    }

    public void setWishlistModelList(List<WishlistModel> wishlistModelList) {
        this.wishlistModelList = wishlistModelList;
    }
}
