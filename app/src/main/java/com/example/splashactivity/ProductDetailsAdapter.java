package com.example.splashactivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class ProductDetailsAdapter extends FragmentPagerAdapter {

    private int totalTabs;
    private String productDescription;
    private String productOtherDetails;
    private List<ProductSpecificationModel> productSpecificationModelList;

    public ProductDetailsAdapter(@NonNull FragmentManager fm,int totalTabs, String productDescription, String productOtherDetails, List<ProductSpecificationModel> productSpecificationModelList) {
        super(fm);
        this.productDescription = productDescription;
        this.productOtherDetails = productOtherDetails;
        this.productSpecificationModelList = productSpecificationModelList;
        this.totalTabs = totalTabs;
    }


    @NonNull
    @Override
    public Fragment getItem(int i) {
        switch(i){
            case 0:
                productDescriptionFragment productDescriptionFragment1 = new productDescriptionFragment();
                productDescriptionFragment1.body=productDescription;
                return productDescriptionFragment1;
            case 1:
                productSpecificationFragment productSpecificationFragmentt = new productSpecificationFragment();
                productSpecificationFragmentt.productSpecificationModelList=productSpecificationModelList;
                return productSpecificationFragmentt;
            case 2:
                productDescriptionFragment productDescriptionFragment2 = new productDescriptionFragment();
                productDescriptionFragment2.body=productOtherDetails;
                return productDescriptionFragment2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
