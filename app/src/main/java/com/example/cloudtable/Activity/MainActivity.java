package com.example.cloudtable.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.cloudtable.Database.DatabaseHelper;
import com.example.cloudtable.Model.PositionProfider;
import com.example.cloudtable.Model.Table;
import com.example.cloudtable.Model.TableView;
import com.example.cloudtable.R;
import com.example.cloudtable.gcm.GcmIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.jraska.console.Console;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
//    String SENDER_ID = "1";
//    private String TAG = MainActivity.class.getSimpleName();
//    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
//    private BroadcastReceiver mRegistrationBroadcastReceiver;
//    Notification myNotication;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        ApiInterface apiInterface = ApiInterface.Interface.buildRetrofitService();
//        Call<ApiResponse> responseCall = apiInterface.tables(1);
//        responseCall.enqueue(new Callback<ApiResponse>() {
//            @Override
//            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response){
//                if(response.code() == 200){
//                    showAlertDialog("table selection success");
//                }else if(response.code() == 400 | response.code() == 500){
//                    showAlertDialog("table selection failed");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse> call, Throwable t) {
//
//            }
//        });
//    }
//
//
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    private void Notification(String notificationTitle, String notificationMessage) {
//
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
////        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
////        android.app.Notification notification = new android.app.Notification(R.drawable.ic_launcher,
////                "A New Message from Dipak Keshariya (Android Developer)!",
////                System.currentTimeMillis());
////
////        Intent notificationIntent = new Intent(this, MainActivity.class);
////        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
////        notification.setLatestEventInfo(AndroidNotifications.this, notificationTitle, notificationMessage, pendingIntent);
////        notificationManager.notify(10001, notification);
//
//        Intent intent = new Intent("com.example.cloudtable.Activity.MainActivity");
//        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 1, intent, 0);
//        Notification.Builder builder = new Notification.Builder(MainActivity.this);
//        builder.setAutoCancel(false);
//        builder.setTicker("this is ticker text");
//        builder.setContentTitle(notificationTitle);
//        builder.setContentText(notificationMessage);
//        builder.setSmallIcon(R.drawable.ic_launcher);
//        builder.setContentIntent(pendingIntent);
//        builder.setOngoing(true);
////        builder.setSubText("This is subtext...");   //API level 16
//        builder.setNumber(100);
//        builder.build();
//
//        myNotication = builder.getNotification();
//        manager.notify(11, myNotication);
//    }
//    public void showAlertDialog(String mess){
//        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder.setMessage(mess);
//        alertDialogBuilder.setTitle("INFO");
//        final AlertDialog alertDialog= alertDialogBuilder.create();
//        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                alertDialog.dismiss();
//            }
//        });
//
//        alertDialog.show();
//    }

    //Creating a broadcast receiver for gcm registration
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initializing our broadcast receiver
        register();

    }

    public void register() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            //When the broadcast received
            //We are sending the broadcast from GCMRegistrationIntentService

            @Override
            public void onReceive(Context context, Intent intent) {
                //If the broadcast has received with success
                //that means device is registered successfully
                if (intent.getAction().equals(GcmIntentService.REGISTRATION_SUCCESS)) {
                    //Getting the registration token from the intent
                    String token = intent.getStringExtra("token");
                    //Displaying the token as toast
                    Toast.makeText(getApplicationContext(), "Registration token:" + token, Toast.LENGTH_LONG).show();

                    //if the intent is not with success then displaying error messages
                } else if (intent.getAction().equals(GcmIntentService.REGISTRATION_ERROR)) {
                    Toast.makeText(getApplicationContext(), "GCM registration error!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        };

        //Checking play service is available or not
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        //if play service is not available
        if (ConnectionResult.SUCCESS != resultCode)

        {
            //If play service is supported but not installed
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //Displaying message that play service is not installed
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());

                //If play service is not supported
                //Displaying an error message
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }

            //If play service is available
        } else

        {
            //Starting intent to register device
            Intent itent = new Intent(this, GcmIntentService.class);
            startService(itent);

        }
    }

    //Registering receiver on activity resume
    @Override
    protected void onResume() {
        super.onResume();
        Log.w("MainActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GcmIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GcmIntentService.REGISTRATION_ERROR));
    }


    //Unregistering receiver on activity paused
    @Override
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }


}
