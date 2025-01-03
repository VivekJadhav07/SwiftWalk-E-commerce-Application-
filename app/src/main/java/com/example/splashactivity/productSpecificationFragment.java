package com.example.splashactivity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class productSpecificationFragment extends Fragment {


    public productSpecificationFragment() {
        // Required empty public constructor
    }

    private RecyclerView productSpecificationRecyclerview;
    public List<ProductSpecificationModel>productSpecificationModelList;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_product_specification, container, false);

        productSpecificationRecyclerview = view.findViewById(R.id.product_specification_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(linearLayoutManager.VERTICAL);

        productSpecificationRecyclerview.setLayoutManager(linearLayoutManager);

//        productSpecificationModelList.add(new ProductSpecificationModel(0,"General"));
//        productSpecificationModelList.add(new ProductSpecificationModel(1,"SIZE","10"));
//        productSpecificationModelList.add(new ProductSpecificationModel(1,"SIZE","10"));
//        productSpecificationModelList.add(new ProductSpecificationModel(1,"SIZE","10"));
//        productSpecificationModelList.add(new ProductSpecificationModel(1,"SIZE","10"));
//        productSpecificationModelList.add(new ProductSpecificationModel(1,"SIZE","10"));
//        productSpecificationModelList.add(new ProductSpecificationModel(1,"SIZE","10"));
//        productSpecificationModelList.add(new ProductSpecificationModel(0,"Quality"));
//
//        productSpecificationModelList.add(new ProductSpecificationModel(1,"SIZE","10"));
//        productSpecificationModelList.add(new ProductSpecificationModel(1,"SIZE","10"));
//        productSpecificationModelList.add(new ProductSpecificationModel(1,"SIZE","10"));
//        productSpecificationModelList.add(new ProductSpecificationModel(1,"SIZE","10"));
//        productSpecificationModelList.add(new ProductSpecificationModel(1,"SIZE","10"));
//        productSpecificationModelList.add(new ProductSpecificationModel(1,"SIZE","10"));
//
//
//        productSpecificationModelList.add(new ProductSpecificationModel(0,"General"));
//        productSpecificationModelList.add(new ProductSpecificationModel(1,"SIZE","10"));
//        productSpecificationModelList.add(new ProductSpecificationModel(1,"SIZE","10"));
//        productSpecificationModelList.add(new ProductSpecificationModel(1,"SIZE","10"));
//        productSpecificationModelList.add(new ProductSpecificationModel(1,"SIZE","10"));
//        productSpecificationModelList.add(new ProductSpecificationModel(1,"SIZE","10"));
//        productSpecificationModelList.add(new ProductSpecificationModel(1,"SIZE","10"));
//        productSpecificationModelList.add(new ProductSpecificationModel(0,"Quality"));
//
//        productSpecificationModelList.add(new ProductSpecificationModel(1,"SIZE","10"));
//        productSpecificationModelList.add(new ProductSpecificationModel(1,"SIZE","10"));
//        productSpecificationModelList.add(new ProductSpecificationModel(1,"SIZE","10"));
//        productSpecificationModelList.add(new ProductSpecificationModel(1,"SIZE","10"));
//        productSpecificationModelList.add(new ProductSpecificationModel(1,"SIZE","10"));
//        productSpecificationModelList.add(new ProductSpecificationModel(1,"SIZE","10"));
        ProductSpecificationAdapter productSpecificationAdapter = new ProductSpecificationAdapter(productSpecificationModelList);
        productSpecificationRecyclerview.setAdapter(productSpecificationAdapter);
        productSpecificationAdapter.notifyDataSetChanged();
        return  view;
    }
}