package com.example.splashactivity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {
    public static boolean running_wishlist_query = false;
    public static boolean running_rating_query = false;
    public static boolean running_cart_query = false;

    public static boolean fromSearch=false;


    private ViewPager productImagesViewPager;
    private TextView productTitle, averageRatingMiniView, totalRatingMiniView;
    private TextView productPrice;
    private String productOriginalPrice;
    private TextView cuttedPrice;
    private ImageView codIndicator;
    private TextView tvCodIndicator;

    private TabLayout viewpagerIndicator;
    private LinearLayout coupenRedemptionLayout;
    private Button coupenRedeemBtn;
    private TextView rewardTitle;
    private TextView rewardBody;


    //product description
    private ConstraintLayout productDetailsOnlyContainer;
    private List<ProductSpecificationModel> productSpecificationModelList = new ArrayList<>();
    private ConstraintLayout productDetailsTabsContainer;
    private ViewPager productDetailsViewPager;
    private TabLayout productDetailsTablayout;
    private TextView productOnlyDescripotionBody;
    private String productDescription;
    private String productOtherDetails;
    private int tabPosition = -1;
    //product description
    public static FloatingActionButton addToWishListBtn;

    /////rating layout
    public static int initialRating;
    public static LinearLayout ratenowcontainer;
    private TextView totalRatings;
    private LinearLayout ratingsNumberContainer;
    private TextView totalRatingsFigure;
    private LinearLayout ratingsProgressBarContainer;
    private TextView averageRating;
    ////rating layout

    private Button buyNowBtn;
    private LinearLayout addToCartButton;
    public static MenuItem cartItem;


    public static boolean ALREADY_ADDED_TO_WISHLIST = false;
    public static boolean ALREADY_ADDED_TO_CART = false;

    private FirebaseFirestore firebaseFirestore;
    List<String> productImages = new ArrayList<>();
    //coupendialog
    private TextView coupenTitle;
    private TextView coupenExpiryDate;
    private TextView coupenBody;
    private RecyclerView coupensRecyclerView;
    private LinearLayout selectedCoupen;
    private TextView discountedPrice;
    private TextView originalPrice;

    //coupendialog
    private Dialog signInDialog;
    private Dialog loadingDialog;
    private FirebaseUser currentUser;
    public static String productID;
    private DocumentSnapshot documentSnapshot;
    private TextView badgeCount;
    private boolean inStock=false;

    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        productImagesViewPager = findViewById(R.id.product_images_viewpager);
        viewpagerIndicator = (TabLayout) findViewById(R.id.viewpager_indicator);
        productDetailsViewPager = findViewById(R.id.product_details_viewpager);
        productDetailsTablayout = findViewById(R.id.product_details_tablayout);
        buyNowBtn = findViewById(R.id.buy_now_btn);
        coupenRedeemBtn = findViewById(R.id.coupen_redeemption_btn);


        productTitle = findViewById(R.id.product_title);
        averageRatingMiniView = findViewById(R.id.tv_product_rating_miniview);
        totalRatingMiniView = findViewById(R.id.total_ratings_miniview);
        productPrice = findViewById(R.id.product_price);
        cuttedPrice = findViewById(R.id.cutted_price);
        tvCodIndicator = findViewById(R.id.cod_textView);
        codIndicator = findViewById(R.id.tv_cod_indicator);
        rewardTitle = findViewById(R.id.reward_title);
        rewardBody = findViewById(R.id.reward_body);
        productDetailsTabsContainer = findViewById(R.id.product_details_tabs_containers);
        productDetailsOnlyContainer = findViewById(R.id.product_details_container);
        productOnlyDescripotionBody = findViewById(R.id.product_details_body);
        totalRatings = findViewById(R.id.total_ratings);
        ratingsNumberContainer = findViewById(R.id.ratings_numbers_container);
        totalRatingsFigure = findViewById(R.id.totalratingsfigure);
        ratingsProgressBarContainer = findViewById(R.id.ratingsg_proessbar_container);
        averageRating = findViewById(R.id.average_rating);
        addToCartButton = findViewById(R.id.add_to_cart_btn);
        coupenRedemptionLayout = findViewById(R.id.coupon_redemption_layout);
        initialRating = -1;
        //loading dialog
        loadingDialog = new Dialog(ProductDetailsActivity.this);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        //loading dialog


        //coupen dialog
        final Dialog checkCoupenPriceDialog = new Dialog(ProductDetailsActivity.this);
        checkCoupenPriceDialog.setContentView(R.layout.coupen_redeem_dialog);
        checkCoupenPriceDialog.setCancelable(true);
        checkCoupenPriceDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageView toggleRecyclerView = checkCoupenPriceDialog.findViewById(R.id.toggle_recyclerview);
        coupensRecyclerView = checkCoupenPriceDialog.findViewById(R.id.coupens_recyclerview);
        selectedCoupen = checkCoupenPriceDialog.findViewById(R.id.selected_coupen);
        coupenTitle = checkCoupenPriceDialog.findViewById(R.id.coupen_title);
        coupenExpiryDate = checkCoupenPriceDialog.findViewById(R.id.coupen_validity);
        coupenBody = checkCoupenPriceDialog.findViewById(R.id.coupen_body);


        originalPrice = checkCoupenPriceDialog.findViewById(R.id.original_price);
        discountedPrice = checkCoupenPriceDialog.findViewById(R.id.discounted_price);



        LinearLayoutManager layoutManager = new LinearLayoutManager(ProductDetailsActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        coupensRecyclerView.setLayoutManager(layoutManager);


        toggleRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogRecyclerView();
            }
        });

        //coupen dialog


        addToWishListBtn = findViewById(R.id.add_to_wishlist_btn);
        firebaseFirestore = FirebaseFirestore.getInstance();


        productID = getIntent().getStringExtra("PRODUCT_ID");
        firebaseFirestore.collection("PRODUCTS").document(productID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            documentSnapshot = task.getResult();


                            firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful())
                                    {

                                        for (long x = 1; x < (long) documentSnapshot.get("no_of_product_images") + 1; x++) {
                                            productImages.add(documentSnapshot.get("product_image_" + x).toString());
                                        }
                                        ProductImagesAdapter productImagesAdapter = new ProductImagesAdapter(productImages);
                                        productImagesViewPager.setAdapter(productImagesAdapter);

                                        productTitle.setText(documentSnapshot.get("product_title").toString());
                                        averageRatingMiniView.setText(documentSnapshot.get("average_rating").toString());
                                        totalRatingMiniView.setText("(" + (long) documentSnapshot.get("total_ratings") + ")ratings");
                                        productPrice.setText("Rs." + documentSnapshot.get("product_price").toString() + "/-");
                                        ///for coupen dialog
                                        originalPrice.setText(productPrice.getText());
                                        productOriginalPrice=documentSnapshot.get("product_price").toString();
                                        MyRewardsAdapter myRewardsAdapter = new MyRewardsAdapter(DBqueries.rewardModelList, true,coupensRecyclerView,selectedCoupen,productOriginalPrice,coupenTitle,coupenExpiryDate,coupenBody,discountedPrice);
                                        coupensRecyclerView.setAdapter(myRewardsAdapter);
                                        myRewardsAdapter.notifyDataSetChanged();
                                        ///for coupen dialog



                                        cuttedPrice.setText("Rs." + documentSnapshot.get("cutted_price").toString() + "/-");
                                        if ((boolean) documentSnapshot.get("COD")) {
                                            codIndicator.setVisibility(View.VISIBLE);
                                            tvCodIndicator.setVisibility(View.VISIBLE);
                                        } else {
                                            codIndicator.setVisibility(View.INVISIBLE);
                                            tvCodIndicator.setVisibility(View.INVISIBLE);
                                        }

                                        rewardTitle.setText((long) documentSnapshot.get("free_coupens") + documentSnapshot.get("free_coupen_title").toString());
                                        rewardBody.setText(documentSnapshot.get("free_coupen_body").toString());


                                        if ((boolean) documentSnapshot.get("use_tab_layout")) {
                                            productDetailsTabsContainer.setVisibility(View.VISIBLE);
                                            productDetailsOnlyContainer.setVisibility(View.GONE);
                                            productDescription = documentSnapshot.get("product_description").toString();
                                            productOtherDetails = documentSnapshot.get("product_other_details").toString();

                                            for (long x = 1; x < (long) documentSnapshot.get("total_spec_titles") + 1; x++) {
                                                productSpecificationModelList.add(new ProductSpecificationModel(0, documentSnapshot.get("spec_title_" + x).toString()));
                                                for (long y = 1; y < (long) documentSnapshot.get("spec_title_" + x + "_total_fields") + 1; y++) {
                                                    productSpecificationModelList.add(
                                                            new ProductSpecificationModel(
                                                                    1,
                                                                    documentSnapshot.get("spec_title_" + x + "_field_" + y + "_name").toString(),
                                                                    documentSnapshot.get("spec_title_" + x + "_field_" + y + "_value").toString()
                                                            )
                                                    );
                                                }
                                            }
                                        } else {
                                            productDetailsTabsContainer.setVisibility(View.GONE);
                                            productDetailsOnlyContainer.setVisibility(View.VISIBLE);
                                            productOnlyDescripotionBody.setText(documentSnapshot.get("product_description").toString());
                                        }


                                        totalRatings.setText((long) documentSnapshot.get("total_ratings") + " ratings");
                                        for (int x = 0; x < 5; x++) {
                                            TextView rating = (TextView) ratingsNumberContainer.getChildAt(x);
                                            rating.setText(String.valueOf((long) documentSnapshot.get((5 - x) + "_star")));

                                            ProgressBar progressBar = (ProgressBar) ratingsProgressBarContainer.getChildAt(x);
                                            int maxProgress = Integer.parseInt(String.valueOf((long) documentSnapshot.get("total_ratings")));
                                            progressBar.setMax(maxProgress);
                                            progressBar.setProgress(Integer.parseInt(String.valueOf((long) documentSnapshot.get((5 - x) + "_star"))));
                                        }
                                        totalRatingsFigure.setText(String.valueOf((long) documentSnapshot.get("total_ratings")));
                                        averageRating.setText(documentSnapshot.get("average_rating").toString());

                                        productDetailsViewPager.setAdapter(new ProductDetailsAdapter(getSupportFragmentManager(), productDetailsTablayout.getTabCount(), productDescription, productOtherDetails, productSpecificationModelList));
                                        if (currentUser != null) {
                                            if (DBqueries.myRating.size() == 0) {
                                                DBqueries.loadRatingList(ProductDetailsActivity.this);
                                            }
                                            if (DBqueries.cartList.size() == 0) {
                                                DBqueries.loadCartList(ProductDetailsActivity.this, loadingDialog, false,badgeCount,new TextView(ProductDetailsActivity.this));
                                            }
                                            if (DBqueries.wishList.size() == 0) {
                                                DBqueries.loadWishList(ProductDetailsActivity.this, loadingDialog, false);
                                            }
                                            if (DBqueries.rewardModelList.size()==0){
                                                DBqueries.loadRewards(ProductDetailsActivity.this,loadingDialog,false);
                                            }
                                            if (DBqueries.cartList.size() != 0 && DBqueries.wishList.size() != 0 && DBqueries.rewardModelList.size()!=0){
                                                loadingDialog.dismiss();
                                            }

                                        } else {
                                            loadingDialog.dismiss();
                                        }


                                        if (DBqueries.myRatedIds.contains(productID)) {
                                            int index = DBqueries.myRatedIds.indexOf(productID);
                                            initialRating = Integer.parseInt(String.valueOf(DBqueries.myRating.get(index))) - 1;
                                            setRating(initialRating);
                                        }
                                        if (DBqueries.cartList.contains(productID)) {
                                            ALREADY_ADDED_TO_CART = true;

                                        } else {
                                            ALREADY_ADDED_TO_CART = false;
                                        }


                                        if (DBqueries.wishList.contains(productID)) {
                                            ALREADY_ADDED_TO_WISHLIST = true;
                                            addToWishListBtn.setSupportImageTintList(getResources().getColorStateList(R.color.btnRed));

                                        } else {
                                            addToWishListBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                                            ALREADY_ADDED_TO_WISHLIST = false;
                                        }






                                        if(task.getResult().getDocuments().size()<(long)documentSnapshot.get("stock_quantity"))
                                        {
                                            inStock=true;
                                            buyNowBtn.setVisibility(View.VISIBLE);
                                            addToCartButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    if (currentUser == null) {
                                                        signInDialog.show();
                                                    } else {
                                                        if (!running_cart_query) {
                                                            running_cart_query = true;

                                                            if (ALREADY_ADDED_TO_CART) {
                                                                running_cart_query = false;
                                                                Toast.makeText(ProductDetailsActivity.this, "Already added to cart!", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Map<String, Object> addProduct = new HashMap<>();
                                                                addProduct.put("product_ID_" + String.valueOf(DBqueries.cartList.size()), productID);
                                                                addProduct.put("list_size", (long) DBqueries.cartList.size() + 1);

                                                                firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_CART")
                                                                        .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    if (DBqueries.cartItemModelList.size() != 0) {
                                                                                        DBqueries.cartItemModelList.add(0,new CartItemModel(documentSnapshot.getBoolean("COD"),CartItemModel.CART_ITEM, productID, documentSnapshot.get("product_image_1").toString()
                                                                                                , documentSnapshot.get("product_title").toString()
                                                                                                , (long) documentSnapshot.get("free_coupens")
                                                                                                , documentSnapshot.get("product_price").toString()
                                                                                                , documentSnapshot.get("cutted_price").toString()
                                                                                                , (long) 1
                                                                                                , (long) 0
                                                                                                , (long) 0
                                                                                                ,inStock
                                                                                                ,(long)documentSnapshot.get("max_quantity")
                                                                                                ,(long)documentSnapshot.get("stock_quantity")));
                                                                                    }

                                                                                    ALREADY_ADDED_TO_CART = true;

                                                                                    DBqueries.cartList.add(productID);
                                                                                    Toast.makeText(ProductDetailsActivity.this, "Added to Cart successfully!", Toast.LENGTH_SHORT).show();
                                                                                    invalidateOptionsMenu();
                                                                                    running_cart_query = false;
                                                                                } else {

                                                                                    running_cart_query = false;
                                                                                    String error = task.getException().getMessage();
                                                                                    Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });


                                                            }
                                                        }
                                                    }
                                                }
                                            });


                                        }else {
                                            inStock=false;
                                            buyNowBtn.setVisibility(View.GONE);

                                            TextView outOfStock = (TextView) addToCartButton.getChildAt(0);
                                            outOfStock.setText("Out of Stock");
                                            outOfStock.setTextColor(getResources().getColor(com.google.android.material.R.color.design_default_color_primary));
                                            outOfStock.setCompoundDrawables(null,null,null,null);
                                        }
                                    }
                                    else {
                                        String error=task.getException().getMessage();
                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        } else {
                            loadingDialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        viewpagerIndicator.setupWithViewPager(productImagesViewPager, true);


        addToWishListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser == null) {
                    signInDialog.show();
                } else {


                    if (!running_wishlist_query) {
                        running_wishlist_query = true;

                        if (ALREADY_ADDED_TO_WISHLIST) {
                            int index = DBqueries.wishList.indexOf(productID);
                            DBqueries.removeFromWishList(index, ProductDetailsActivity.this);
                            addToWishListBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                        } else {
                            addToWishListBtn.setSupportImageTintList(getResources().getColorStateList(R.color.btnRed));
                            Map<String, Object> addProduct = new HashMap<>();
                            addProduct.put("product_ID_" + String.valueOf(DBqueries.wishList.size()), productID);
                            addProduct.put("list_size", (long) DBqueries.wishList.size() + 1);

                            firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_WISHLIST")
                                    .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                if (DBqueries.wishlistModelList.size() != 0) {
                                                    DBqueries.wishlistModelList.add(new WishlistModel(productID, (String) documentSnapshot.get("product_image_1").toString()
                                                            , (String) documentSnapshot.get("product_title")
                                                            , (long) documentSnapshot.get("free_coupens")
                                                            , documentSnapshot.get("average_rating").toString()
                                                            , (long) documentSnapshot.get("total_ratings")
                                                            , documentSnapshot.get("product_price").toString()
                                                            , documentSnapshot.get("cutted_price").toString()
                                                            , (boolean) documentSnapshot.get("COD")
                                                    ,inStock));
                                                }

                                                ALREADY_ADDED_TO_WISHLIST = true;
                                                addToWishListBtn.setSupportImageTintList(getResources().getColorStateList(R.color.btnRed));
                                                DBqueries.wishList.add(productID);
                                                Toast.makeText(ProductDetailsActivity.this, "Added to Wishlist successfully!", Toast.LENGTH_SHORT).show();

                                            } else {
                                                addToWishListBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));


                                                String error = task.getException().getMessage();
                                                Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                            }
                                            running_wishlist_query = false;
                                        }
                                    });
                        }
                    }
                }
            }
        });

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser == null) {
                    signInDialog.show();
                } else {
                    ///todo:add to cart
                }
            }
        });


        productDetailsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDetailsTablayout));
        productDetailsTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabPosition = tab.getPosition();
                productDetailsViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ///rating layout

        ratenowcontainer = findViewById(R.id.ratenow);
        for (int x = 0; x < ratenowcontainer.getChildCount(); x++) {
            final int starposition = x;
            ratenowcontainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentUser == null) {
                        signInDialog.show();
                    } else {
                        if (starposition != initialRating) {
                            if (!running_rating_query) {
                                running_rating_query = true;
                                setRating(starposition);
                                Map<String, Object> updateRating = new HashMap<>();

                                if (DBqueries.myRatedIds.contains(productID)) {

                                    TextView oldRating = (TextView) ratingsNumberContainer.getChildAt(5 - initialRating - 1);
                                    TextView finalRating = (TextView) ratingsNumberContainer.getChildAt(5 - starposition - 1);


                                    updateRating.put(initialRating + 1 + "_star", Long.parseLong(oldRating.getText().toString()) - 1);
                                    updateRating.put(starposition + 1 + "_star", Long.parseLong(finalRating.getText().toString()) + 1);
                                    updateRating.put("average_rating", calculateAverageRating((long) starposition - initialRating, true));
                                } else {
//                                Map<String, Object> rating = new HashMap<>();
//                                rating.put("product_ID_" + DBqueries.myRatedIds.size(), productID);
//                                rating.put("rating_" + DBqueries.myRatedIds.size(), (long) starposition + 1);

                                    updateRating.put(starposition + 1 + "_star", (long) documentSnapshot.get(starposition + 1 + "_star") + 1);
                                    updateRating.put("average_rating", calculateAverageRating((long) starposition + 1, false));
                                    updateRating.put("total_ratings", (long) documentSnapshot.get("total_ratings") + 1);

                                }
                                firebaseFirestore.collection("PRODUCTS").document(productID).update(updateRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Map<String, Object> myRating = new HashMap<>();

                                            if (DBqueries.myRatedIds.contains(productID)) {

                                                myRating.put("rating_" + DBqueries.myRatedIds.indexOf(productID), (long) starposition + 1);


                                            } else {
                                                myRating.put("list_size", (long) DBqueries.myRatedIds.size() + 1);
                                                myRating.put("product_ID_" + DBqueries.myRatedIds.size(), productID);
                                                myRating.put("rating_" + DBqueries.myRatedIds.size(), (long) starposition + 1);

                                            }

                                            firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_RATINGS").update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        if (DBqueries.myRatedIds.contains(productID)) {
                                                            DBqueries.myRating.set(DBqueries.myRatedIds.indexOf(productID), (long) starposition + 1);

                                                            TextView oldRating = (TextView) ratingsNumberContainer.getChildAt(5 - initialRating - 1);
                                                            TextView finalRating = (TextView) ratingsNumberContainer.getChildAt(5 - starposition - 1);


                                                            oldRating.setText(String.valueOf(Integer.parseInt(oldRating.getText().toString()) - 1));
                                                            finalRating.setText(String.valueOf(Integer.parseInt(finalRating.getText().toString()) + 1));


                                                        } else {
                                                            DBqueries.myRatedIds.add(productID);
                                                            DBqueries.myRating.add((long) starposition + 1);

                                                            TextView rating = (TextView) ratingsNumberContainer.getChildAt(5 - starposition - 1);
                                                            rating.setText(String.valueOf(Integer.parseInt(rating.getText().toString()) + 1));

                                                            totalRatingMiniView.setText("(" + ((long) documentSnapshot.get("total_ratings") + 1) + ")ratings");
                                                            totalRatings.setText((long) documentSnapshot.get("total_ratings") + 1 + " ratings");
                                                            totalRatingsFigure.setText(String.valueOf((long) documentSnapshot.get("total_ratings") + 1));

                                                            Toast.makeText(ProductDetailsActivity.this, "Thank You! For Rating...", Toast.LENGTH_SHORT).show();
                                                        }

                                                        for (int x = 0; x < 5; x++) {
                                                            TextView ratingfigures = (TextView) ratingsNumberContainer.getChildAt(x);
                                                            ProgressBar progressBar = (ProgressBar) ratingsProgressBarContainer.getChildAt(x);

                                                            int maxProgress = Integer.parseInt(totalRatingsFigure.getText().toString());
                                                            progressBar.setMax(maxProgress);

                                                            progressBar.setProgress(Integer.parseInt(ratingfigures.getText().toString()));
                                                        }
                                                        initialRating = starposition;
                                                        averageRating.setText(calculateAverageRating(0, true));
                                                        averageRatingMiniView.setText(calculateAverageRating(0, true));

                                                        if (DBqueries.wishList.contains(productID) && DBqueries.wishlistModelList.size() != 0) {

                                                            int index = DBqueries.wishList.indexOf(productID);
                                                            DBqueries.wishlistModelList.get(index).setRating(averageRating.getText().toString());
                                                            DBqueries.wishlistModelList.get(index).setTotalRating(Long.parseLong(totalRatingsFigure.getText().toString()));

                                                        }
                                                    } else {
                                                        setRating(initialRating);
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                    running_rating_query = false;
                                                }
                                            });
                                        } else {
                                            running_rating_query = false;
                                            setRating(initialRating);
                                            String error = task.getException().getMessage();
                                            Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }
                    }
                }
            });
        }
        //rating layout

        buyNowBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                loadingDialog.show();
                if (currentUser == null) {
                    signInDialog.show();
                } else {
                    DeliveryActivity.cartItemModelList = new ArrayList<>();
                    DeliveryActivity.cartItemModelList.add(new CartItemModel(documentSnapshot.getBoolean("COD"),CartItemModel.CART_ITEM, productID, documentSnapshot.get("product_image_1").toString()
                            , documentSnapshot.get("product_title").toString()
                            , (long) documentSnapshot.get("free_coupens")
                            , documentSnapshot.get("product_price").toString()
                            , documentSnapshot.get("cutted_price").toString()
                            , (long) 1
                            , (long) documentSnapshot.get("offers_applied")
                            , (long) 0
                            ,inStock
                    ,(long) documentSnapshot.get("max_quantity")
                            ,(long) documentSnapshot.get("stock_quantity")));
                    DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));

                    if (DBqueries.addressesModelList.size()==0) {
                        DBqueries.loadAddresses(ProductDetailsActivity.this, loadingDialog,true);
                    }else{
                        loadingDialog.dismiss();

                        Intent deliveryIntent = new Intent(ProductDetailsActivity.this, DeliveryActivity.class);
                        startActivity(deliveryIntent);
                    }
                }
            }
        });
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser == null) {
                    signInDialog.show();
                } else {
                    DeliveryActivity.fromCart=true;
                    if (!running_cart_query) {
                        running_cart_query = true;

                        if (ALREADY_ADDED_TO_CART) {
                            running_cart_query = false;
                            Toast.makeText(ProductDetailsActivity.this, "Already added to cart!", Toast.LENGTH_SHORT).show();
                        } else {
                            Map<String, Object> addProduct = new HashMap<>();
                            addProduct.put("product_ID_" + String.valueOf(DBqueries.cartList.size()), productID);
                            addProduct.put("list_size", (long) DBqueries.cartList.size() + 1);

                            firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_CART")
                                    .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                if (DBqueries.cartItemModelList.size() != 0) {
                                                    DBqueries.cartItemModelList.add(new CartItemModel(documentSnapshot.getBoolean("COD"),CartItemModel.CART_ITEM, productID, documentSnapshot.get("product_image_1").toString()
                                                            , documentSnapshot.get("product_title").toString()
                                                            , (long) documentSnapshot.get("free_coupens")
                                                            , documentSnapshot.get("product_price").toString()
                                                            , documentSnapshot.get("cutted_price").toString()
                                                            , (long) 1
                                                            , (long) documentSnapshot.get("offers_applied")
                                                            , (long) 0
                                                    ,(boolean)documentSnapshot.get("in_stock")
                                                    ,(long) documentSnapshot.get("max_quantity")
                                                            ,(long) documentSnapshot.get("stock_quantity")));
                                                }

                                                ALREADY_ADDED_TO_CART = true;

                                                DBqueries.cartList.add(productID);
                                                Toast.makeText(ProductDetailsActivity.this, "Added to Cart successfully!", Toast.LENGTH_SHORT).show();
                                                invalidateOptionsMenu();
                                                running_cart_query = false;
                                            } else {

                                                running_cart_query = false;
                                                String error = task.getException().getMessage();
                                                Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                        }
                    }
                }
            }
        });


        coupenRedeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCoupenPriceDialog.show();
            }
        });


        //sign in dialog
        signInDialog = new Dialog(ProductDetailsActivity.this);
        signInDialog.setCancelable(true);
        signInDialog.setContentView(R.layout.sign_in_dialog);


        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button dialogSignInButton = signInDialog.findViewById(R.id.sign_inbutton);
        Button dialogSignUnButton = signInDialog.findViewById(R.id.sign_upbtn);

        Intent registerIntent = new Intent(ProductDetailsActivity.this, RegisterActivity.class);

        dialogSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SigninFragment.disableCloseButton = true;
                SignupFragment.disableCloseButton = true;
                signInDialog.dismiss();
                RegisterActivity.setSignUpFragment = false;
                startActivity(registerIntent);
            }
        });

        dialogSignUnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SigninFragment.disableCloseButton = true;
                SignupFragment.disableCloseButton = true;
                signInDialog.dismiss();
                RegisterActivity.setSignUpFragment = true;
                startActivity(registerIntent);
            }
        });

        //sign in dialog


    }

    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            coupenRedemptionLayout.setVisibility(View.GONE);
        } else {
            coupenRedemptionLayout.setVisibility(View.VISIBLE);
        }
        if (currentUser != null) {
            if (DBqueries.myRating.size() == 0) {
                DBqueries.loadRatingList(ProductDetailsActivity.this);
            }
            if (DBqueries.wishList.size() == 0) {
                DBqueries.loadWishList(ProductDetailsActivity.this, loadingDialog, false);
            }
            if (DBqueries.rewardModelList.size()==0){
                DBqueries.loadRewards(ProductDetailsActivity.this,loadingDialog,false);
            }
            if (DBqueries.cartList.size() != 0 && DBqueries.wishList.size() != 0 && DBqueries.rewardModelList.size()!=0){
                loadingDialog.dismiss();
            }

        } else {
            loadingDialog.dismiss();
        }

        if (DBqueries.myRatedIds.contains(productID)) {
            int index = DBqueries.myRatedIds.indexOf(productID);
            initialRating = Integer.parseInt(String.valueOf(DBqueries.myRating.get(index))) - 1;
            setRating(initialRating);
        }
        if (DBqueries.cartList.contains(productID)) {
            ALREADY_ADDED_TO_CART = true;

        } else {
            ALREADY_ADDED_TO_CART = false;
        }

        if (DBqueries.wishList.contains(productID)) {
            ALREADY_ADDED_TO_WISHLIST = true;
            addToWishListBtn.setSupportImageTintList(getResources().getColorStateList(R.color.btnRed));

        } else {
            addToWishListBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
            ALREADY_ADDED_TO_WISHLIST = false;
        }
        invalidateOptionsMenu();
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

    public static void setRating(int starposition) {
        for (int x = 0; x < ratenowcontainer.getChildCount(); x++) {
            ImageView starBtn = (ImageView) ratenowcontainer.getChildAt(x);
            starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));
            if (x <= starposition) {
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffbb00")));
            }
        }
    }

    private String calculateAverageRating(long currentUserRating, boolean update) {
        double totalStars = Double.valueOf(0);
        for (int x = 1; x < 6; x++) {
            TextView ratingNo = (TextView) ratingsNumberContainer.getChildAt(5 - x);
            totalStars = totalStars + (Long.parseLong(ratingNo.getText().toString()) * x);
        }
        totalStars = totalStars + currentUserRating;
        if (update) {
            return String.valueOf(totalStars / Long.parseLong(totalRatingsFigure.getText().toString())).substring(0, 3);

        } else {
            return String.valueOf(totalStars / (Long.parseLong(totalRatingsFigure.getText().toString()) + 1)).substring(0, 3);


        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_and_cart_icon, menu);

        cartItem = menu.findItem(R.id.main_cart_icon);

            cartItem.setActionView(R.layout.badge_layout);
            ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
            badgeIcon.setImageResource(R.drawable.cart_white1);
            badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);

            if(currentUser!=null){
                if (DBqueries.cartList.size() == 0) {
                    DBqueries.loadCartList(ProductDetailsActivity.this, loadingDialog, false,badgeCount,new TextView(ProductDetailsActivity.this));
                }else{
                    badgeCount.setVisibility(View.VISIBLE);
                    if(DBqueries.cartList.size()<99) {
                        badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
                    }else{
                        badgeCount.setText("99");
                    }
                }
            }
            cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentUser == null) {
                        signInDialog.show();
                        ;
                    } else {
                        Intent cartIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                        MainActivity.showCart = true;
                        startActivity(cartIntent);
                    }
                }
            });
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.main_search_icon) {
            if(fromSearch){

            }else {
                Intent intent = new Intent(this, SearchAcitivy.class);
                startActivity(intent);
            }
            return true;
        } else if (id == R.id.main_cart_icon) {  //todo:cart
            if (currentUser == null) {
                signInDialog.show();
            } else {
                Intent cartIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                MainActivity.showCart = true;
                startActivity(cartIntent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fromSearch=false;
    }
}