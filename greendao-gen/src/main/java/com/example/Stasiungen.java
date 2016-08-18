package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * Created by Lenovo on 16/08/2016.
 */
public class Stasiungen {
    public static void main(String[] arg) throws Exception {
        Schema schema = new Schema(1, "com.example"); //your package name
        createDB(schema);
        try {
            new DaoGenerator().generateAll(schema,"..");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void createDB(Schema schema) {

        //Customer table...
        Entity stasiun = schema.addEntity("Stasiun");
        stasiun.setTableName("STASIUNS");
        stasiun.addStringProperty("id").unique();
        stasiun.addStringProperty("kota");
        stasiun.addStringProperty("stasiun");
        stasiun.addStringProperty("namaTampil");
        stasiun.addStringProperty("namaKota");
    }
}
