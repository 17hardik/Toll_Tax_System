package com.example.toll;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.firebase.client.Firebase;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    EditText vNumber;
    Button submitButton;
    Spinner JType;
    AdView mAdView;
    String number, journey_type, S, id, name;
    int i;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Spinner spinner = findViewById(R.id.j_type);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.jtype, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        vNumber = findViewById(R.id.vnumber);
        JType = findViewById(R.id.j_type);
        submitButton = findViewById(R.id.submit_button);
        Firebase.setAndroidContext(this);
        final int PERMISSION_REQUEST_CODE = 1;

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.SEND_SMS};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }
        }
        submitButton.setBackgroundResource(R.drawable.button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number = vNumber.getText().toString();
                if (number.equals("")) {
                    vNumber.setError("Please enter vehicle number");
                    vNumber.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));

                }
                else if(!number.matches("[A-Z0-9 ]+")){
                    vNumber.setError("Only capital letters and numbers are allowed here");
                    vNumber.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
                }
                else if(journey_type.equals("Select Journey Type")){
                        Toast.makeText(MainActivity.this, "Please select journey type", Toast.LENGTH_SHORT).show();

                }
                else if(!isInternet()){
                    Toast.makeText(MainActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
                else {
                    Details.Pass_Type = journey_type;
                    String url = "https://toll-79537.firebaseio.com/toll.json";
                    final ProgressDialog pd = new ProgressDialog(MainActivity.this);
                    pd.setMessage("Loading...");
                    pd.show();

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            if (s.equals("null")) {
                                Toast.makeText(MainActivity.this, "Number not found", Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    if (!obj.has(number)) {
                                        Toast.makeText(MainActivity.this, "Number not found", Toast.LENGTH_LONG).show();
                                    } else {
                                        Details.VNumber = number;
                                        startActivity(new Intent(MainActivity.this, Bill.class));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            pd.dismiss();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            System.out.println("" + volleyError);
                            pd.dismiss();
                        }
                    });

                    RequestQueue rQueue = Volley.newRequestQueue(MainActivity.this);
                    rQueue.add(request);
                }

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        journey_type = text;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                SharedPreferences.Editor editor = getSharedPreferences(S,i).edit();
                editor.putString("Username","No name");
                editor.apply();
                Intent intent1 = new Intent(MainActivity.this,Login.class);
                startActivity(intent1);
                finish();
                return true;
            case R.id.reg_vehicle:
                Intent intent = new Intent(MainActivity.this, Vehicle_Register.class);
                startActivity(intent);
                return true;
            case R.id.contact_us:
                String recipient = "systemtolltax@gmail.com";
                String subject = "Your Subject";
                String message = "Your Message";
                Intent intent4 = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
                intent4.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
                intent4.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent4.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(intent4);
        return true;
            case R.id.about_us:
                Intent intent2 = new Intent(MainActivity.this, About_Us.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public  boolean isInternet(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}




