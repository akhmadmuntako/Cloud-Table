package com.example.cloudtable.Model;

import android.graphics.Rect;

/**
 * Created by gembong on 6/10/16.
 */
public interface PositionProfider {
    TableView getPositionTable(int position);
    Rect getPositionRect(int position);
}
