package com.example.peakbrowser;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class MyWebViewClient extends WebViewClient {
    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // 设置在webView点击打开的新网页在当前界面显示,而不跳转到新的浏览器中

        if (url == null) {
            // 返回true自己处理，返回false不处理
            return true;
        }

        // 正常的内容，打开
        if (url.startsWith(HTTP) || url.startsWith(HTTPS)) {
            view.loadUrl(url);
            return true;
        }

        // Removed.
        // 调用第三方应用，防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
//        try {
//            // TODO:弹窗提示用户，允许后再调用
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//            startActivity(intent);
//            return true;
//        }
//        catch (Exception e) {
//            return true;
//        }

        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        // 更新状态文字
        WebActivity.textUrl.setText("加载中...");

        // 切换默认网页图标
        WebActivity.webIcon.setImageResource(R.drawable.internet);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        // 改变标题
//        setTitle(activeWeb.getTitle());
        // 显示页面标题
        WebActivity.textUrl.setText(WebActivity.activeWeb.getTitle());
    }
}
