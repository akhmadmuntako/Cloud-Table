package com.example.cloudtable.Model;

        import java.io.Serializable;

/**
 * Created by Lenovo on 08/08/2016.
 * class to representing table
 */
public class Table implements Serializable{
    String TableName;
    int TableID, TableLeft, TableTop, TableRight, TableBottom;

    public Table( int tableID, String tableName, int tableLeft, int tableTop, int tableRight, int tableBottom) {
        TableName = tableName;
        TableID = tableID;
        TableLeft = tableLeft;
        TableTop = tableTop;
        TableRight = tableRight;
        TableBottom = tableBottom;
    }

    public String getTableName() {
        return TableName;
    }

    public void setTableName(String tableName) {
        TableName = tableName;
    }

    public int getTableID() {
        return TableID;
    }

    public void setTableID(int tableID) {
        TableID = tableID;
    }

    public int getTableLeft() {
        return TableLeft;
    }

    public void setTableLeft(int tableLeft) {
        TableLeft = tableLeft;
    }

    public int getTableTop() {
        return TableTop;
    }

    public void setTableTop(int tableTop) {
        TableTop = tableTop;
    }

    public int getTableRight() {
        return TableRight;
    }

    public void setTableRight(int tableRight) {
        TableRight = tableRight;
    }

    public int getTableBottom() {
        return TableBottom;
    }

    public void setTableBottom(int tableBottom) {
        TableBottom = tableBottom;
    }
}
