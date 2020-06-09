package com.example.toll;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.Random;

public class Forgot_Password extends AppCompatActivity {
    Button ForgotButton;
    EditText id;
    String number;
    ProgressDialog pd;
    DatabaseReference reff;
    Boolean isOpen = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff0000")));
        setTitle("Forgot Password");
        setContentView(R.layout.activity_forgot__password);
        pd = new ProgressDialog(this);
        pd.setMessage("Sending Message...");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().setStatusBarColor(getResources().getColor(R.color.darkRed,this.getTheme()));
        }
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(getResources().getColor(R.color.darkRed));
        }
        ForgotButton = findViewById(R.id.forgot_button);
        id = findViewById(R.id.userid);
        ForgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOpen = true;
                final String ID = id.getText().toString();
                Details.Id = ID;
                if (ID.equals("")) {
                    id.setError("Please enter your id");
                    id.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
                }
                else if(!isInternet()){
                    Toast.makeText(Forgot_Password.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
                else {
                    pd.show();
                    reff = FirebaseDatabase.getInstance().getReference().child("toll").child(ID);
                    reff.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()&& isOpen) {
                                Toast.makeText(getApplicationContext(), "OTP Sent", Toast.LENGTH_SHORT).show();
                                number = dataSnapshot.child("Phone").getValue().toString();
                                String otp = String.valueOf(gen());
                                Details.OTP = otp;
                                sendSMS(number, "Your OTP for password recovery is " + otp);
                                pd.dismiss();
                                Intent intent = new Intent(Forgot_Password.this, OTP.class);
                                startActivity(intent);
                                isOpen = false;
                                finish();
                            } else {
                                if(isOpen) {
                                    Toast.makeText(Forgot_Password.this, "Id not found", Toast.LENGTH_SHORT).show();
                                }
                                pd.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    public int gen(){
        Random r = new Random(System.currentTimeMillis());
        return ((1+r.nextInt(2))*10000+r.nextInt(10000));
    }
    public void sendSMS(String ph, String msg) {
        try {

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(ph, null, msg, null, null);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
    public  boolean isInternet(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}
