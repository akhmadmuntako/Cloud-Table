package com.example.cloudtable.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.cloudtable.Activity.ListTableActivity;
import com.example.cloudtable.Activity.MainActivity;
import com.example.cloudtable.Database.DatabaseHelper;
import com.example.cloudtable.Model.Table;
import com.example.cloudtable.R;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Lenovo on 08/08/2016.
 */
public class MyGcmPushReceiver extends GcmListenerService {
    private static final String TAG = MyGcmPushReceiver.class.getSimpleName();
    ArrayList<Table> tables = new ArrayList<>();
    DatabaseHelper handler;

    @Override
    public void onMessageReceived(String s, Bundle bundle) {

        if (!bundle.isEmpty()) {
            saveMessage(bundle.getString("message"));
        }

        //Getting the message from the bundle
        String message = bundle.getString("message");
        //Displaying a notification with the message
        sendNotification(message);
        super.onMessageReceived(s, bundle);
    }

    public void saveMessage(String message){
        try {
            handler = new DatabaseHelper(getApplicationContext());
            JSONObject object = new JSONObject(message);
            JSONArray array = object.getJSONArray("tables");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObjectTable = array.getJSONObject(i);
                int tableId = jsonObjectTable.getInt("table_id");
                String tableName = jsonObjectTable.getString("table_name");
                int tableLeft = jsonObjectTable.getInt("table_left");
                int tableTop = jsonObjectTable.getInt("table_top");
                int tableRight = jsonObjectTable.getInt("table_right");
                int tableeBottom = jsonObjectTable.getInt("table_bottom");

                Table table = new Table(tableId, tableName, tableLeft, tableTop,tableRight,tableeBottom);
                handler.insertTable(table);// Inserting into DB
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Showing notification with text only
     */
    //This method is generating a notification and displaying the notification
    private void sendNotification(String message) {
        Intent intent = new Intent(this, ListTableActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("New message from GCM")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(sound)
                .setVibrate(new long[1000]);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noBuilder.build()); //0 = ID of notification
    }

}
