package com.example.toll;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;

public class Bill extends AppCompatActivity {
    TextView VNumber,Price,Date,VType,OName,OEmail,ONumber,NTyre;
    DatabaseReference reff, reff2;
    ProgressDialog pd;
    Button SMS, Mail;
    StringBuilder s;
    String price, message,name,S;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Bill Receipt");
        setContentView(R.layout.activity_bill);
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.show();
        VNumber = findViewById(R.id.Vnumber);
        VType = findViewById(R.id.Vtype);
        Price = findViewById(R.id.price);
        OName = findViewById(R.id.Oname);
        OEmail = findViewById(R.id.Oemail);
        SMS = findViewById(R.id.smsbutton);
        Mail = findViewById(R.id.mailbutton);
        ONumber = findViewById(R.id.Onumber);
        NTyre = findViewById(R.id.Ntyre);
        FirebaseApp.initializeApp(this);

        SMS.setBackgroundResource(R.drawable.button);
        Mail.setBackgroundResource(R.drawable.button);
        Date = findViewById(R.id.date);
        SharedPreferences preferences = getSharedPreferences(S,i);
        name = preferences.getString("Username","No name");
        reff = FirebaseDatabase.getInstance().getReference().child("toll").child(Details.VNumber);
        reff2 = FirebaseDatabase.getInstance().getReference().child("toll").child(name);
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String owner = dataSnapshot.child("Owner").getValue().toString();
                String type = dataSnapshot.child("Type").getValue().toString();
                String phone = dataSnapshot.child("Phone").getValue().toString();
                String email = dataSnapshot.child("Email").getValue().toString();
                String wheels = dataSnapshot.child("Wheels").getValue().toString();

                VNumber.setText(Details.VNumber);
                VType.setText(type);
                ONumber.setText(phone);
                OEmail.setText(email);
                NTyre.setText(wheels);
                OName.setText(owner);
                Details.Owner = owner;
                Details.Email = email;
                Details.Type = type;
                Details.Phone = phone;

                 Calendar d = Calendar.getInstance();
                 int yy = d.get(Calendar.YEAR);
                 int mm = d.get(Calendar.MONTH);
                 int dd = d.get(Calendar.DAY_OF_MONTH);
                 mm = mm+1;
                 if(mm!=12){
                     mm = mm+1;
                 }
                 else{
                     mm = 1;
                     yy = yy+1;
                 }
                 s = new StringBuilder().append(dd).append("-").append(mm).append("-").append(yy);

                if(type.equals("7 or more Axle")){
                    if(Details.Pass_Type.equals("One Way")){
                        price = "265.00";
                    }
                    else if(Details.Pass_Type.equals("Two Way")){
                        price = "400.00";
                    }
                    else{
                        price = "8900.00";
                        Date.setText("Valid Till: "+s);
                    }
                }
               else if(type.equals("LCV")){
                    if(Details.Pass_Type.equals("One Way")){
                        price = "65.00";
                    }
                    else if(Details.Pass_Type.equals("Two Way")){
                        price = "100.00";
                    }
                    else{
                        price = "2225.00";
                        Date.setText("Valid Till: "+s);
                    }
                }
                else if(type.equals("Bus/Truck")){
                    if(Details.Pass_Type.equals("One Way")){
                        price = "140.00";
                    }
                    else if(Details.Pass_Type.equals("Two Way")){
                        price = "210.00";
                    }
                    else{
                        price = "4660.00";
                        Date.setText("Valid Till: "+s);
                    }
                }
                else if(type.equals("Upto 3 Axle Vehicle")){
                    if(Details.Pass_Type.equals("One Way")){
                        price = "155.00";
                    }
                    else if(Details.Pass_Type.equals("Two Way")){
                        price = "230.00";
                    }
                    else{
                        price = "5085.00";
                        Date.setText("Valid Till: "+s);
                    }
                }
                else if(type.equals("4 to 6 Axle")){
                    if(Details.Pass_Type.equals("One Way")){
                        price = "220.00";
                    }
                    else if(Details.Pass_Type.equals("Two Way")){
                        price = "330.00";
                    }
                    else{
                        price = "7310.00";
                        Date.setText("Valid Till: "+s);
                    }
                }
                else if(type.equals("HCM/EME")){
                    if(Details.Pass_Type.equals("One Way")){
                        price = "220.00";
                    }
                    else if(Details.Pass_Type.equals("Two Way")){
                        price = "330.00";
                    }
                    else{
                        price = "7310.00";
                        Date.setText("Valid Till: "+s);
                    }
                }
                else{
                    if(Details.Pass_Type.equals("One Way")){
                        price = "40.00";
                    }
                    else if(Details.Pass_Type.equals("Two Way")){
                        price = "60.00";
                    }
                    else{
                        price = "1375.00";
                        Date.setText("Valid Till: "+s);
                    }
                }
                Price.setText(price);
                if(Details.Pass_Type.equals("Monthly Pass"))
                {
                    Details.Message = "Dear "+Details.Owner+", We ("+name+") have recieved Rs. "+price+" from you. Your monthly pass is valid till "+s;
                }
                else {
                    Details.Message = "Dear " + Details.Owner +", We ("+name+") have recieved Rs. "+price+" from you.";
                }
                pd.dismiss();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reff2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               String id = dataSnapshot.child("Name").getValue().toString();
               name = id;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        SMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS(Details.Phone,Details.Message);
            }
        });

        Mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });
    }
    public void sendSMS(String ph, String msg) {
        try {

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(ph, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
public void sendMail(){
    String recipient = Details.Email;

    String subject = "Bill Reciept";

    if(Details.Pass_Type.equals("Monthly Pass"))
    {
        message = "Dear "+Details.Owner+", We ("+name+") have recieved Rs. "+price+" from you. Your monthly pass is valid till "+s;
    }
    else {
        message = "Dear " + Details.Owner +", We ("+name+") have recieved Rs. "+price+" from you.";
    }

    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
    intent.putExtra(Intent.EXTRA_TEXT, message);
    startActivity(intent);
  }

}
