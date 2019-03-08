package com.jackson_siro.beexpress.db;

public class Utils {

    //DATABASE NAME
    public static final String DATABASE_NAME = "be_express";
    public static final int DATABASE_VERSION = 3;

    //COLUMN DATABASE
    public static final String TABLE_NAME = "as_beexpress";
    public static final String AS_ID = "itemid";
    public static final String AS_TITLE = "title";
    public static final String AS_CODE = "code";
    public static final String AS_CATEGORY = "category";
    public static final String AS_IMAGE = "image";
    public static final String AS_CONTENT = "content";
    public static final String AS_CREATED = "created";

    //CREATE DB SQL
    public static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + " ("
            + AS_ID + " INTEGER PRIMARY KEY, "
            + AS_TITLE + " TEXT, "
            + AS_CODE + " TEXT, "
            + AS_CATEGORY + " TEXT, "
            + AS_IMAGE + " TEXT, "
            + AS_CONTENT + " TEXT ,"
            + AS_CREATED + " TEXT);";
}
