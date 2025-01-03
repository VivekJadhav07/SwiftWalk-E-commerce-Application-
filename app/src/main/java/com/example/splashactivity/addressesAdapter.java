package com.example.splashactivity;

import static com.example.splashactivity.DeliveryActivity.SELECT_ADDRESS;
import static com.example.splashactivity.MyAccountFragment.MANAGE_ADDRESS;
import static com.example.splashactivity.My_AddressesActivity.refreshItem;

import android.app.Dialog;
import android.content.Intent;
import android.media.Image;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class addressesAdapter extends RecyclerView.Adapter<addressesAdapter.Viewholder> {

    private List<addressesModel> addressesModelList;
    private int MODE;
    private int preselectedposition;
    private boolean refresh = false;
    private Dialog loadingDialog;


    public addressesAdapter(List<addressesModel> addressesModelList, int MODE, Dialog loadingDialog) {
        this.addressesModelList = addressesModelList;
        this.MODE = MODE;
        preselectedposition = DBqueries.selectedAddress;
        if (loadingDialog!=null){
            this.loadingDialog=loadingDialog;
        }
    }

    @NonNull
    @Override
    public addressesAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.addresses_item_layout, viewGroup, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull addressesAdapter.Viewholder viewholder, int position) {
//        addressesModel model = addressesModelList.get(position);

        String city=addressesModelList.get(position).getCity();
        String locality=addressesModelList.get(position).getLocality();
        String flatNo=addressesModelList.get(position).getFlatNo();
        String pincode=addressesModelList.get(position).getPincode();
        String landmark=addressesModelList.get(position).getLandmark();
        String name=addressesModelList.get(position).getName();
        String mobileNo=addressesModelList.get(position).getMobileNo();
        String alternatemobileNo=addressesModelList.get(position).getAlternatemobileNo();
        String state=addressesModelList.get(position).getState();
        Boolean selected = addressesModelList.get(position).getSelected();
        viewholder.setData(name, city, pincode, selected, position, mobileNo,alternatemobileNo,flatNo,locality,state,landmark);
    }

    @Override
    public int getItemCount() {
        return addressesModelList.size(); // Return the actual size of the list
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private TextView fullname;
        private TextView addres;
        private TextView pincode;
        private ImageView icon;
        private LinearLayout optionContainer;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            fullname = itemView.findViewById(R.id.name);
            addres = itemView.findViewById(R.id.address);
            pincode = itemView.findViewById(R.id.pincode);
            icon = itemView.findViewById(R.id.icon_view);
            optionContainer = itemView.findViewById(R.id.option_container);
        }

        private void setData(String username, String city, String userpincode, Boolean selected,final  int position, String mobileNo,String alternateMobileNo,String flatNo,String locality,String state,String landmark) {

            if(alternateMobileNo==""){
                fullname.setText(username + " - " + mobileNo);
            }else{
                fullname.setText(username + " - " + mobileNo+"or"+alternateMobileNo);
            }
            String flat_no="";
            if (DBqueries.selectedAddress >= 0 && DBqueries.selectedAddress < addressesModelList.size()) {
                 flat_no = addressesModelList.get(DBqueries.selectedAddress).getFlatNo();

            }
            if(landmark.equals("")){
                addres.setText(flat_no + " " + locality + " " + city + " " + state);
            }else {
                addres.setText(flat_no + " " + locality + " " + landmark + " " + city + " " + state);


            }
            pincode.setText(userpincode);


            if (MODE == SELECT_ADDRESS) {

                icon.setImageResource(R.drawable.baseline_check_24);
                if (selected) {
                    icon.setVisibility(View.VISIBLE);
                    preselectedposition = position;
                } else {
                    icon.setVisibility(View.GONE);
                }
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (preselectedposition != position) {
                            addressesModelList.get(position).setSelected(true);
                            addressesModelList.get(preselectedposition).setSelected(false);
                            refreshItem(preselectedposition, position);
                            preselectedposition = position;
                            DBqueries.selectedAddress = position;
                        }
                    }
                });

            } else if (MODE == MANAGE_ADDRESS) {
                optionContainer.setVisibility(View.GONE);
                optionContainer.getChildAt(0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {////edit Addresss
                        Intent addAddressIntent  = new Intent(itemView.getContext(),AddAddressActivity.class);
                        addAddressIntent.putExtra("INTENT","update_address");
                        addAddressIntent.putExtra("index",position);
                        itemView.getContext().startActivity(addAddressIntent);
                        refresh=false;
                    }
                });
                optionContainer.getChildAt(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {//remove addresss

                        loadingDialog.show();

                        Map<String,Object> addresses=new HashMap<>();
                        int x=0;
                        int selected=-1;
                        for (int i=0;i<addressesModelList.size();i++){
                            if (i!=position){
                                x++;
                                addresses.put("city_" + x,addressesModelList.get(i).getCity());
                                addresses.put("locality_" + x,addressesModelList.get(i).getLocality());
                                addresses.put("flat_no_" + x,addressesModelList.get(i).getFlatNo());
                                addresses.put("pincode_" + x,addressesModelList.get(i).getPincode());
                                addresses.put("landmark_" + x,addressesModelList.get(i).getLandmark());
                                addresses.put("name_" + x,addressesModelList.get(i).getName());
                                addresses.put("mobile_no_" + x,addressesModelList.get(i).getMobileNo());
                                addresses.put("alternate_mobile_no" + x,addressesModelList.get(i).getAlternatemobileNo());
                                addresses.put("state_" + x,addressesModelList.get(i).getState());
                                if (addressesModelList.get(position).getSelected()){
                                    if (position-1>=0){
                                        if (x==position){
                                            addresses.put("selected_" + x,true);
                                            selected=x;

                                        }else{
                                            addresses.put("selected_" + x,addressesModelList.get(i).getSelected());
                                        }
                                    }else{
                                        if (x==1){
                                            addresses.put("selected_" + x,true);
                                            selected=x;
                                        }else{
                                            addresses.put("selected_" + x,addressesModelList.get(i).getSelected());
                                        }
                                    }
                                }else{
                                    addresses.put("selected_" + x,addressesModelList.get(i).getSelected());
                                    if (addressesModelList.get(i).getSelected()){
                                        selected=x;
                                    }

                                }

                            }
                        }
                        addresses.put("list_size",x);

                        int finalSelected = selected;
                        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES")
                                .set(addresses).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            DBqueries.addressesModelList.remove(position);
                                            if (finalSelected!=-1) {
                                                DBqueries.selectedAddress = finalSelected - 1;
                                                DBqueries.addressesModelList.get(finalSelected - 1).setSelected(true);
                                            }else if (DBqueries.addressesModelList.size()==0){
                                                DBqueries.selectedAddress=-1;
                                            }

                                            notifyDataSetChanged();
                                        }else{
                                            String error = task.getException().getMessage();
                                            Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                        }
                                        loadingDialog.dismiss();
                                    }
                                });

                        refresh=false;

                    }
                });
                icon.setImageResource(R.drawable.vertical_dot);
                icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        optionContainer.setVisibility(View.VISIBLE);
                        if (refresh) {
                            refreshItem(preselectedposition, preselectedposition);
                        } else {
                            refresh = true;
                        }
                        preselectedposition = position;

                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refreshItem(preselectedposition, preselectedposition);
                        preselectedposition = -1;
                    }
                });
            }
        }
    }
}
