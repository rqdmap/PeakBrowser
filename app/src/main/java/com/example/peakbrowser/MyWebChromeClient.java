package com.example.peakbrowser;

import android.graphics.Bitmap;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class MyWebChromeClient extends WebChromeClient {
    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        super.onReceivedIcon(view, icon);

        // 改变图标
        WebActivity.activeIcon.setImageBitmap(icon);
        WebActivity.webIcon.setImageBitmap(icon);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);

        // 显示页面标题
//        setTitle(title); ai
        WebActivity.textUrl.setText(title);
    }
}
