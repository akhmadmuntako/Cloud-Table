package com.example.cloudtable.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.example.cloudtable.Activity.MainActivity;
import com.example.cloudtable.Activity.NotUpdateActivity;
import com.example.cloudtable.ApiResponse;
import com.example.cloudtable.Database.generator.DaoMaster;
import com.example.cloudtable.Database.generator.DaoSession;
import com.example.cloudtable.Database.generator.Tables;
import com.example.cloudtable.Database.generator.TablesDao;
import com.example.cloudtable.R;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 08/08/2016.
 * this class is service to get message or notification from GCM server
 */
public class MyGcmPushReceiver extends GcmListenerService {
    private static final String TAG = MyGcmPushReceiver.class.getSimpleName();
    List<Tables> tables = new ArrayList<>();

    @Override
    public void onMessageReceived(String s, Bundle bundle) {
        //Getting the message from the bundle
        String message = bundle.getString("message");
        //Displaying a notification with the message
        sendNotification(bundle,message);
        super.onMessageReceived(s, bundle);
    }

    /**
     * method to save message notification from server
     * @param message the message from server
     * @return true if save successfully
     */
    public boolean saveMessage(String message){
        //open databse
        DaoMaster.DevOpenHelper ex_database_helper_obj = new DaoMaster.DevOpenHelper(getApplicationContext(), "CloudTable.sqlite", null);
        SQLiteDatabase ex_db= ex_database_helper_obj.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster( ex_db);
        DaoSession daoSession = daoMaster.newSession();
        TablesDao tablesDao = daoSession.getTablesDao();
        //upgrade table
        ex_database_helper_obj.onUpgrade(ex_db,1,2);

        //parse json message
        Gson gson = new Gson();
        try {
            ApiResponse api = gson.fromJson(message, ApiResponse.class);
            tables = api.getTables();

            for (Tables t : tables){
                //insert each Tables to SQLite
                tablesDao.insert(t);
            }
        }catch (Exception e){
            //return false if get exception
            return false;
        }

        //close all
        daoSession.clear();
        ex_db.close();
        ex_database_helper_obj.close();

        return true;
    }


    /**
     * Showing notification and displaying the notification
     * @param bundle message bundle
     * @param message the message data from server
     */
    private void sendNotification(Bundle bundle,String message) {
        //intent to main activity
        Intent ok = new Intent(this, MainActivity.class);
        ok.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent to not no update activity
        Intent bad = new Intent(getApplicationContext(), NotUpdateActivity.class);
        bad.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        bad.putExtra("message",message);

        int requestCode = 0;
        //check if save successed
        Boolean cek = saveMessage(message);
        PendingIntent pendingIntent = null;

        if(cek){
            //if check successful will intent to main activity
            pendingIntent = PendingIntent.getActivity(MyGcmPushReceiver.this, requestCode, ok, PendingIntent.FLAG_ONE_SHOT);
        }else{
            //if check not successful will intent to main activity
            pendingIntent = PendingIntent.getActivity(MyGcmPushReceiver.this, requestCode, bad, PendingIntent.FLAG_ONE_SHOT);
        }
        //set notification builder, to show notification
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.meja)
                .setContentText("New message from GCM")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(sound)
                .setVibrate(new long[] { 1000, 1000});

//        if (!bundle.isEmpty()) {
//            saveMessage(bundle.getString("message"));
//        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noBuilder.build()); //0 = ID of notification
    }


}
