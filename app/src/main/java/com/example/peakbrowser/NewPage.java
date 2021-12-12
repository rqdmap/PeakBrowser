package com.example.peakbrowser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
public class NewPage{
    ImageView icon;
    WebView web;
    int tag;

    static private LinearLayout.LayoutParams pageIconParam;
    static private FrameLayout.LayoutParams webParam;

    public static int dp2px(float dpValue) {
        return (int) (0.5f + dpValue * Resources.getSystem().getDisplayMetrics().density);
    }

    public static void initializeParam(){
        pageIconParam = new LinearLayout.LayoutParams(dp2px(30), dp2px(30));
        pageIconParam.setMargins(dp2px(10), 0, dp2px(10), 0);

        webParam = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
    }

    NewPage(WebActivity that, int tag){
        // Initialize webView
        web = new WebView(that);
        initWeb();
        web.loadUrl("https://www.baidu.com/");
        WebActivity.allPages.addView(web, webParam);

        // Initialize webIcon
        icon = new ImageView(that);
        icon.setLayoutParams(pageIconParam);
        icon.setPadding(dp2px(3), dp2px(3),
                dp2px(3), dp2px(3));

        icon.setImageBitmap(web.getFavicon());
        icon.setOnClickListener(that);
        icon.setTag(tag);
        WebActivity.allIcons.addView(icon);

        // Initialize tag as index in the array.
        this.tag = tag;
    }


    public void onActive(){
        // Change webView
        if(WebActivity.activeWeb != null) WebActivity.activeWeb.setVisibility(View.GONE);
        web.setVisibility(View.VISIBLE);
        WebActivity.activeWeb = web;

        // Change webIcon
        if(WebActivity.activeIcon != null) WebActivity.activeIcon.setBackgroundResource(0);
        icon.setImageBitmap(web.getFavicon());
        WebActivity.activeIcon = icon;
        WebActivity.activeIcon.setBackgroundResource(R.drawable.boarder);

        // Change top bar.
        WebActivity.webIcon.setImageBitmap(web.getFavicon());
        WebActivity.textUrl.setText(web.getTitle());
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
        settings.setUserAgentString(settings.getUserAgentString() + " mkBrowser/" + getVerName(WebActivity.mContext));

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
}
