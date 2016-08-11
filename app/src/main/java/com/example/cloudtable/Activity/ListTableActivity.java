package com.example.cloudtable.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.cloudtable.ApiInterface;
import com.example.cloudtable.ApiResponse;
import com.example.cloudtable.Database.generator.DaoMaster;
import com.example.cloudtable.Database.generator.DaoSession;
import com.example.cloudtable.Database.generator.Tables;
import com.example.cloudtable.Database.generator.TablesDao;
import com.example.cloudtable.Model.Freegrid;
import com.example.cloudtable.Model.PanningView;
import com.example.cloudtable.Model.PositionProfider;
import com.example.cloudtable.Model.Table;
import com.example.cloudtable.Model.TableView;
import com.example.cloudtable.NetworkServer;
import com.example.cloudtable.R;
import com.example.cloudtable.gcm.GcmIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Lenovo on 09/08/2016.
 */
public class ListTableActivity extends AppCompatActivity {
    MyAdapter myAdapter;
    static String ip;
    ArrayList<Table> tables;
    //Creating a broadcast receiver for gcm registration
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        register();

        new Thread(new NetworkServer()).start();

        myAdapter = new MyAdapter();
        DaoMaster.DevOpenHelper dev = new DaoMaster.DevOpenHelper(ListTableActivity.this, "CloudTable.sqlite", null);
        SQLiteDatabase database = dev.getWritableDatabase();
        DaoMaster.createAllTables(database, true);
        database.close();
        dev.close();

        ArrayList<Rect> rects = new ArrayList<>();
        Rect rect = new Rect(100, 100, 400, 400);
        rects.add(rect);
        rects.add(new Rect(100, 500, 400, 800));

        rects.add(new Rect(100, 1000, 400, 1300));

//        DatabaseHelper dataBaseHelper = new DatabaseHelper(this);
//        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
//        dataBaseHelper.onUpgrade(db,1,2);
        DaoMaster.DevOpenHelper ex_database_helper_obj = new DaoMaster.DevOpenHelper(getApplicationContext(), "CloudTable.sqlite", null);
        SQLiteDatabase ex_db = ex_database_helper_obj.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(ex_db);
        DaoSession daoSession = daoMaster.newSession();
        TablesDao tablesDao = daoSession.getTablesDao();
        List<Tables> tables = tablesDao.queryBuilder().where(TablesDao.Properties.Table_id.isNotNull()).list();
        try {
            if (!tables.isEmpty()) {
                for (Tables t : tables) {
                    rect = new Rect(t.getTable_left(), t.getTable_top(), t.getTable_right(), t.getTable_bottom());
//                    rects.add(rect);
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        myAdapter.setRects(rects);
        PanningView scrollView = new PanningView(this);

        final Freegrid freegrid = (Freegrid) findViewById(R.id.a);
        freegrid.setAdapter(myAdapter);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        View view = findViewById(R.id.main);

        if (view.getParent() != null)
            ((ViewGroup) view.getParent()).removeView(view);

        layout.addView(view, new LinearLayout.LayoutParams(1280, 2400));
        scrollView.addView(layout);
        setContentView(scrollView);

        freegrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ListTableActivity.this, "posisi : " + position, Toast.LENGTH_SHORT).show();
                tableChoosen(position);
            }
        });
        freegrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                return true;
            }
        });

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
                    sendToken(token);

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

    public void sendToken(String token) {
        Log.w("send", "start");
        ApiInterface apiInterface = ApiInterface.Interface.buildRetrofitService();
        Call<ApiResponse> call = apiInterface.getResponse(token);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Toast.makeText(ListTableActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                Log.w("response", response.message());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(ListTableActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.w("failure", t.getMessage());
            }
        });

    }

    public void tableChoosen(int table_id) {
        ApiInterface apiInterface = ApiInterface.Interface.buildRetrofitService();
        Call<Tables> tablesCall = apiInterface.selectTable(table_id);
        tablesCall.enqueue(new Callback<Tables>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(Call<Tables> call, Response<Tables> response) {
                Log.w("table click get response", String.valueOf(response.code()));
                if (response.code() == 200) {
                    showAlertDialog("table selection success");
                } else if (response.code() == 403 | response.code() == 500) {
                    showAlertDialog("table selection failed");
                }
            }

            @Override
            public void onFailure(Call<Tables> call, Throwable t) {
                Toast.makeText(ListTableActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.w("table click failure", t.getMessage());
                showAlertDialog("table selection failed");
            }
        });
    }

    public void showAlertDialog(String mess) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(mess);
        alertDialogBuilder.setTitle("INFO");
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialogBuilder.show();
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

    public static String getIP(){
        return ip;
    }


}