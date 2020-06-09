package com.example.toll;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

public class Vehicle_Register extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    Button registerButton;
    String owner, vnumber,vehicleType,phone,email,tyres;
    TextView search;
    EditText OName,VNumber,Phone,Email,Tyres;
    Spinner VType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Register Vehicle");
        setContentView(R.layout.activity_vehicle__register);
        Spinner spinner = findViewById(R.id.v_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.vtype, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        OName = findViewById(R.id.name);
        registerButton = findViewById(R.id.register_button);
        search = findViewById(R.id.search);
        Tyres = findViewById(R.id.tyres);
        Email = findViewById(R.id.email);
        Phone= findViewById(R.id.phone);
        VType=findViewById(R.id.v_type);
        VNumber=findViewById(R.id.vnumber);

        Firebase.setAndroidContext(this);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Vehicle_Register.this,MainActivity.class));
            }
        });
        registerButton.setBackgroundResource(R.drawable.button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                owner = OName.getText().toString();
                vnumber = VNumber.getText().toString();
                email = Email.getText().toString();
                phone = Phone.getText().toString();
                tyres = Tyres.getText().toString();

                if(owner.equals("")){
                    OName.setError("Please enter name");
                    OName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
                }
                else if(!owner.matches("[A-Za-z ]+")){
                    OName.setError("Please enter a valid name");
                    OName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
                }
                else if(!vnumber.matches("[A-Z0-9 ]+")){
                    VNumber.setError("Only capital letters and numbers are allowed here");
                    VNumber.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
                }
                else if(vnumber.contains(" ")){
                    VNumber.setError("Space is not allowed here");
                    VNumber.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
                }
                else if(vnumber.equals("")){
                    VNumber.setError("Please enter vehicle number");
                    VNumber.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
                }
                else if(tyres.equals("")){
                    Tyres.setError("Please enter number of tyres");
                    Tyres.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
                }
                else if(email.equals("")){
                    Email.setError("Please enter email");
                    Email.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
                }
                else if(phone.length()!=10){
                    Phone.setError("Please enter a valid phone number");
                    Phone.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
                }
                else if(!(email.contains("@") && email.contains("."))) {
                    Email.setError("Please Enter a valid email");
                    Email.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
                }
                else if(vehicleType.equals("Select Vehicle Type")){
                    Toast.makeText(Vehicle_Register.this, "Please select vehicle type", Toast.LENGTH_SHORT).show();
                }
                else if(!isInternet()){
                    Toast.makeText(Vehicle_Register.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
                else {
                    final ProgressDialog pd = new ProgressDialog(Vehicle_Register.this);
                    pd.setMessage("Loading...");
                    pd.show();


                    String url = "https://toll-79537.firebaseio.com/toll.json";

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                        @Override
                        public void onResponse(String s) {
                            Firebase reference = new Firebase("https://toll-79537.firebaseio.com/toll");

                            if(s.equals("null")) {
                                reference.child(vnumber).child("Phone").setValue(phone);
                                reference.child(vnumber).child("Email").setValue(email);
                                reference.child(vnumber).child("Owner").setValue(owner);
                                reference.child(vnumber).child("Type").setValue(vehicleType);
                                reference.child(vnumber).child("Wheels").setValue(tyres);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(Vehicle_Register.this)
                                        .setSmallIcon(R.drawable.ic_launcher_transparent)
                                        .setDefaults(Notification.DEFAULT_ALL)
                                        .setDefaults(Notification.DEFAULT_SOUND)
                                        .setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE)
                                        .setContentTitle("Registration Successful")
                                        .setContentText("A new vehicle has been registered");
                                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.notify(1, builder.build());
                                Toast.makeText(Vehicle_Register.this, "Registration Successful", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Vehicle_Register.this,MainActivity.class);
                                startActivity(intent);
                            }
                            else {
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    if (!obj.has(vnumber)) {
                                        reference.child(vnumber).child("Phone").setValue(phone);
                                        reference.child(vnumber).child("Email").setValue(email);
                                        reference.child(vnumber).child("Owner").setValue(owner);
                                        reference.child(vnumber).child("Type").setValue(vehicleType);
                                        reference.child(vnumber).child("Wheels").setValue(tyres);
                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(Vehicle_Register.this)
                                                .setSmallIcon(R.drawable.ic_launcher_transparent)
                                                .setDefaults(Notification.DEFAULT_ALL)
                                                .setDefaults(Notification.DEFAULT_SOUND)
                                                .setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE)
                                                .setContentTitle("Registration Successful")
                                                .setContentText("A new vehicle has been registered");
                                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                        notificationManager.notify(1, builder.build());
                                        Toast.makeText(Vehicle_Register.this, "Registration Successful", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(Vehicle_Register.this,MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(Vehicle_Register.this, "Id Already Exists", Toast.LENGTH_LONG).show();
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

                    RequestQueue rQueue = Volley.newRequestQueue(Vehicle_Register.this);
                    rQueue.add(request);
                }
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
            vehicleType = text;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public  boolean isInternet(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
