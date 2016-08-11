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

import com.example.cloudtable.Activity.ListTableActivity;
import com.example.cloudtable.ApiResponse;
import com.example.cloudtable.Database.DatabaseHelper;
import com.example.cloudtable.Database.generator.DaoMaster;
import com.example.cloudtable.Database.generator.DaoSession;
import com.example.cloudtable.Database.generator.Tables;
import com.example.cloudtable.Database.generator.TablesDao;
import com.example.cloudtable.Model.Table;
import com.example.cloudtable.R;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

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
        DaoMaster.DevOpenHelper ex_database_helper_obj = new DaoMaster.DevOpenHelper(getApplicationContext(), "CloudTable.sqlite", null);
        SQLiteDatabase ex_db= ex_database_helper_obj.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster( ex_db);
        DaoSession daoSession = daoMaster.newSession();
        TablesDao tablesDao = daoSession.getTablesDao();
//            handler = new DatabaseHelper(getApplicationContext());
        Gson gson = new Gson();
        ApiResponse api = gson.fromJson(message, ApiResponse.class);
        List<Tables> tables = api.getTables();

        for (Tables t : tables){
            tablesDao.insert(t);
        }

//            JSONObject object = new JSONObject(message);
//            JSONArray array = object.getJSONArray("tables");
//            for (int i = 0; i < array.length(); i++) {
//                JSONObject jsonObjectTable = array.getJSONObject(i);
//                int tableId = jsonObjectTable.getInt("table_id");
//                String tableName = jsonObjectTable.getString("table_name");
//                int tableLeft = jsonObjectTable.getInt("table_left");
//                int tableTop = jsonObjectTable.getInt("table_top");
//                int tableRight = jsonObjectTable.getInt("table_right");
//                int tableBottom = jsonObjectTable.getInt("table_bottom");
//
//                Tables table = new Tables(tableId,tableName,tableLeft,tableTop,tableRight,tableBottom);
////                handler.insertTable(table);// Inserting into DB
//                tablesDao.insert(table);
//            }
        daoSession.clear();
        ex_db.close();
        ex_database_helper_obj.close();

    }


    /**
     * Showing notification with text only
     */
    //This method is generating a notification and displaying the notification
    private void sendNotification(String message) {
        Intent intent = new Intent(this, ListTableActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(MyGcmPushReceiver.this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.meja)
                .setContentText("New message from GCM")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(sound)
                .setVibrate(new long[1000]);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noBuilder.build()); //0 = ID of notification
    }

}
