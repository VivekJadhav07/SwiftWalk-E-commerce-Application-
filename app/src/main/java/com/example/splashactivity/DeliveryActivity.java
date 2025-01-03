package com.example.splashactivity;

import static com.example.splashactivity.DBqueries.firebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.UUID;

public class DeliveryActivity extends AppCompatActivity implements PaymentResultListener {


    private RecyclerView deliveryRecyclerview;
    public static List<CartItemModel> cartItemModelList;
    public static CardAdapter cartAdapter;
    private Button changeOrAddNewAddressBtn;
    public static int SELECT_ADDRESS = 0;
    private TextView totalAmount;
    private TextView fullName;
    private String name;
    public static String mobileno;
    private TextView fullAddress;
    private TextView pincode;
    private Button continueBtn;
    public  static  Dialog loadingDialog;
    private  Dialog paymentMethodDialog;
    private  TextView codTitle;
    private View divider;
    private ImageButton paytm, cod;
    private String paymentMethod ="PAYTM";
    private ConstraintLayout confirmation_Layout;
    private ImageButton continue_shopping_button;
    private TextView order_Id;
    private String order_id;

    private boolean successResponse = false;
    public static String str;
    public static boolean fromCart;
    public static boolean codOrderConconfirmed=false;
    private  FirebaseFirestore firebaseFirestore;
    public String str2;

   public static boolean getQtyIDs=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Delivery");


        deliveryRecyclerview = findViewById(R.id.delivery_recyclerview);
        changeOrAddNewAddressBtn = findViewById(R.id.change_or_add_address_btn);
        totalAmount = findViewById(R.id.total_cart_amount);

        fullName = findViewById(R.id.fullname);
        fullAddress = findViewById(R.id.address);
        pincode = findViewById(R.id.pincode);
        continueBtn = findViewById(R.id.cart_continue_btn);

        ///  confirmation_Layout=findViewById(R.id.order_confirmation_layout);
        continue_shopping_button = findViewById(R.id.continue_shopping_button);
        order_Id = findViewById(R.id.order_id);

