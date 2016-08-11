package com.example.cloudtable.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.cloudtable.Model.Freegrid;
import com.example.cloudtable.Model.PanningView;
import com.example.cloudtable.Model.PositionProfider;
import com.example.cloudtable.Model.TableView;
import com.example.cloudtable.R;
import com.example.cloudtable.gcm.GcmIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

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
////        android.app.Notification notification = new android.app.Notification(R.drawable.meja,
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
//        builder.setSmallIcon(R.drawable.meja);
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

        List<Rect> rects = new ArrayList<>();
        Rect rect = new Rect(100, 100, 200, 200);
        rects.add(rect);
        rects.add(new Rect(150, 300, 250, 400));

        rects.add(new Rect(300, 100, 800, 200));

        final MyAdapter adapter = new MyAdapter();
        adapter.setRects(rects);

        PanningView scrollView = new PanningView(this);

        final Freegrid freegrid = (Freegrid) findViewById(R.id.a);
        freegrid.setAdapter(adapter);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        View view = findViewById(R.id.main);

        if(view.getParent()!=null)
            ((ViewGroup)view.getParent()).removeView(view);

        layout.addView(view, new LinearLayout.LayoutParams(1280,2400));
        scrollView.addView(layout);
        setContentView(scrollView);
    }
    class MyAdapter extends BaseAdapter implements PositionProfider {

        List<Rect> rects;


        public void setRects(List<Rect> dd) {
            rects = dd;
        }

        @Override
        public int getCount() {
            return rects.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.item, null, false);
            return v;
        }

        @Override
        public TableView getPositionTable(int position) {
            return null;
        }

        @Override
        public Rect getPositionRect(int position) {
            return rects.get(position);
        }
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
