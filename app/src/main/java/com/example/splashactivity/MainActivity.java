package com.example.splashactivity;



import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;


import com.example.splashactivity.databinding.ActivityMain2Binding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

   private NavigationView navigationView;
   private ImageView actionbar_logo;
    public  static  Boolean showCart=false;

    private static final int HOME_FRAGEMENT=0;
    private static final int CART_FRAGEMENT=1;
    private static final int ORDERS_FRAGMENT =2;
    private static final int WISHLIST_FRAGMENT=3;
    private static final int REWARDS_FRAGEMENT=4;
    private static final int ACCOUNT_FRAGEMENT=5;
    public    static DrawerLayout drawer;
    public static  boolean resetMainActivity=false;

    private Window window;
    private Toolbar toolbar;
    private Dialog signInDialog;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMain2Binding binding;
    private FrameLayout frameLayout;
    private  int currentFragment=-1;

    private ImageView noInternetConection;
    private TextView internetText;

    private FirebaseUser currentUser;
    private TextView badgeCount;
    private int scrollFlags;
    private AppBarLayout.LayoutParams params;
    private CircleImageView profileView;
    private TextView fullname,email;
    private ImageView addProfileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        actionbar_logo=findViewById(R.id.actionbar_logo);

        setSupportActionBar(toolbar);
        window=getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        params = (AppBarLayout.LayoutParams)toolbar.getLayoutParams();
        scrollFlags = params.getScrollFlags();



         drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

               // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navigationView=(NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);


       /* mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow).setOpenableLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);*/

        frameLayout = findViewById(R.id.main_frame_layout);

        profileView=navigationView.getHeaderView(0).findViewById(R.id.main_profile_image);
        fullname=navigationView.getHeaderView(0).findViewById(R.id.main_fullname);
        email=navigationView.getHeaderView(0).findViewById(R.id.main_email);
        addProfileIcon=navigationView.getHeaderView(0).findViewById(R.id.add_profile);



        if (showCart) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            gotoFragment("My Cart", new MyCartFragment(), -2);
        } else {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            setFragment(new HomeFragment(), HOME_FRAGEMENT);
        }



         signInDialog=new Dialog(MainActivity.this);
        signInDialog.setCancelable(true);
        signInDialog.setContentView(R.layout.sign_in_dialog);


        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Button dialogSignInButton=signInDialog.findViewById(R.id.sign_inbutton);
        Button dialogSignUnButton=signInDialog.findViewById(R.id.sign_upbtn);

        Intent registerIntent=new Intent (MainActivity.this,RegisterActivity.class);

        dialogSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignupFragment.disableCloseButton=true;
                SigninFragment.disableCloseButton=true;
                signInDialog.dismiss();
                RegisterActivity.setSignUpFragment=false;
                startActivity(registerIntent);
            }
        });

        dialogSignUnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SigninFragment.disableCloseButton=true;
                SignupFragment.disableCloseButton=true;
                signInDialog.dismiss();
                RegisterActivity.setSignUpFragment=true;
                startActivity(registerIntent);
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(false);
        } else {
            if(DBqueries.email==null) {
                FirebaseFirestore.getInstance().collection("USERS").document(currentUser.getUid())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DBqueries.fullname = task.getResult().getString("fullname");
                                    DBqueries.email = task.getResult().getString("email");
                                    DBqueries.profile = task.getResult().getString("profile");

                                    fullname.setText(DBqueries.fullname);
                                    email.setText(DBqueries.email);
                                    if (DBqueries.profile == "") {

                                        addProfileIcon.setVisibility(View.VISIBLE);
                                    } else {
                                        addProfileIcon.setVisibility(View.INVISIBLE);
                                        Glide.with(MainActivity.this).load(DBqueries.profile).apply(new RequestOptions().placeholder(R.drawable.profile_placeholder)).into(profileView);
                                    }
                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }else{
                fullname.setText(DBqueries.fullname);
                email.setText(DBqueries.email);
                if (DBqueries.profile .equals( "")) {
                      profileView.setImageResource(R.drawable.profile_placeholder);
                    addProfileIcon.setVisibility(View.VISIBLE);
                } else {
                    addProfileIcon.setVisibility(View.INVISIBLE);
                    Glide.with(MainActivity.this).load(DBqueries.profile).apply(new RequestOptions().placeholder(R.drawable.profile_placeholder)).into(profileView);
                }

            }
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(true);

        }
        if (resetMainActivity){
            resetMainActivity=false;
            actionbar_logo.setVisibility(View.VISIBLE);
            setFragment(new HomeFragment(),HOME_FRAGEMENT);
            navigationView.getMenu().getItem(0).setChecked(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        DBqueries.checkNotifications(true,null);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(currentFragment==HOME_FRAGEMENT) {
                currentFragment=-1;
                super.onBackPressed();
            }else{
                if(showCart)
                {
 showCart=false;
 finish();
                }
                else {
                    actionbar_logo.setVisibility(View.VISIBLE);
                    invalidateOptionsMenu();
                    setFragment(new HomeFragment(), HOME_FRAGEMENT);
                    navigationView.getMenu().getItem(0).setChecked(true);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(currentFragment == HOME_FRAGEMENT) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getMenuInflater().inflate(R.menu.main, menu);
            MenuItem cartItem = menu.findItem(R.id.main_cart_icon);
                cartItem.setActionView(R.layout.badge_layout);
                ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
                badgeIcon.setImageResource(R.drawable.cart_white);
                badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);
                if(currentUser!=null){

                    if (DBqueries.cartList.size() == 0) {
                        DBqueries.loadCartList(MainActivity.this, new Dialog(MainActivity.this), false,badgeCount,new TextView(MainActivity.this));
                    }else{
                        badgeCount.setVisibility(View.VISIBLE);
                        if(DBqueries.cartList.size()<99) {
                            badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
                        }else{
                            badgeCount.setText("99");
                        }
                    }
                }


                cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (currentUser == null) {
                            signInDialog.show();
                        } else {
                            gotoFragment("My Cart", new MyCartFragment(), CART_FRAGEMENT);
                        }
                    }
                });

            MenuItem notifyItem = menu.findItem(R.id.main_notification_icon);
            notifyItem.setActionView(R.layout.badge_layout);
            ImageView notifyIcon = notifyItem.getActionView().findViewById(R.id.badge_icon);
            notifyIcon.setImageResource(R.drawable.bell);
            TextView notifyCount = notifyItem.getActionView().findViewById(R.id.badge_count);
            if(currentUser!=null){
                DBqueries.checkNotifications(false,notifyCount);
            }
            notifyItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent notificationIntent=new Intent(MainActivity.this,NotificationActivity.class);
                    startActivity(notificationIntent);
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.main_search_icon) {
            Intent intent=new Intent(this, SearchAcitivy.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.main_notification_icon) {
          Intent notificationIntent=new Intent(this,NotificationActivity.class);
          startActivity(notificationIntent);
            return true;
        } else if (id == R.id.main_cart_icon) {  //todo:cart

            if (currentUser == null) {


                signInDialog.show();
            } else {
                gotoFragment("My Cart", new MyCartFragment(), CART_FRAGEMENT);
            }
            return true;

        }else if(id==android.R.id.home){
            if(showCart)
            {
                showCart=false;
                finish();
                return  true;
            }
        }
        return super.onOptionsItemSelected(item);

    }

    private void gotoFragment(String title,Fragment fragment ,int fragmentNo) {
        actionbar_logo.setVisibility(View.GONE);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(title);
        invalidateOptionsMenu();
        setFragment(fragment,fragmentNo);
        if(fragmentNo == CART_FRAGEMENT || showCart) {
            navigationView.getMenu().getItem(3).setChecked(true);
            params.setScrollFlags(0);
        }else{
            params.setScrollFlags(scrollFlags);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    MenuItem menuItem;
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        menuItem = item;



        if(currentUser!=null) {
            drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    int id = menuItem.getItemId();
                    if (id == R.id.main_my_mall) {

                        actionbar_logo.setVisibility(View.VISIBLE);
                        invalidateOptionsMenu();
                        setFragment(new HomeFragment(), HOME_FRAGEMENT);

                    } else if (id == R.id.main_my_orders) {
                        gotoFragment("My Orders", new MyOrdersFragment(), ORDERS_FRAGMENT);
                    } else if (id == R.id.main_my_rewards) {
                        gotoFragment("My Rewards", new MyRewardsFragment(), REWARDS_FRAGEMENT);

                    } else if (id == R.id.main_my_cart) {
                        gotoFragment("My Cart", new MyCartFragment(), CART_FRAGEMENT);
                    } else if (id == R.id.main_my_wishlist) {
                        gotoFragment("My Wishlist", new MyWishlistFragment(), WISHLIST_FRAGMENT);

                    } else if (id == R.id.main_my_account) {
                        gotoFragment("My Account", new MyAccountFragment(), ACCOUNT_FRAGEMENT);

                    } else if (id == R.id.main_my_sign_out) {
                        FirebaseAuth.getInstance().signOut();
                        DBqueries.clearData();
                        Intent registerIntent=new Intent(MainActivity.this,RegisterActivity.class);
                        startActivity(registerIntent);
                        finish();
                    }
                    drawer.removeDrawerListener(this);
                }
            });
            return true;
        }else {
            signInDialog.show();
            return false;
        }

    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
    private void setFragment(Fragment fragment, int fragmentNo) {
        if(fragmentNo!=currentFragment) {
            if(fragmentNo==REWARDS_FRAGEMENT){
                window.setStatusBarColor(Color.parseColor("#4B64EF"));
                toolbar.setBackgroundColor(Color.parseColor("#FF7EBBF1"));

            }else{
                toolbar.setBackgroundColor(Color.parseColor("#FF7EBBF1"));
                toolbar.setBackgroundColor(Color.parseColor("#FF7EBBF1"));

            }
            currentFragment = fragmentNo;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out); // Set the animations here
            fragmentTransaction.replace(R.id.nav_host_fragment_content_main, fragment);
            fragmentTransaction.commit();
        }
    }


}


