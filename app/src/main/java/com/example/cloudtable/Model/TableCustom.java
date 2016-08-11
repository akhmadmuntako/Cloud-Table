package com.example.cloudtable.Model;

import java.io.Serializable;

/**
 * Created by Lenovo on 10/08/2016.
 */
public class TableCustom implements Serializable {
    private int tableId;
    private String tableName;
    private int tableX, tableY;

    public TableCustom(int tableId, String tableName, int tableX, int tableY) {
        this.tableId = tableId;
        this.tableName = tableName;
        this.tableX = tableX;
        this.tableY = tableY;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getTableX() {
        return tableX;
    }

    public void setTableX(int tableX) {
        this.tableX = tableX;
    }

    public int getTableY() {
        return tableY;
    }

    public void setTableY(int tableY) {
        this.tableY = tableY;
    }
}
