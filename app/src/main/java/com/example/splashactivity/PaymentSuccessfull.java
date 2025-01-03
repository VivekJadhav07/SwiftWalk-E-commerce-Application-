package com.example.splashactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class PaymentSuccessfull extends AppCompatActivity
{
    private ImageButton continue_shopping_button;
    private TextView order_id;
    public static String s;
    ConstraintLayout layout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_successfull);
        layout=findViewById(R.id.confirm_constraint_layout);
        layout.setVisibility(View.VISIBLE);
        order_id = findViewById(R.id.order_id);

        s = order_id.getText()+" "+DeliveryActivity.str;
        order_id.setText(s);

        continue_shopping_button=findViewById(R.id.continue_shopping_button);
        continue_shopping_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent i=new Intent(PaymentSuccessfull.this,MainActivity.class);
              startActivity(i);
            }
        });

    }
}
