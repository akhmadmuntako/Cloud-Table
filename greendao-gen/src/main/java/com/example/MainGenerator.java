package com.example;


import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class MainGenerator {

    public static void main(String[] arg) throws Exception {
        Schema schema = new Schema(3, "com.example"); //your package name
        createDB(schema);
        try {
            new DaoGenerator().generateAll(schema,"..");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        new DaoGenerator().generateAll(schema, "../greendao-gen"); //where you want to store the generated classes.
    }
    private static void createDB(Schema schema) {

        //Customer table...
        Entity tables = schema.addEntity("Tables");
        tables.setTableName("TABLES");
        tables.addIntProperty("table_id").unique();
        tables.addStringProperty("table_name");
        tables.addIntProperty("table_left");
        tables.addIntProperty("table_top");
        tables.addIntProperty("table_right");
        tables.addIntProperty("table_bottom");
    }
}
