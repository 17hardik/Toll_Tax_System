package com.example.toll;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.firebase.client.Firebase;

public class Change_Password extends AppCompatActivity {
    EditText Password, Confirm;
    Button Update;
    ImageView Show, Show2;
    String password, confirm;
    int count = 1, count2 = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Make New Password");
        setContentView(R.layout.activity_change__password);
        Password = findViewById(R.id.password);
        Confirm = findViewById(R.id.confirm);
        Update = findViewById(R.id.update);
        Show = findViewById(R.id.show);
        Show2 = findViewById(R.id.show2);
        Firebase.setAndroidContext(this);
        Show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count%2!=0) {
                    Password.setInputType(InputType.TYPE_CLASS_TEXT);
                    Password.setSelection(Password.getText().length());
                    Show.setImageResource(R.drawable.visibility_off);
                    count++;
                }
                else{
                    Password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    Password.setTypeface(Typeface.SANS_SERIF);
                    Password.setSelection(Password.getText().length());
                    Show.setImageResource(R.drawable.visible);
                    count++;
                }
            }
        });

        Show2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count2%2!=0) {
                    Confirm.setInputType(InputType.TYPE_CLASS_TEXT);
                    Confirm.setSelection(Confirm.getText().length());
                    Show2.setImageResource(R.drawable.visibility_off);
                    count2++;
                }
                else{
                    Confirm.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    Confirm.setTypeface(Typeface.SANS_SERIF);
                    Confirm.setSelection(Confirm.getText().length());
                    Show2.setImageResource(R.drawable.visible);
                    count2++;
                }
            }
        });

        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = Password.getText().toString();
                confirm = Confirm.getText().toString();
                if(password.equals("")){
                    Password.setError("Please enter your new password");
                    Password.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
                }
                else if(confirm.equals("")){
                    Confirm.setError("Please confirm your password");
                    Confirm.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
                }
                else if(!(password.equals(confirm))) {
                    Toast.makeText(Change_Password.this, "Passwords are not matching", Toast.LENGTH_SHORT).show();
                    Confirm.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
                }
                else if(!isInternet()){
                    Toast.makeText(Change_Password.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
                else if(password.length()<6){
                    Password.setError("Password must be at least 5 characters long");
                    Password.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorError)));
                }
                else {
                    final ProgressDialog pd = new ProgressDialog(Change_Password.this);
                    pd.setMessage("Updating Password...");
                    pd.show();
                    Firebase reference = new Firebase("https://toll-79537.firebaseio.com/toll");
                    reference.child(Details.Id).child("Password").setValue(password);
                    pd.dismiss();
                    Toast.makeText(Change_Password.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(Change_Password.this)
                            .setSmallIcon(R.drawable.ic_launcher_transparent)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE)
                            .setContentTitle("Password Updated")
                            .setContentText("Your Toll Tax System password has been updated");
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(1, builder.build());
                    Intent intent = new Intent(Change_Password.this,Login.class);
                    startActivity(intent);
                    finish();
                }
              }
        });

    }
    public  boolean isInternet(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}
