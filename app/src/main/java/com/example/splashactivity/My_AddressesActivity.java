package com.example.splashactivity;

import static com.example.splashactivity.DeliveryActivity.SELECT_ADDRESS;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class My_AddressesActivity extends AppCompatActivity {
    private RecyclerView myAddressesRecyclerView;
    private static addressesAdapter addressesadapter;
    private Button deliverHereBtn;
    private LinearLayout addNewAddressBtn;
    private TextView addressesSaved;
    private Dialog loadingDialog;
    private int previousAddress;
    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);
        Toolbar toolbar=findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("My Addresses");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //loading dialog
        loadingDialog=new Dialog(this);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(this.getDrawable(R.drawable.slider_background));
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                addressesSaved.setText(String.valueOf(DBqueries.addressesModelList.size())+" saved addresses");
            }
        });
        //loading dialog


        previousAddress = DBqueries.selectedAddress;

        myAddressesRecyclerView=findViewById(R.id.addresses_recycler_vire);
        deliverHereBtn = findViewById(R.id.deliver_here_button);
        addNewAddressBtn = findViewById(R.id.add_new_address_button);
        addressesSaved = findViewById(R.id.address_saved);



        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        myAddressesRecyclerView.setLayoutManager(layoutManager);

        mode = getIntent().getIntExtra("MODE",-1);
        if(mode==SELECT_ADDRESS){
            deliverHereBtn.setVisibility(View.VISIBLE);
        }else{
            deliverHereBtn.setVisibility(View.GONE);
        }
        deliverHereBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DBqueries.selectedAddress!=previousAddress){
                    int previousAddressIndex=previousAddress;
                   loadingDialog.show();
                    Map<String,Object> updateSelection = new HashMap<>();
                    updateSelection.put("selected_"+String.valueOf(previousAddress+1),false);
                    updateSelection.put("selected_"+String.valueOf(DBqueries.selectedAddress+1),true);

                    previousAddress = DBqueries.selectedAddress;

                    FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES")
                            .update(updateSelection).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        finish();
                                    }else{
                                        previousAddress = previousAddressIndex;
                                        String error = task.getException().getMessage();
                                        Toast.makeText(My_AddressesActivity.this, error, Toast.LENGTH_SHORT).show();

                                    }
                                    loadingDialog.dismiss();
                                }
                            });
                }else{
                    finish();
                }
            }
        });
        addressesadapter = new addressesAdapter(DBqueries.addressesModelList,mode,loadingDialog);
        myAddressesRecyclerView.setAdapter(addressesadapter);
        ((SimpleItemAnimator)myAddressesRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        addressesadapter.notifyDataSetChanged();


        addNewAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addAddressIntent  = new Intent(My_AddressesActivity.this,AddAddressActivity.class);
                if (mode!=SELECT_ADDRESS){
                    addAddressIntent.putExtra("INTENT","manage");
                }else {
                    addAddressIntent.putExtra("INTENT", "null");
                }
                startActivity(addAddressIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        addressesSaved.setText(String.valueOf(DBqueries.addressesModelList.size())+" saved addresses");
    }

    public static void refreshItem(int deselect, int select){
        addressesadapter.notifyItemChanged(deselect);
        addressesadapter.notifyItemChanged(select);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            if(mode==SELECT_ADDRESS) {
                if (DBqueries.selectedAddress != previousAddress) {
                    DBqueries.addressesModelList.get(DBqueries.selectedAddress).setSelected(false);
                    DBqueries.addressesModelList.get(previousAddress).setSelected(true);
                    DBqueries.selectedAddress = previousAddress;
                }
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }


    @Override
    public void onBackPressed() {
        if(mode==SELECT_ADDRESS) {
            if (DBqueries.selectedAddress != previousAddress) {
                DBqueries.addressesModelList.get(DBqueries.selectedAddress).setSelected(false);
                DBqueries.addressesModelList.get(previousAddress).setSelected(true);
                DBqueries.selectedAddress = previousAddress;
            }
        }
        super.onBackPressed();
    }
}