package com.example.splashactivity;

import static com.example.splashactivity.DBqueries.firebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OTPverificationActivity extends AppCompatActivity {
    private TextView phoneNo;
    private EditText Otp;
    private Button Btn;
    private String userNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);
        phoneNo = findViewById(R.id.phone_no);
        Btn = findViewById(R.id.verify1);
        Otp = findViewById(R.id.otp);
        //  userNo = getIntent().getStringExtra("mobileNO");
        userNo = DeliveryActivity.mobileno;
        phoneNo.setText("Verification code has been sent to +91" + userNo);
        Random random = new Random();
        final int  OTP_number = random.nextInt(999999 - 111111 + 1) + 111111;
        String SMS_API = "https://www.fast2sms.com/dev/bulkV2";



        StringRequest stringRequest = new StringRequest(Request.Method.POST, SMS_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Set OnClickListener for Btn
                        Btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Generate a new OTP every time the button is clicked


                                if (Otp.getText().toString().equals(String.valueOf(OTP_number))) {
                                    Map<String,Object>updatestatus=new HashMap<>();
                                    updatestatus.put("Order Status","Ordered");
                                    String order_id=getIntent().getStringExtra("order_id");
                                    FirebaseFirestore.getInstance().collection("ORDERS").document(String.valueOf(order_id)).update(updatestatus)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Map <String,Object> userOrder=new HashMap<>();
                                                        userOrder.put("order_id",order_id);
                                                        userOrder.put("time", FieldValue.serverTimestamp());
                                                        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").document(order_id).set(userOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    DeliveryActivity.codOrderConconfirmed = true;
                                                                    finish();

                                                                }else {
                                                                    Toast.makeText(OTPverificationActivity.this, "failed to update user order list", Toast.LENGTH_SHORT).show();

                                                                }
                                                            }
                                                        });

                                                    }else{
                                                        Toast.makeText(OTPverificationActivity.this, "Order Cancelled", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                } else {
                                    Toast.makeText(OTPverificationActivity.this, "OTP incorrect!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // onErrorResponse logic
                Toast.makeText(OTPverificationActivity.this, "Failed to send the OTP verification code", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", "fmW391LEKAdVwYDshjipTRnqePI6zNvu7ktxlJMacbBO5C04ZS619lpvhyNPR3g8r5E2xTZWueQ7fMFd");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> body = new HashMap<>();
                body.put("route", "otp");
                body.put("variables_values", String.valueOf(OTP_number));
                body.put("numbers", userNo);
                return body;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT

        ));
        RequestQueue requestQueue = Volley.newRequestQueue(OTPverificationActivity.this);
        requestQueue.add(stringRequest);
    }
}
