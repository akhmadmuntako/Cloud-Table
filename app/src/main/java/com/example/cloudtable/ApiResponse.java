package com.example.cloudtable;

import com.example.cloudtable.Database.generator.Tables;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 08/08/2016.
 */
public class ApiResponse {
    List<Tables> tables = new ArrayList<>();

    public List<Tables> getTables() {
        return tables;
    }

    public void setTables(List<Tables> tables) {
        this.tables = tables;
    }
}
