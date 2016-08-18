package com.example.cloudtable.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.example.cloudtable.Model.TableView;
import com.example.cloudtable.NetworkServer;
import com.example.cloudtable.R;
import com.example.cloudtable.gcm.GcmIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Lenovo on 09/08/2016.
 */

public class MainActivity extends AppCompatActivity {
    MyAdapter myAdapter;
    static String ip;
    static List<Tables> tables = new ArrayList<>();
    //Creating a broadcast receiver for gcm registration
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    ArrayList<Rect> rects = new ArrayList<>();
    Rect rect;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //meninisiasi ip address dari wifi
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Toast.makeText(MainActivity.this, "your IP sddress is "+ip , Toast.LENGTH_SHORT).show();

        //register GCM
        register();

        //memulai thread server
        new Thread(new NetworkServer()).start();

        myAdapter = new MyAdapter();
        //open or create database
        DaoMaster.DevOpenHelper dev = new DaoMaster.DevOpenHelper(MainActivity.this, "CloudTable.sqlite", null);
        SQLiteDatabase database = dev.getWritableDatabase();
        //create  new table
        DaoMaster.createAllTables(database, true);

        DaoMaster daoMaster = new DaoMaster(database);
        DaoSession daoSession = daoMaster.newSession();
        TablesDao tablesDao = daoSession.getTablesDao();

        //get Array all of Tables from SQLite
        tables = tablesDao.queryBuilder().where(TablesDao.Properties.Table_id.isNotNull()).list();
        try {
            if (!tables.isEmpty()) {
                for (Tables t : tables) {
                    //Generate  Tables as Rect
                    rect = new Rect(t.getTable_left(), t.getTable_top(), t.getTable_right(), t.getTable_bottom());
                    rects.add(rect);
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        myAdapter.setRects(rects);
        PanningView scrollView = new PanningView(this);
        //initiate freegrid
        final Freegrid freegrid = (Freegrid) findViewById(R.id.a);
        freegrid.setAdapter(myAdapter);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        View view = findViewById(R.id.main);

        if (view.getParent() != null)
            ((ViewGroup) view.getParent()).removeView(view);
        //add view to layout with size 1280 x 2400
        layout.addView(view, new LinearLayout.LayoutParams(1280, 2400));
        scrollView.addView(layout);
        setContentView(scrollView);

        //set and handling onItemclick (when table was clicked)
        freegrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Table : " + position, Toast.LENGTH_SHORT).show();
                tableChoosen(position);
            }
        });

        //set and handling onItemlongclick (when table was longclicked)
        freegrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return true;
            }
        });

    }

    /**
     * Adapter class
     */
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

    //method to receive broadcast from GCM intent Service
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

    /**
     * method to push token to server
     * @param token String object that receive from GCM registration
     */
    public void sendToken(final String token) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Map<String, ?> keys = sharedPreferences.getAll();
        //check if  apps has send token to server
        //if apss has send token to server, token will be save in sharedpreference
        if (!keys.containsValue(token)) {
            Log.w("send", "start");
            //initialize api interface to server
            ApiInterface apiInterface = ApiInterface.Interface.buildRetrofitService();
            //start Call with retrofit
            //the call is use POST Method, with parameter device id
            Call<ApiResponse> call = apiInterface.getResponse(token);
            //get callback from server
            call.enqueue(new Callback<ApiResponse>() {
                //if server give respons callback
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    //toast to show response message
                    Toast.makeText(MainActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    Log.w("response", response.message());
                    //call saveregistration method
                    saveRegistrationToken(token);
                }

                //if Call has failed
                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    //toast to show throwable message
                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.w("failure", t.getMessage());
                }
            });
        }

    }

    /**
     * method to handle when table is clicked
     * @param table_id id table that will be sent to server
     */
    public void tableChoosen(int table_id) {
        ApiInterface apiInterface = ApiInterface.Interface.buildRetrofitService();
        //start retrofit call with POST method
        Call<Tables> tablesCall = apiInterface.selectTable(table_id);
        //get response callback from server
        tablesCall.enqueue(new Callback<Tables>() {
            //if server give response
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(Call<Tables> call, Response<Tables> response) {
                Log.w("table click get response", String.valueOf(response.code()));
                if (response.code() == 200) {
                    //call showallertdialog method to show message beside on retrofit response
                    showAlertDialog("table selection success");
                } else {
                    showAlertDialog("table selection failed");
                }
            }

            //if failed to get response
            @Override
            public void onFailure(Call<Tables> call, Throwable t) {
                //toast to  show throwable message
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.w("table click failure", t.getMessage());
                //call sshowallertdialog method
                showAlertDialog("table selection failed");
            }
        });
    }

    /**
     * method to show alert dialog
     * @param mess message that will be showed
     */
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

    /**
     * method to get all tables as list
     * @return tables a list of table
     */
    public static List<Tables> getTables() {
        return tables;
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
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
//        setRegistered(false);
    }

    /**
     * method to get ip address
     * @return ip address
     */
    public static String getIP() {
        return ip;
    }

    /**
     * method to save registration token to Sharedpreference
     * @param token
     */
    private void saveRegistrationToken(final String token) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString("Token", token).apply();
    }
}