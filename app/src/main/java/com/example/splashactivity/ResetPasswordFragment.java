package com.example.splashactivity;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResetPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResetPasswordFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    private EditText registerEmail;
    private Button resetPasswordButton;
    private TextView goBack;
    private FrameLayout parentFrameLayout;

    private FirebaseAuth firebaseAuth;

    private ViewGroup emailIconContainer;
    private ImageView emailIcon;
    private TextView emailiconText;
    private ProgressBar progressbar;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResetPasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResetPasswordFragment newInstance(String param1, String param2) {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_reset_password, container, false);

        registerEmail = view.findViewById(R.id.forgot_password_email);
        resetPasswordButton = view.findViewById(R.id.reset_password_button);
        String hexColor1="#3E59EF";
        resetPasswordButton.setBackgroundColor(Color.parseColor(hexColor1));
        goBack = view.findViewById(R.id.tv_forgot_password_go_back);
        parentFrameLayout = getActivity().findViewById(R.id.register_framelayout);
        firebaseAuth = FirebaseAuth.getInstance();


        emailIconContainer = view.findViewById(R.id.forgot_password_email_icon_container);
        emailIcon = view.findViewById(R.id.forgot_password_email_icon);
        emailiconText = view.findViewById(R.id.forgot_password_email_icon_text);
        progressbar = view.findViewById(R.id.forgot_password_progressbar);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registerEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                TransitionManager.beginDelayedTransition(emailIconContainer);
                emailiconText.setVisibility(View.GONE);

                TransitionManager.beginDelayedTransition(emailIconContainer);
                emailIcon.setVisibility(View.VISIBLE);
                progressbar.setVisibility(View.VISIBLE);

                resetPasswordButton.setEnabled(false);
                resetPasswordButton.setTextColor(Color.argb(50,255,255,255));

                firebaseAuth.sendPasswordResetEmail(registerEmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    ScaleAnimation scaleAnimation = new ScaleAnimation(1,0,1,0,emailIcon.getWidth()/2,emailIcon.getHeight()/2);
                                    scaleAnimation.setDuration(100);
                                    scaleAnimation.setInterpolator(new AccelerateInterpolator());
                                    scaleAnimation.setRepeatMode(Animation.REVERSE);
                                    scaleAnimation.setRepeatCount(1);


                                    scaleAnimation.setAnimationListener(new Animation.AnimationListener(){

                                        public void onAnimationEnd(Animation animation) {
                                            emailiconText.setText("Recovery email sent successfully ! check your inbox");
                                            emailiconText.setTextColor(getResources().getColor(R.color.successGreen));

                                            TransitionManager.beginDelayedTransition(emailIconContainer);
                                            emailiconText.setVisibility(View.VISIBLE);
                                        }


                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }
                                        @Override
                                        public void onAnimationRepeat(Animation animation) {
                                            emailIcon.setImageResource(R.drawable.baseline_email_24_green);
                                            emailiconText.setTextColor(getResources().getColor(R.color.successGreen));

                                        }
                                    });

                                    emailIcon.startAnimation(scaleAnimation);


                                }else{
                                    String error = task.getException().getMessage();
                                    emailiconText.setText(error);
                                    emailIcon.setImageResource(R.drawable.baseline_email_24);
                                    emailiconText.setTextColor(getResources().getColor(R.color.btnRed));
                                    TransitionManager.beginDelayedTransition(emailIconContainer);
                                    emailiconText.setVisibility(View.VISIBLE);
                                }
                                progressbar.setVisibility(View.GONE);
                                resetPasswordButton.setEnabled(true);
                                resetPasswordButton.setTextColor(Color.rgb(255,255,255));
                            }
                        });
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new SigninFragment());

            }
        });

    }
    private void checkInputs(){
        if(TextUtils.isEmpty(registerEmail.getText())){
            String hexColor2="#3E59EF";
            resetPasswordButton.setBackgroundColor(Color.parseColor(hexColor2));
            resetPasswordButton.setBackgroundColor(Color.rgb(37,44,145));
        }else{
            resetPasswordButton.setEnabled(true);
            resetPasswordButton.setTextColor(Color.rgb(255,255,255));
            String hexColor="#1B2B81";
            resetPasswordButton.setBackgroundColor(Color.parseColor(hexColor));
        }

    }
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right,R.anim.slide_out_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }
}