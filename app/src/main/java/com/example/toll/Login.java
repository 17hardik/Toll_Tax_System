package com.example.toll;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    TextView register, Forgot;
    EditText Id, password;
    Button loginButton;
    ImageView Show;
    int count = 1,i;
    String user, pass,S;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Login");
        setContentView(R.layout.activity_login);
        register = findViewById(R.id.register);
        Forgot = findViewById(R.id.forgot);
        Id = findViewById(R.id.id);
        Show = findViewById(R.id.show);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        String token = task.getResult().getToken();
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("Token", msg);
                    }
                });
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-1120892018817048/8855443152");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        Forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotIntent = new Intent(Login.this,Forgot_Password.class);
                startActivity(forgotIntent);
                finish();
            }
        });
        Show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count%2!=0) {
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                    password.setSelection(password.getText().length());
                    Show.setImageResource(R.drawable.visibility_off);
                    count++;
                }
                else{
                    password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password.setTypeface(Typeface.SANS_SERIF);
                    password.setSelection(password.getText().length());
                    Show.setImageResource(R.drawable.visible);
                    count++;
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                }
                startActivity(new Intent(Login.this, Register.class));
            }
        });
        loginButton.setBackgroundResource(R.drawable.button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user = Id.getText().toString();
                pass = password.getText().toString();
                StringBuilder sb = new StringBuilder();
                char[] letters = pass.toCharArray();

                for (char ch : letters) {
                    sb.append((byte) ch);
                }

                if(user.equals("")){
                    Id.setError("Please enter id");
                    Id.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
                }
                else if(pass.equals("")){
                    password.setError("Please enter your password");
                    password.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
                }
                else if(!isInternet()){
                    Toast.makeText(Login.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
                else{
                    String url = "https://toll-79537.firebaseio.com/toll.json";
                    final ProgressDialog pd = new ProgressDialog(Login.this, R.style.CustomDialog);
                    pd.setMessage("Logging in...");
                    pd.show();

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                        @Override
                        public void onResponse(String s) {
                            if(s.equals("null")){
                                Toast.makeText(Login.this, "Id not found", Toast.LENGTH_LONG).show();
                            }
                            else{
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    if(!obj.has(user)){
                                        Toast.makeText(Login.this, "Id not found", Toast.LENGTH_LONG).show();
                                    }
                                    else if(obj.getJSONObject(user).getString("Password").equals(pass)){
                                        Details.username = user;
                                        Details.password = pass;
                                        SharedPreferences.Editor editor = getSharedPreferences(S,i).edit();
                                        editor.putString("Username",user);
                                        editor.apply();
                                        startActivity(new Intent(Login.this, MainActivity.class));
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(Login.this, "Incorrect Password", Toast.LENGTH_LONG).show();
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
                            System.out.println("" + volleyError);
                            pd.dismiss();
                        }
                    });

                    RequestQueue rQueue = Volley.newRequestQueue(Login.this);
                    rQueue.add(request);
                }

            }
        });
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
