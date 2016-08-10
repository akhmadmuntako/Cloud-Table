package com.example.cloudtable;

import com.example.cloudtable.Model.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 08/08/2016.
 */
public class ApiResponse {
    List<Table> tables = new ArrayList<>();

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }
}
