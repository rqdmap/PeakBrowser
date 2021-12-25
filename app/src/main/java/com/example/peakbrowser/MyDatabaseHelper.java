package com.example.peakbrowser;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
//gzp 新增

public  class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_bookmarkDB = "create table bookmarkDB(" +
            "id integer primary key autoincrement," +
            "title text  key,"+
            "url text)";

    //历史记录表
    public static final String CREATE_historyDB = "create table historyDB(" +
            "id integer primary key autoincrement," +
            "title text  key," +
            "url text)";

    private Context mContext;

    //构造方法：
    // 第一个参数Context上下文，
    // 第二个参数数据库名，
    // 第三个参数cursor允许我们在查询数据的时候返回一个自定义的光标位置，一般传入的都是null，
    // 第四个参数表示目前库的版本号（用于对库进行升级）
    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory , int version){
        super(context,name,factory,version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //调用SQLiteDatabase中的execSQL（）执行建表语句。
        db.execSQL(CREATE_bookmarkDB);
        db.execSQL(CREATE_historyDB);
        //创建成功
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //更新表
        db.execSQL("drop table if exists bookmarkDB");
        db.execSQL("drop table if exists historyDB");
        onCreate(db);
    }

}