        String rando = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);

        order_id= rando;
        //loading dialog
        loadingDialog = new Dialog(DeliveryActivity.this);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(DeliveryActivity.this.getDrawable(R.drawable.slider_background));
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //loading dialog

        //payment dialog
        paymentMethodDialog = new Dialog(DeliveryActivity.this);
        paymentMethodDialog.setCancelable(true);
        paymentMethodDialog.getWindow().setBackgroundDrawable(DeliveryActivity.this.getDrawable(R.drawable.slider_background));
        paymentMethodDialog.setContentView(R.layout.payment_method);
        paymentMethodDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paytm = paymentMethodDialog.findViewById(R.id.paytm);
        cod = paymentMethodDialog.findViewById(R.id.cod_btn);
        codTitle=paymentMethodDialog.findViewById(R.id.cod_btn_title);
        divider=paymentMethodDialog.findViewById(R.id.divideredit);

        //payment dialog

        firebaseFirestore=FirebaseFirestore.getInstance();
        getQtyIDs=true;

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        deliveryRecyclerview.setLayoutManager(layoutManager);

         cartAdapter = new CardAdapter(cartItemModelList, totalAmount, false);
        deliveryRecyclerview.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        changeOrAddNewAddressBtn.setVisibility(View.VISIBLE);
        changeOrAddNewAddressBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getQtyIDs=false;
                Intent myAddressesIntent = new Intent(DeliveryActivity.this, My_AddressesActivity.class);
                myAddressesIntent.putExtra("MODE", SELECT_ADDRESS);
                startActivity(myAddressesIntent);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override            public void onClick(View view) {
              boolean allproductsAvaible=true;
              for(CartItemModel cartItemModel:cartItemModelList) {
                  if (cartItemModel.isQtyError()) {
                      allproductsAvaible = false;
                      break;
                  }
                  if (cartItemModel.getType() == CartItemModel.CART_ITEM){
                      if (!cartItemModel.isCOD()) {
                          cod.setEnabled(false);
                          cod.setAlpha(0.5f);
                          codTitle.setAlpha(0.5f);
                       
                          break;
                      } else {
                          cod.setEnabled(true);
                          cod.setAlpha(1f);
                          codTitle.setAlpha(1f);
                          divider.setVisibility(View.VISIBLE);
                      }
              }
              }
              if(allproductsAvaible){
                  paymentMethodDialog.show();
              }

            }
        });

        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               paymentMethod="COD";
               placeOrderDetails();
            }
        });
        paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentMethod="PAYTM";
                placeOrderDetails();
            }
        });
    }

    private void makepayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_6w6PqYZuSESZE8");


        checkout.setImage(R.drawable.logo);
        final Activity activity = this;

        try {
            JSONObject options = new JSONObject();

            options.put("name", "SWIFTWALK");
            options.put("description", "Reference No. #123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            // options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            // Get the text from totalAmount TextView
            // Get the text from totalAmount TextView
            String amountTextOriginal = totalAmount.getText().toString();
// Create a copy of the original string
            String amountTextCopy = amountTextOriginal;

// Remove non-numeric characters from the copied string
            String numericAmount = amountTextCopy.replaceAll("[^0-9]", "");

// Parse the numeric string to an integer
            int amount = Integer.parseInt(numericAmount);


            // Multiply by 100
            options.put("amount", String.valueOf(amount * 100));

            //options.put("amount",String.valueOf(Integer.parseInt((String) totalAmount.getText())*100));//300 X 100
            options.put("prefill.email", "sushilrahatole@gmail.com");
            options.put("prefill.contact", "9309289200");
            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }

    }

    public void onPaymentSuccess(String s) {
        str = s;
        str2=str;
//        Intent i=new Intent(DeliveryActivity.this,PaymentSuccessfull.class);
//        startActivity(i);
            Map<String,Object>updatestatus=new HashMap<>();
         updatestatus.put("Payment Status","Paid");
         updatestatus.put("Order Status","Ordered");
            firebaseFirestore.collection("ORDERS").document(String.valueOf(order_id)).update(updatestatus)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                             if(task.isSuccessful()){
                                 Map <String,Object> userOrder=new HashMap<>();
                                 userOrder.put("order_id",String.valueOf(order_id));
                                 userOrder.put("time",FieldValue.serverTimestamp());
                                 firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").document(String.valueOf(order_id)).set(userOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                                     @Override
                                     public void onComplete(@NonNull Task<Void> task) {
                                         if(task.isSuccessful())
                                         {
                                             showConfirmationLayout();

                                         }else {
                                             Toast.makeText(DeliveryActivity.this, "failed to update user order list", Toast.LENGTH_SHORT).show();

                                         }
                                     }
                                 });
                             }else{
                                 Toast.makeText(DeliveryActivity.this, "Order Cancelled", Toast.LENGTH_SHORT).show();
                             }
                        }
                    });


    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Failed and cause is :" + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {

        super.onStart();
        ///accesing quantity///////////////////////////
        if(getQtyIDs) {
            loadingDialog.show();
            for ( int x = 0; x < cartItemModelList.size() - 1; x++) {

               for( int y=0;y<cartItemModelList.get(x).getProductQuantity();y++)
               {

                   String quantityDocumentName= UUID.randomUUID().toString().substring(0,20);
                   Map<String,Object>timestamp=new HashMap<>();
                   timestamp.put("time", FieldValue.serverTimestamp());
                   int finalX = x;
                   int finalY = y;
                   int finalX1 = x;
                   firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(quantityDocumentName).set(timestamp)
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if(task.isSuccessful()){
                                       cartItemModelList.get(finalX).getQtyIDs().add(quantityDocumentName);

                                       if(finalY +1 == cartItemModelList.get(finalX).getProductQuantity())
                                       {


                                           firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(finalX).getProductID()).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).limit(cartItemModelList.get(finalX).getStockQuantity()).get()
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
                                                               boolean noLongerAvailable=true;
                                                               for(  String qtyId : cartItemModelList.get(finalX).getQtyIDs()){
                                                                   cartItemModelList.get(finalX).setQtyError(false);
                                                                   if(!serverQuantity.contains(qtyId))
                                                                   {
                                                                       if(noLongerAvailable){
                                                                           cartItemModelList.get(finalX).setInStock(false);
                                                                       }else{

                                                                           cartItemModelList.get(finalX).setQtyError(true);
                                                                           cartItemModelList.get(finalX).setMaxQuantity(availableQty);
                                                                           Toast.makeText(DeliveryActivity.this, "Sorry ! all products may not be available  in required quantity...", Toast.LENGTH_SHORT).show();

                                                                       }



                                                                   }else{
                                                                       availableQty++;
                                                                       noLongerAvailable=false;
                                                                   }

                                                               }
                                                               cartAdapter.notifyDataSetChanged();
                                                           }
                                                           else
                                                           {
                                                               String error=task.getException().getMessage();
                                                               Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                                           }
                                                           loadingDialog.dismiss();
                                                       }
                                                   });

                                       }
                                   }else{
                                       loadingDialog.dismiss();
                                       String error=task.getException().getMessage();
                                       Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                   }

                               }
                           });
               }
            }
        }else {
            getQtyIDs=true;
        }
        ///accesing quantity///////////////////////////

        name = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getName();
        mobileno = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getMobileNo();
        if(DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternatemobileNo()==""){
            fullName.setText(name + " - " + mobileno);
        }else{
            fullName.setText(name + " - " + mobileno+"or"+DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternatemobileNo());
        }
        String flat_no = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getFlatNo();
        String locality = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLocality();
        String landmark = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLandmark();
        String city = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getCity();
        String state = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getState();
        if(landmark.equals("")){
            fullAddress.setText(flat_no + " " + locality + " " + city + " " + state);
        }else {
            fullAddress.setText(flat_no + " " + locality + " " + landmark + " " + city + " " + state);


        }
        pincode.setText(DBqueries.addressesModelList.get(DBqueries.selectedAddress).getPincode());

        if(codOrderConconfirmed){
            showConfirmationLayout();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // for back button
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onPause() {
        super.onPause();
        loadingDialog.dismiss();
        if (getQtyIDs) {



            for (int x = 0; x < cartItemModelList.size() - 1; x++) {
                if(!successResponse) {
                    for (final String qtyID : cartItemModelList.get(x).getQtyIDs()) {
                        int finalX = x;
                        int finalX1 = x;
                        int finalX2 = x;
                        int finalX3 = x;
                        firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(qtyID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                if(qtyID.equals(cartItemModelList.get(finalX).getQtyIDs().get(cartItemModelList.get(finalX).getQtyIDs().size()-1)))
                                {

                                    cartItemModelList.get(finalX).getQtyIDs().clear();

                                }
                            }
                        });

                    }
                }else
                {cartItemModelList.get(x).getQtyIDs().clear();}

            }
        }
    }


    @Override
    public void onBackPressed() {
        if (successResponse) {
            finish();
        }
        super.onBackPressed();
    }

    private  void showConfirmationLayout() {
     codOrderConconfirmed=false;
        successResponse = true;
        getQtyIDs=false;

        for(int x=0;x<cartItemModelList.size()-1;x++){
            for(String qtyID : cartItemModelList.get(x).getQtyIDs())
            {
                firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY").document(qtyID).update("user_ID",FirebaseAuth.getInstance().getUid());


            }


        }


        ///// sent confirmation SMS
        try {

            Intent i = new Intent(DeliveryActivity.this, PaymentSuccessfull.class);
            startActivity(i);
            Toast.makeText(this, "Successful payment ID :" + str2, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
        }

        if (fromCart) {
            loadingDialog.show();
            Map<String, Object> updateCartList = new HashMap<>();
            long cartListsize = 0;
            List<Integer> indexList = new ArrayList<>();
            for (int x = 0; x < DBqueries.cartList.size(); x++) {
                if (!cartItemModelList.get(x).isInStock()) {
                    updateCartList.put("product_ID_" + cartListsize, cartItemModelList.get(x).getProductID());
                    cartListsize++;
                } else {
                    indexList.add(x);
                }
            }
            updateCartList.put("list_size", (long) cartListsize);
            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                    .set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                for (int x = 0; x < indexList.size(); x++) {
                                    DBqueries.cartList.remove(indexList.get(x).intValue());
                                    DBqueries.cartItemModelList.remove(cartItemModelList.size() - 1);
                                }
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                            }
                            loadingDialog.dismiss();
                        }
                    });
        }
    }
    private void placeOrderDetails(){
        loadingDialog.show();
        for(CartItemModel cartItemModel:cartItemModelList) {
            if(cartItemModel.getType()==CartItemModel.CART_ITEM) {

                String UserID=FirebaseAuth.getInstance().getUid();
                Map<String,Object>orderDetails=new HashMap<>();
                orderDetails.put("ORDER_ID",order_id);
                orderDetails.put("Product Id",cartItemModel.getProductID());
                orderDetails.put("Product Image",cartItemModel.getProductImage());
                orderDetails.put("Product Title",cartItemModel.getProductTitle());
                orderDetails.put("User Id",UserID);
                orderDetails.put("Product Quantity", cartItemModel.getProductQuantity());
                if(cartItemModel.getCuttedPrice()!=(null)) {
                    orderDetails.put("Cutted Price", cartItemModel.getCuttedPrice());
                }else{
                    orderDetails.put("Cutted Price", "");
                }
                orderDetails.put("Product Price",cartItemModel.getProductPrice());
                if(cartItemModel.getSelectedCoupenId()!=(null)) {
                    orderDetails.put("Coupen Id", cartItemModel.getSelectedCoupenId());
                }else{
                    orderDetails.put("Coupen Id", "");
                }
                if(cartItemModel.getDiscountedPrice()!=(null)) {
                    orderDetails.put("Discounted Price", "");
                }else{
                    orderDetails.put("Discounted Price", "");
                }
                orderDetails.put("Ordered date",FieldValue.serverTimestamp());
                orderDetails.put("Packed date",FieldValue.serverTimestamp());
                orderDetails.put("Shipped date",FieldValue.serverTimestamp());
                orderDetails.put("Delivered date",FieldValue.serverTimestamp());
                orderDetails.put("Cancelled date",FieldValue.serverTimestamp());
                orderDetails.put("Order Status","Ordered");

                orderDetails.put("Payment Method",paymentMethod);
                orderDetails.put("Address",fullAddress.getText());
                orderDetails.put("FullName",fullName.getText());
                orderDetails.put("PinCode",pincode.getText());
                orderDetails.put("Free Coupens",cartItemModel.getFreeCoupens());
                orderDetails.put("Delivery Price",cartItemModelList.get(cartItemModelList.size()-1).getDeliveryPrice());
                orderDetails.put("Cancellation requested",false);


                firebaseFirestore.collection("ORDERS").document(String.valueOf(order_id)).collection("OrderItems").document(cartItemModel.getProductID())
                        .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful()){
                                    String error=task.getException().getMessage();
                                    Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }else {
                Map<String,Object>orderDetails=new HashMap<>();
                orderDetails.put("Total Items",cartItemModel.getTotalItems());
                orderDetails.put("Total Items Price",cartItemModel.getTotalItemsPrice());
                orderDetails.put("Delivery Price",cartItemModel.getDeliveryPrice());
                orderDetails.put("Total Amount",cartItemModel.getTotalAmoount());
                orderDetails.put("Saved Amount",cartItemModel.getSavedAmount());
                orderDetails.put("Payment Status"," Not paid");
                orderDetails.put("Order Status","Canceled");
               firebaseFirestore.collection("ORDERS").document(String.valueOf(order_id))
                       .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if(task.isSuccessful()){
                           if(paymentMethod.equals("PAYTM")){
                               paytm();
                           }else{
                               cod();
                           }
                       }else{
                           String error=task.getException().getMessage();
                           Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                       }
                   }
               });

            }

        }
    }
    private void paytm(){
        getQtyIDs=false;
        paymentMethodDialog.dismiss();
        loadingDialog.show();
        makepayment();
    }
    private void cod(){
        getQtyIDs=false;
        paymentMethodDialog.dismiss();
        Intent otpIntent = new Intent(DeliveryActivity.this, OTPverificationActivity.class);
        otpIntent.putExtra("mobileNo", mobileno.substring(0, 10));
        otpIntent.putExtra("order_id",order_id);
        startActivity(otpIntent);
    }
}
