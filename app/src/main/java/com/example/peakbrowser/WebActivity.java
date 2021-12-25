package com.example.peakbrowser;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Selection;
import android.text.Spannable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebActivity extends AppCompatActivity implements View.OnClickListener {

    public static EditText textUrl;
    public static ImageView webIcon, goBack, goForward, navSet, goHome, btnStart;//WebIcon就是左上角顶上图标

    //Add mething new...
    private ImageView btnAddPage;

    public static ImageView activeIcon;
    public static WebView activeWeb;
    public static LinearLayout allIcons;
    public static FrameLayout allPages;
    public static ArrayList<NewPage> pages = new ArrayList<>();
    //gzp新加menuicon,需要在menu_icon.xml view中find
    public static ImageView history_icon;
    public static ImageView bookmark_icon;

    private long exitTime = 0;
    public static Context mContext;
    private InputMethodManager manager;


    private static final int PRESS_BACK_EXIT_GAP = 2000;

    //gzp
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 防止底部按钮上移
        getWindow().setSoftInputMode
                (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setContentView(R.layout.activity_web);

        mContext = WebActivity.this;
        manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        // Initialize every Views possibly.
        initView();

//        getResources().getStringArray()
    }


    private void initView() {
        textUrl = findViewById(R.id.textUrl);
        webIcon = findViewById(R.id.webIcon);
        btnStart = findViewById(R.id.btnStart);

        goBack = findViewById(R.id.goBack);
        goForward = findViewById(R.id.goForward);
        navSet = findViewById(R.id.navSet);
        goHome = findViewById(R.id.goHome);


        activeWeb = null;
        activeIcon = null;
        allIcons = findViewById(R.id.allIcons);
        allPages = findViewById(R.id.allPages);
        btnAddPage = findViewById(R.id.addNewPage);

        NewPage.initializeParam();
        pages.add(new NewPage(mContext, this, pages.size(),"https://cn.bing.com"));
        pages.get(0).onActive();

        // 绑定按钮点击事件
        btnStart.setOnClickListener(this);
        goBack.setOnClickListener(this);
        goForward.setOnClickListener(this);
        navSet.setOnClickListener(this);
        goHome.setOnClickListener(this);

        // Add new page...
        btnAddPage.setOnClickListener(this);
        textUrl.setOnClickListener(this);



        // 地址输入栏获取与失去焦点处理
        textUrl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (hasFocus) {
                    // 显示网站URL
                    textUrl.setText(pages.get((int)activeWeb.getTag()).url);
                    Selection.selectAll((Spannable)textUrl.getText());

                    // 显示跳转按钮
                    btnStart.setImageResource(R.drawable.go);
                } else {
                    // 显示网站名, 显示网站图标
                    pages.get((int)activeWeb.getTag()).updateTopBar();

                    // 显示刷新按钮
                    btnStart.setImageResource(R.drawable.refresh);
                }
            }
        });

        // 监听键盘回车搜索
        textUrl.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    // 执行搜索
                    btnStart.callOnClick();
                    textUrl.clearFocus();
                }
                return false;
            }
        });
    }


    /**
     * 返回按钮处理
     */
    @Override
    public void onBackPressed() {
        // 能够返回则返回上一页
        if (activeWeb.canGoBack()) {
            activeWeb.goBack();
        } else {
            if ((System.currentTimeMillis() - exitTime) > PRESS_BACK_EXIT_GAP) {
                // 连点两次退出程序
                Toast.makeText(mContext, "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }

        }
    }

    @Override
    public void onClick(View v) {
        // Ensure TWO parallel switch branches.
        switch (v.getId()) {
            // 跳转 或 刷新
            case R.id.btnStart:
                if (textUrl.hasFocus()) {
                    // Remove previous iconBitmap
                    pages.get((int)activeWeb.getTag()).iconBitmap = null;
                    activeIcon.setImageResource(R.drawable.internet);
                    webIcon.setImageResource(R.drawable.internet);


                    // 隐藏软键盘
                    if (manager.isActive()) {
                        manager.hideSoftInputFromWindow(textUrl.getApplicationWindowToken(), 0);
                    }

                    // 地址栏有焦点，是跳转
                    String input = textUrl.getText().toString();
                    if (!isHttpUrl(input)) {
                        // 不是网址，加载搜索引擎处理
                        try {
                            // URL 编码
                            input = URLEncoder.encode(input, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        input = "https://cn.bing.com/search?q=" + input;
                        activeWeb.loadUrl(input);
                    }
                    else{
                        if(input.startsWith("http://") | input.startsWith("https://") |
                                input.startsWith("ftp://"))
                            activeWeb.loadUrl(input);
                        else activeWeb.loadUrl("https://" + input);
                    }

//                    pages.get((int)activeWeb.getTag()).updateTopBar();

                    // 取消掉地址栏的焦点
                    textUrl.setText(activeWeb.getUrl());
                    textUrl.clearFocus();
                } else {
                    // 地址栏没焦点，是刷新
                    activeWeb.reload();
                }
                break;

            // 后退
            case R.id.goBack:
                activeWeb.goBack();
                pages.get((int)activeWeb.getTag()).updateTopBar();
                break;

            // 前进
            case R.id.goForward:
                activeWeb.goForward();
                pages.get((int)activeWeb.getTag()).updateTopBar();
                break;

            // 设置
            case R.id.navSet:
                //设置菜单栏弹窗
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View view = LayoutInflater.from(this).inflate(R.layout.menu_icon, null);
                builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
                builder.setView(view);
                history_icon = view.findViewById(R.id.history_icon);
                bookmark_icon = view.findViewById(R.id.bookmark_icon);
                if(view.getParent() != null) {
                    ((ViewGroup)view.getParent()).removeView(view); // <- fix
                }
                final AlertDialog d = builder.show();

                //history_icon监听
                history_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        d.cancel();
                        Intent hintent = new Intent(WebActivity.this,historyActivity.class);
                        startActivityForResult(hintent,0);
                    }
                });



                break;

            // 主页
            case R.id.goHome:
                activeWeb.loadUrl("https://cn.bing.com");
                pages.get((int)activeWeb.getTag()).updateTopBar();
                break;

            case R.id.addNewPage:
                pages.add(new NewPage(mContext, this, pages.size(),"https://cn.bing.com"));
                pages.get(pages.size() - 1).onActive();
                break;
            case R.id.textUrl:
                Selection.selectAll((Spannable)textUrl.getText());
                break;
            default:
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            activeWeb.getClass().getMethod("onPause").invoke(activeWeb);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            activeWeb.getClass().getMethod("onResume").invoke(activeWeb);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断url的性质
     *
     * @param urls 要勘定的字符串
     * @return true: 合法URL, false: 非法URL，交由搜索引擎
     */
    public static boolean isHttpUrl(String urls) {
        String regex = "((http|https|ftp)://)?[a-zA-Z0-9_]+(\\.[a-zA-Z0-9_]+)+";

        Pattern pat = Pattern.compile(regex.trim());
        Matcher mat = pat.matcher(urls.trim());
        return mat.matches();
    }
    public  static void addinfo_history( String title,String url) {
        //第二个参数是数据库名
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(mContext, "usersDataBase", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("url", url);
        //insert（）方法中第一个参数是表名，第二个参数是表示给表中未指定数据的自动赋值为NULL。第三个参数是一个ContentValues对象
        db.insert("historyDB", null, values);
    }


    public static int dp2px(float dpValue) {
        return (int) (0.5f + dpValue * Resources.getSystem().getDisplayMetrics().density);
    }

    //gzp 新增，用于接收history页面返回的url信息
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0&&resultCode==RESULT_OK){
            Toast.makeText(this,data.getExtras().getString("title"),Toast.LENGTH_SHORT).show();
            pages.add(new NewPage(mContext, this, pages.size(),data.getExtras().getString("url")));
            pages.get(pages.size() - 1).onActive();
        }else if(requestCode==0&&resultCode==RESULT_CANCELED){

            //什么都不干，简单回退
        }
    }
}

