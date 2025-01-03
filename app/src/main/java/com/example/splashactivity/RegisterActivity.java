package com.example.splashactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;

public class RegisterActivity extends AppCompatActivity {

    private FrameLayout framelayout;
    public static boolean onResetPasswordFragment = false;
    public static  boolean setSignUpFragment= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        framelayout = findViewById(R.id.register_framelayout);
        if(setSignUpFragment==true)
        {
            setSignUpFragment=false;
            setDefaultFragment(new SignupFragment());

        }
        else {


            setDefaultFragment(new SigninFragment());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){

            SignupFragment.disableCloseButton=false;
            SigninFragment.disableCloseButton=false;
            if(onResetPasswordFragment){
                onResetPasswordFragment = false;
                setFragment(new SigninFragment());
                return false;
            }else{

            }

        }else{

        }
        return super.onKeyDown(keyCode, event);
    }

    private void setDefaultFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(framelayout.getId(),fragment);
        fragmentTransaction.commit();
    }
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right,R.anim.slide_out_from_right);
        fragmentTransaction.replace(framelayout.getId(),fragment);
        fragmentTransaction.commit();
    }
}