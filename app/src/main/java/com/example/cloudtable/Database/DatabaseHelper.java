package com.example.cloudtable.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.cloudtable.Model.Table;

import java.util.ArrayList;

/**
 * Created by Lenovo on 08/08/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static SQLiteDatabase db;
    //    public static String dbname = "/storage/sdcard0/Handler.db";
    public static final String DATABASE_NAME = "MyDBName.db";
    public static final String TABLE_NAME = "tables";
    public static final String TABLE_COLUMN_ID = "table_id";
    public static final String TABLE_COLUMN_NAME = "table_name";
    public static final String TABLE_COLUMN_LEFT = "table_left";
    public static final String TABLE_COLUMN_TOP = "table_top";
    public static final String TABLE_COLUMN_RIGHT = "table_right";
    public static final String TABLE_COLUMN_BOTTOM = "table_bottom";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    public static void openDatabase() {
        try {
            //db= SQLiteDatabase.openOrCreateDatabase(dbname,Activity.MODE_MULTI_PROCESS,null);
            db = SQLiteDatabase.openDatabase(DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE + SQLiteDatabase.CREATE_IF_NECESSARY);
            Log.d("DataBase open ", "open");
        } catch (SQLiteException ex) {
            //Toast.makeText(this, ex.getMessage(), 1).show();
            Log.d("DataBase open fail", "fail");
        }
    }

    public static void closeDatabase() {
        try {
            Log.d("DB close", "Close");
            // close database
            db.close();
        } catch (Exception ext) {
            ext.printStackTrace();
            Log.d("closeDatabase", "Exception in closing database : " + ext.toString());
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("create table if not exists " + TABLE_NAME + "("
                    + TABLE_COLUMN_ID + " integer PRIMARY KEY , "
                    + TABLE_COLUMN_NAME + " text,"
                    + TABLE_COLUMN_LEFT +" integer,"
                    + TABLE_COLUMN_TOP +" integer,"
                    + TABLE_COLUMN_RIGHT + " integer,"
                    + TABLE_COLUMN_BOTTOM + " integer);");
        } catch (Exception e) {
            Log.d("Create table fail", "table make fail");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertTable(Table table) {
        Integer id = table.getTableID();
        String name = table.getTableName();
        Integer left = table.getTableLeft();
        Integer top = table.getTableTop();
        Integer right = table.getTableRight();
        Integer bottom = table.getTableBottom();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_COLUMN_ID, id);
        contentValues.put(TABLE_COLUMN_NAME, name);
        contentValues.put(TABLE_COLUMN_LEFT, left);
        contentValues.put(TABLE_COLUMN_TOP, top);
        contentValues.put(TABLE_COLUMN_RIGHT,right);
        contentValues.put(TABLE_COLUMN_BOTTOM,bottom);
        db.insert(TABLE_NAME, null, contentValues);
        Log.w("DatabaseHelper", "insert  Table success");
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from tables where table_id=" + id + "", null);
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

    public boolean updateTable(Table table) {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer id = table.getTableID();
        String name = table.getTableName();
        Integer left = table.getTableLeft();
        Integer top = table.getTableTop();
        Integer right = table.getTableRight();
        Integer bottom = table.getTableBottom();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_COLUMN_ID, id);
        contentValues.put(TABLE_COLUMN_NAME, name);
        contentValues.put(TABLE_COLUMN_LEFT, left);
        contentValues.put(TABLE_COLUMN_TOP, top);
        contentValues.put(TABLE_COLUMN_RIGHT,right);
        contentValues.put(TABLE_COLUMN_BOTTOM, bottom);
        db.update(TABLE_NAME, contentValues, "table_id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deleteTable(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public ArrayList<Table> getAllTables() {
        ArrayList<Table> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from tables", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            int id = res.getInt(res.getColumnIndex(TABLE_COLUMN_ID));
            String name = res.getString(res.getColumnIndex(TABLE_COLUMN_NAME));
            int left = res.getInt(res.getColumnIndex(TABLE_COLUMN_LEFT));
            int top = res.getInt(res.getColumnIndex(TABLE_COLUMN_TOP));
            int right = res.getInt(res.getColumnIndex(TABLE_COLUMN_RIGHT));
            int bottom = res.getInt(res.getColumnIndex(TABLE_COLUMN_BOTTOM));
            Table table = new Table(id,name,left,top,right,bottom);
            array_list.add(table);
            res.moveToNext();
        }
        return array_list;
    }

}
