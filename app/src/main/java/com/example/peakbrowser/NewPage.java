package com.example.peakbrowser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;



public class NewPage{
    IconView icon;
    Bitmap iconBitmap;
    WebView web;
    int tag;
    String title, url;
    boolean active;

    final static int paddingDP = 8;

    static private LinearLayout.LayoutParams pageIconParam;
    static private FrameLayout.LayoutParams webParam;


    public static void initializeParam(){
        pageIconParam = new LinearLayout.LayoutParams(WebActivity.dp2px(30), WebActivity.dp2px(30));
        pageIconParam.setMargins(WebActivity.dp2px(10), 0, WebActivity.dp2px(10), 0);

        webParam = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
    }


    NewPage(Context mContext, WebActivity that, final int tag){
        // Initialize webView
        web = new WebView(that);
        initWeb();
        web.loadUrl("https://cn.bing.com");
        web.setTag(tag);
        WebActivity.allPages.addView(web, webParam);

        // Initialize webIcon
        icon = new IconView(mContext);
        icon.setLayoutParams(pageIconParam);
        icon.setPadding(WebActivity.dp2px(paddingDP), WebActivity.dp2px(paddingDP),
                WebActivity.dp2px(paddingDP), WebActivity.dp2px(paddingDP));

        icon.setImageResource(R.drawable.internet);
//        icon.setScaleType(ImageView.ScaleType.MATRIX);
        icon.setOnClickListener(new MyImageView.OnClickListener() {
            @Override
            public void onClick(boolean isCancel) {
                if(!active) onActive();
                else if(!isCancel) onActive();
                else if(WebActivity.pages.size() > 1){
                    int tag = (int)icon.getTag();

                    // Update new views.
                    if(tag == 0) WebActivity.pages.get(1).onActive();
                    else WebActivity.pages.get(tag - 1).onActive();

                    // Remove old views and change related information.
                    WebActivity.allIcons.removeView(icon);
                    WebActivity.allPages.removeView(web);
                    WebActivity.pages.remove(tag);
                    for(int i = 0; i < WebActivity.pages.size(); i++)
                        WebActivity.pages.get(i).updateTag(i);
                }
            }
        });
        icon.setTag(tag);
        WebActivity.allIcons.addView(icon);

        // Initialize tag as index in the array.
        this.tag = tag;

        // active onCreated
        active = true;
    }


    public void onActive(){
        // Change active status;
        if(WebActivity.activeWeb != null) WebActivity.pages.get((int)WebActivity.activeWeb.getTag()).active = false;
        active = true;

        // Change webView
        if(WebActivity.activeWeb != null) WebActivity.activeWeb.setVisibility(View.GONE);
        web.setVisibility(View.VISIBLE);
        WebActivity.activeWeb = web;

        // Change webIcon
        if(WebActivity.activeIcon != null) WebActivity.activeIcon.setBackgroundResource(0);
        icon.setBackgroundResource(R.drawable.boarder);
        WebActivity.activeIcon = icon;

        updateTopBar();
    }

    public void updateTopBar(){
        if(iconBitmap != null) WebActivity.webIcon.setImageBitmap(Bitmap.createBitmap(iconBitmap));
        else WebActivity.webIcon.setImageResource(R.drawable.internet);
        WebActivity.textUrl.setText(title);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWeb() {
        // 重写 WebViewClient
        web.setWebViewClient(new MyWebViewClient());
        // 重写 WebChromeClient
        web.setWebChromeClient(new MyWebChromeClient());

        WebSettings settings = web.getSettings();
        // 启用 js 功能
        settings.setJavaScriptEnabled(true);
        // 设置浏览器 UserAgent
        settings.setUserAgentString(settings.getUserAgentString() + " PeakBrowser/" + getVerName(WebActivity.mContext));

        // 将图片调整到适合 WebView 的大小
        settings.setUseWideViewPort(true);
        // 缩放至屏幕的大小
        settings.setLoadWithOverviewMode(true);

        // 支持缩放，默认为true。是下面那个的前提。
        settings.setSupportZoom(true);
        // 设置内置的缩放控件。若为false，则该 WebView 不可缩放
        settings.setBuiltInZoomControls(true);
        // 隐藏原生的缩放控件
        settings.setDisplayZoomControls(false);

        // 缓存
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 设置可以访问文件
        settings.setAllowFileAccess(true);
        // 支持通过JS打开新窗口
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 支持自动加载图片
        settings.setLoadsImagesAutomatically(true);
        // 设置默认编码格式
        settings.setDefaultTextEncodingName("utf-8");
        // 本地存储
        settings.setDomStorageEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON);

        // 资源混合模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return 当前版本名称
     */
    private static String getVerName(Context context) {
        String verName = "unKnow";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    public class IconView extends MyImageView{
        public IconView(Context context) {
            this(context, null);
        }
        public IconView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }
        public IconView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }
    }


    public void updateTag(int tag){
        this.tag = tag;
        web.setTag(tag);
        icon.setTag(tag);
    }
}
