package com.example.peakbrowser;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peakbrowser.utils.ScreenSizeUtils;

import java.util.ArrayList;
import java.util.HashMap;

//gzp 新增

public class historyActivity extends AppCompatActivity {
    private ListView lv;
    private MyDatabaseHelper dbHelper;
    private String get_title,get_url;//暂存从数据库得到的title和url
    private int get_id; //暂存从数据库得到的id
    private String titler, url;//按值查找详细信息
    int id; //按值查找id
    private TextView text_title, text_url;//书签详细信息edittext
    private ImageView hgoback;
    private ArrayList arrayList=new ArrayList();//存储index
    private boolean if_exsit;//历史记录是否存在
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        lv=(ListView)findViewById(R.id.history_list);
        hgoback = findViewById(R.id.hback);
        queryinfo();
        //点击函数：
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (if_exsit) {
                    query_by_id(position);
                    Toast.makeText(historyActivity.this, "转入中...", Toast.LENGTH_SHORT).show();
                    //加载该页面
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("title",titler);
                    bundle.putString("url",url);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK,intent);
                    finish();//结束当前界面，退回到调用它的界面

                }

            }
        });
        //长按选择删除
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (if_exsit)
                    dialog_bottom(position);
                return true;
            }
        });
        hgoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("title","简单回退");

                intent.putExtras(bundle);
                setResult(RESULT_CANCELED,intent);
                finish();//结束当前界面，退回到调用它的界面
            }
        });

    }

    //删除函数：
    public void delete(int position){
        dbHelper = new MyDatabaseHelper(this,"usersDataBase",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("historyDB","id=?",new String[] {String.valueOf(arrayList.get(position))});
        db.close();
        Toast.makeText(this,"删除成功",Toast.LENGTH_SHORT).show();
        //删除后清空数组，重新放入数据，刷新UI
        arrayList.clear();
        queryinfo();
    }

    //全部删除
    public void delete_all(){
        dbHelper = new MyDatabaseHelper(this,"usersDataBase",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from historyDB", null);
        while (cursor.moveToNext()) {
            id=cursor.getInt(0);
            db.delete("historyDB","id=?",new String[] {String.valueOf(id)});
        }
        cursor.close();
        db.close();
        Toast.makeText(this,"删除成功",Toast.LENGTH_SHORT).show();
        //删除后清空数组，重新放入数据，刷新UI
        arrayList.clear();
        queryinfo();
    }

    //全部删除对话框
    public void delete_all_dialog(){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("确定全部删除吗？")//设置对话框的标题
                //设置对话框的按钮
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete_all();
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();

    }

    //数据库逆序查询函数：
    public void queryinfo(){
        final ArrayList<HashMap<String, Object>> listItem = new ArrayList <HashMap<String,Object>>();/*在数组中存放数据*/
        //第二个参数是数据库名
        dbHelper = new MyDatabaseHelper(this,"usersDataBase",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //Cursor cursor = db.rawQuery("select * from bookmarkDB", null);
        //查询语句也可以这样写
        Cursor cursor = db.query("historyDB", null, null, null, null, null, "id desc");
        if (cursor != null && cursor.getCount() > 0) {
            if_exsit=true;
            while(cursor.moveToNext()) {
                get_id=cursor.getInt(0);//得到int型的自增变量
                get_title = cursor.getString(1);
                get_url = cursor.getString(2);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("title", get_title);
                map.put("url", get_url);
                arrayList.add(get_id);
                listItem.add(map);
                //new String  数据来源， new int 数据到哪去
                SimpleAdapter mSimpleAdapter = new SimpleAdapter(this,listItem,R.layout.list_item,
                        new String[] {"title","url"},
                        new int[] {R.id.textView2,R.id.textView3});
                lv.setAdapter(mSimpleAdapter);//为ListView绑定适配器
            }
        }
        else {
            if_exsit=false;
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("title", "暂时没有浏览记录");
            map.put("url", "此处是存放您历史记录地方");
            listItem.add(map);
            //new String  数据来源， new int 数据到哪去
            SimpleAdapter mSimpleAdapter = new SimpleAdapter(this,listItem,R.layout.list_item,
                    new String[] {"title","url"},
                    new int[] {R.id.textView2,R.id.textView3});
            lv.setAdapter(mSimpleAdapter);//为ListView绑定适配器
        }

        cursor.close();
        db.close();
    }

    //按id查找，返回url
    public String query_by_id(int position){
        dbHelper = new MyDatabaseHelper(this,"usersDataBase",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from historyDB", null);
        while (cursor.moveToNext()) {
            url = cursor.getString(2);
            titler=cursor.getString(1);
            id=cursor.getInt(0);
            //找到id相等的就返回url
            if (arrayList.get(position).equals(id))
                break;
        }
        cursor.close();
        db.close();
        return url;
    }

    //url详细信息对话框
    public void url_infomation(final int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.history_url_information, null);

//        final Dialog dialog = new Dialog(this, R.style.NormalDialogStyle);
//        View view = View.inflate(this, R.layout.history_url_information, null);
        text_title = (TextView) view.findViewById(R.id.textView4);
        text_url = (TextView) view.findViewById(R.id.textView5);
//        dialog.setContentView(view);
//        //使得点击对话框外部可消失对话框
//        dialog.setCanceledOnTouchOutside(true);
//        //设置对话框的大小
//        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
//        Window dialogWindow = dialog.getWindow();
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.gravity = Gravity.CENTER;
//        dialogWindow.setAttributes(lp);
        //显示详细信息
        text_url.setText(query_by_id(position));//这个必须在前面
        text_title.setText(titler);


        builder.setTitle("详细信息").setPositiveButton("转入", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(historyActivity.this, "转入中...", Toast.LENGTH_SHORT).show();
                //加载该页面
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("title",titler);
                bundle.putString("url",url);
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finish();//结束当前界面，退回到调用它的界面
            }
        }).setNegativeButton("取消" ,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(historyActivity.this, "已取消", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setView(view).show();

    }

    //底部对话框
    public void dialog_bottom(final int position){
        //弹出对话框
        final Dialog dialog = new Dialog(this, R.style.NormalDialogStyle);
        View view = View.inflate(this, R.layout.history_dialog_botton, null);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        //view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.9f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(lp);
        dialog.show();

        //点击事件
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消对话框
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除制定position，并取消对话框
                delete(position);
                dialog.dismiss();

            }
        });
        view.findViewById(R.id.lookup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查看
                dialog.dismiss();
                url_infomation(position);
            }
        });
        view.findViewById(R.id.clear_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //全部删除
                dialog.dismiss();
                delete_all_dialog();

            }
        });
    }

    private double getScreenHeight() {
        return 5.3;
    }

    private double getScreenWidth() {
        return 1000.3;
    }


}
