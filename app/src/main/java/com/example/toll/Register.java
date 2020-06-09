package com.example.toll;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends AppCompatActivity {

    Button registerButton;
    String user, pass,id,confirm,loc,phone;
    TextView login;
    ImageView Show, Show2;
    int count = 1, count2 = 1;
    EditText TName,TId,password,location,Phone;
    EditText TConfirm;
    private RewardedAd rewardedAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Register");
        setContentView(R.layout.activity_register);
        password = findViewById(R.id.password);
        Show = findViewById(R.id.show);
        Show2 = findViewById(R.id.show2);
        registerButton = findViewById(R.id.register_button);
        login = findViewById(R.id.login1);
        location = findViewById(R.id.location);
        TId= findViewById(R.id.id);
        Phone = findViewById(R.id.phone);
        TConfirm=findViewById(R.id.confirm);
        TName=findViewById(R.id.name);
        rewardedAd = new RewardedAd(this,"ca-app-pub-1120892018817048/5463827202");

        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
        Firebase.setAndroidContext(this);
        Show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count%2!=0) {
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                    password.setSelection(password.getText().length());
                    Show.setImageResource(R.drawable.visibility_off);
                }
                else{
                    password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password.setTypeface(Typeface.SANS_SERIF);
                    password.setSelection(password.getText().length());
                    Show.setImageResource(R.drawable.visible);
                }
                count++;
            }
        });

        Show2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count2%2!=0) {
                    TConfirm.setInputType(InputType.TYPE_CLASS_TEXT);
                    TConfirm.setSelection(TConfirm.getText().length());
                    Show2.setImageResource(R.drawable.visibility_off);
                }
                else{
                    TConfirm.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    TConfirm.setTypeface(Typeface.SANS_SERIF);
                    TConfirm.setSelection(TConfirm.getText().length());
                    Show2.setImageResource(R.drawable.visible);
                }
                count2++;
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this,Login.class));
            }
        });
        registerButton.setBackgroundResource(R.drawable.button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rewardedAd.isLoaded()) {
                    Activity activityContext = Register.this;
                    RewardedAdCallback adCallback = new RewardedAdCallback() {
                        @Override
                        public void onRewardedAdOpened() {
                            // Ad opened.
                        }

                        @Override
                        public void onRewardedAdClosed() {
                            reg();
                        }

                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem reward) {
                            Toast.makeText(Register.this, "You have earned 17 points", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onRewardedAdFailedToShow(int errorCode) {
                            reg();
                        }
                    };
                    rewardedAd.show(activityContext, adCallback);
                } else {

                }

            }
          });
    }

    public  boolean isInternet(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void reg(){
            user = TName.getText().toString();
            pass = password.getText().toString();
            id = TId.getText().toString();
            phone = Phone.getText().toString();
            confirm = TConfirm.getText().toString();
            loc = location.getText().toString();

            if(user.equals("")){
                TName.setError("Please enter your name");
                TName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
            }
            else if(pass.equals("")){
                password.setError("Please enter your password");
                password.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
            }
            else if(id.equals("")){
                TId.setError("Please enter your toll id");
                TId.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
            }
            else if(phone.equals("")){
                Phone.setError("Please enter your toll id");
                Phone.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
            }
            else if(confirm.equals("")){
                TConfirm.setError("Please confirm your password");
                TConfirm.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
            }
            else if(loc.equals("")){
                location.setError("Please enter your location");
                location.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
            }
            else if(pass.length()<6){
                password.setError("Password must be at least 5 characters long");
                password.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
            }
            else if(!(pass.equals(confirm))) {
                Toast.makeText(Register.this, "Passwords are not matching", Toast.LENGTH_SHORT).show();
                TConfirm.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
            }
            else if(!isInternet()){
                Toast.makeText(Register.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
            else {
                final ProgressDialog pd = new ProgressDialog(Register.this);
                pd.setMessage("Registering...");
                pd.show();


                String url = "https://toll-79537.firebaseio.com/toll.json";

                StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                    @Override
                    public void onResponse(String s) {
                        Firebase reference = new Firebase("https://toll-79537.firebaseio.com/toll");

                        if(s.equals("null")) {
                            reference.child(id).child("Name").setValue(user);
                            reference.child(id).child("Password").setValue(pass);
                            reference.child(id).child("Location").setValue(loc);
                            reference.child(id).child("Phone").setValue(phone);
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(Register.this)
                                    .setSmallIcon(R.drawable.ic_launcher_transparent)
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setDefaults(Notification.DEFAULT_SOUND)
                                    .setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE)
                                    .setContentTitle("Registration Successful")
                                    .setContentText("Welcome to Toll Tax System");
                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(1, builder.build());
                            Toast.makeText(Register.this, "Registration Successful", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Register.this,Login.class);
                            startActivity(intent);
                        }
                        else {
                            try {
                                JSONObject obj = new JSONObject(s);

                                if (!obj.has(id)) {
                                    reference.child(id).child("Name").setValue(user);
                                    reference.child(id).child("Password").setValue(pass);
                                    reference.child(id).child("Location").setValue(loc);
                                    reference.child(id).child("Phone").setValue(phone);
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(Register.this)
                                            .setSmallIcon(R.drawable.ic_launcher_transparent)
                                            .setDefaults(Notification.DEFAULT_ALL)
                                            .setDefaults(Notification.DEFAULT_SOUND)
                                            .setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE)
                                            .setContentTitle("Registration Successful")
                                            .setContentText("Welcome to Toll Tax System");
                                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    notificationManager.notify(1, builder.build());
                                    Toast.makeText(Register.this, "Registration Successful", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(Register.this,Login.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(Register.this, "Id Already Exists", Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        pd.dismiss();
                    }

                },new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("" + volleyError );
                        pd.dismiss();
                    }
                });

                RequestQueue rQueue = Volley.newRequestQueue(Register.this);
                rQueue.add(request);
            }
        }


}

