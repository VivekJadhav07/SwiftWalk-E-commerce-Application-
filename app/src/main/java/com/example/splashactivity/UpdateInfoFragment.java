package com.example.splashactivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.Manifest;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.grpc.Context;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateInfoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UpdateInfoFragment() {
        // Required empty public constructor
    }

    private Dialog loadingDialog, passDialog;
    private CircleImageView circleImageView;
    private Button changePhotoBtn, removeBtn, updateBtn, doneBtn;
    private EditText nameField, emailField, password;
    private String name, email, photo;
    Uri imageUri;
    private boolean updatePhoto = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpdateInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateInfoFragment newInstance(String param1, String param2) {
        UpdateInfoFragment fragment = new UpdateInfoFragment();
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
        View view = inflater.inflate(R.layout.fragment_update_info, container, false);
        circleImageView = view.findViewById(R.id.profile_image);
        changePhotoBtn = view.findViewById(R.id.change_photo_btn);
        removeBtn = view.findViewById(R.id.remove_photo_btn);
        updateBtn = view.findViewById(R.id.update);
        nameField = view.findViewById(R.id.name);
        emailField = view.findViewById(R.id.email);

        //loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //loading dialog

        //passsword loading dialog
        passDialog = new Dialog(getContext());
        passDialog.setCancelable(true);
        passDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        passDialog.setContentView(R.layout.password_confirmation_dialog);
        passDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // passsword loading dialog
        password = passDialog.findViewById(R.id.password);
        doneBtn = passDialog.findViewById(R.id.done_btn);

        name = getArguments().getString("Name");
        email = getArguments().getString("Email");
        photo = getArguments().getString("Photo");


        Glide.with(getContext()).load(photo).into(circleImageView);
        nameField.setText(name);
        emailField.setText(email);
        changePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {


                        Intent galleyIntent = new Intent(Intent.ACTION_PICK);

                        galleyIntent.setType("image/*");
                        startActivityForResult(galleyIntent, 1);
                    } else {
                        getActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                    }

                } else {
                    Intent galleyIntent = new Intent(Intent.ACTION_PICK);

                    galleyIntent.setType("image/*");
                    startActivityForResult(galleyIntent, 1);
                }
            }
        });
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageUri = null;
                updatePhoto = true;
                Glide.with(getContext()).load(R.drawable.profile_placeholder).into(circleImageView);

            }
        });
        emailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                checkInputs();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkInputs();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkEmailAndPassword();
            }
        });


        return view;
    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(emailField.getText())) {
            if (!TextUtils.isEmpty(nameField.getText())) {

                updateBtn.setEnabled(true);
                updateBtn.setTextColor(Color.rgb(255, 255, 255));
                String hexColor = "#1B2B81";
                updateBtn.setBackgroundColor(Color.parseColor(hexColor));


            } else {
                updateBtn.setEnabled(false);
                updateBtn.setTextColor(Color.parseColor("#FFFFFF"));

            }
        } else {
            updateBtn.setEnabled(false);
            updateBtn.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    private void checkEmailAndPassword() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
        Drawable customErrorIcon = getResources().getDrawable(R.drawable.warning);
        customErrorIcon.setBounds(0, 0, customErrorIcon.getIntrinsicWidth(), customErrorIcon.getIntrinsicHeight());

        if (emailField.getText().toString().matches(emailPattern)) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (emailField.getText().toString().toLowerCase().trim().equals(email.toLowerCase().trim())) {
                // Same email, proceed with updating other fields
                loadingDialog.show();
                updateFields(user);
            } else {
                // Update email and other fields
                passDialog.show();
                doneBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userPassword = password.getText().toString().trim();
                        if (TextUtils.isEmpty(userPassword)) {
                            // Display toast indicating that the password field is empty
                            Toast.makeText(getContext(), "Please enter a password", Toast.LENGTH_SHORT).show();
                        } else {
                            // Proceed with re-authentication
                            loadingDialog.show();
                            passDialog.dismiss();
                            AuthCredential credential = EmailAuthProvider.getCredential(email, userPassword);

                            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        user.updateEmail(emailField.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    updateFields(user);
                                                } else {
                                                    loadingDialog.dismiss();
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        loadingDialog.dismiss();
                                        String error = task.getException().getMessage();
                                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });

            }
        } else {
            emailField.setError("Invalid Email!", customErrorIcon);
        }
    }

    private void updateFields(FirebaseUser user) {
        // Map to store updated user data
        Map<String, Object> updateData = new HashMap<>();
        if (!nameField.getText().toString().equals(name)) {
            updateData.put("fullname", nameField.getText().toString());
        }

        // Check if any data is changed
        if (!updateData.isEmpty() || updatePhoto) {
            // Data changed, proceed with updating
            FirebaseFirestore.getInstance().collection("USERS").document(user.getUid()).update(updateData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Update successful
                                if (updateData.containsKey("fullname")) {
                                    DBqueries.fullname = nameField.getText().toString().trim();
                                }

                                if (updatePhoto) {
                                    updatePhoto(user);
                                } else {
                                    loadingDialog.dismiss();
                                    getActivity().finish();
                                    Toast.makeText(getContext(), "Successfully Updated!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Update failed
                                loadingDialog.dismiss();
                                String error = task.getException().getMessage();
                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // No data changed, dismiss loading dialog
            loadingDialog.dismiss();
            Toast.makeText(getContext(), "No updates were made.", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == getActivity().RESULT_OK) {
                if (data != null) {
                    imageUri = data.getData();
                    updatePhoto = true;
                    Log.d("UpdateInfoFragment", "Selected image URI: " + imageUri);
                    Glide.with(getContext())
                            .load(imageUri)
                            .into(circleImageView);
                } else {
                    Toast.makeText(getContext(), "Image not found!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

private void updateFields(FirebaseUser user,Map<String,Object>updateData){
    FirebaseFirestore.getInstance().collection("USERS").document(user.getUid()).update(updateData)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        if(updateData.size()>1) {
                            DBqueries.email=emailField.getText().toString().toString().trim();
                            DBqueries.fullname=nameField.getText().toString().toString().trim();

                        }else{
                              DBqueries.fullname=nameField.getText().toString().toString().trim();
                        }
                        getActivity().finish();
                        Toast.makeText(getContext(), "Successfullly Updated!", Toast.LENGTH_SHORT).show();
                    } else {

                        String error = task.getException().getMessage();
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.dismiss();
                }
            });
}
private void updatePhoto(FirebaseUser user){
    ////updating photo
    if (updatePhoto) {
         StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profile/" + user.getUid() + ".jpg");
        if (imageUri != null) {

            Glide.with(getContext()).asBitmap().load(imageUri).circleCrop().into(new ImageViewTarget<Bitmap>(circleImageView) {

                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    resource.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = storageReference.putBytes(data);
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {

                                storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            imageUri = task.getResult();
                                            DBqueries.profile = task.getResult().toString();
                                            Glide.with(getContext()).load(DBqueries.profile).into(circleImageView);

                                            Map<String, Object> updateData = new HashMap<>();
                                            updateData.put("email", emailField.getText().toString());
                                            updateData.put("fullname", nameField.getText().toString());
                                            updateData.put("profile", DBqueries.profile);
                                            updateFields(user,updateData);
                                            loadingDialog.dismiss();


                                        } else {
                                            loadingDialog.dismiss();
                                            DBqueries.profile = "";
                                            String error = task.getException().getMessage();
                                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                loadingDialog.dismiss();
                                String error = task.getException().getMessage();
                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    return;
                }

                @Override
                protected void setResource(@Nullable Bitmap resource) {
                    circleImageView.setImageResource(R.mipmap.splashlogo);
                }
            });

        } else {///remove photo
            storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){

                        DBqueries.profile ="";
                        Glide.with(getContext()).load(DBqueries.profile).into(circleImageView);

                        Map<String, Object> updateData = new HashMap<>();
                        updateData.put("email", emailField.getText().toString());
                        updateData.put("fullname", nameField.getText().toString());
                        updateData.put("profile", "");
                        updateFields(user,updateData);
                    }else{
                        loadingDialog.dismiss();
                        String error = task.getException().getMessage();
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }else{

        DBqueries.profile ="";
        Glide.with(getContext()).load(DBqueries.profile).into(circleImageView);

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("fullname", nameField.getText().toString());
        updateFields(user,updateData);

    }

    ///updating photo

}
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent galleyIntent = new Intent(Intent.ACTION_PICK);

                galleyIntent.setType("image/*");
                startActivityForResult(galleyIntent, 1);
            } else {
                Toast.makeText(getContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}