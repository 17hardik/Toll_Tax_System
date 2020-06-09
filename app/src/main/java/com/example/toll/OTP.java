package com.example.toll;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OTP extends AppCompatActivity {
    Button Verify;
    EditText OTP;
    DatabaseReference reff;
    TextView Confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Verify OTP");
        setContentView(R.layout.activity_otp);
        Verify = findViewById(R.id.verify_button);
        OTP = findViewById(R.id.otp);
        Confirm = findViewById(R.id.confirm);
        reff = FirebaseDatabase.getInstance().getReference().child("toll").child(Details.Id);
        Verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = OTP.getText().toString();
                if (otp.equals("")) {
                    OTP.setError("Please enter your OTP");
                    OTP.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
                }
                else if(!isInternet()){
                    Toast.makeText(OTP.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
                else {
                    final ProgressDialog pd = new ProgressDialog(OTP.this);
                    pd.setMessage("Verifying OTP...");
                    pd.show();
                    if (otp.equals(Details.OTP)) {
                        pd.dismiss();
                        finish();
                       Intent intent = new Intent(OTP.this,Change_Password.class);
                       startActivity(intent);
                    } else {
                        pd.dismiss();
                        Toast.makeText(OTP.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    public  boolean isInternet(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


}
