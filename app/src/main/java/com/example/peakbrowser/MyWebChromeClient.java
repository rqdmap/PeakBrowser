package com.example.peakbrowser;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class MyWebChromeClient extends WebChromeClient {
    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {//用于接收网页图标
        super.onReceivedIcon(view, icon);

        int id = (int)view.getTag();
        WebActivity.pages.get(id).icon.setImageBitmap(icon);//底部多标签栏
        WebActivity.pages.get(id).iconBitmap = icon;//用于下次回来的时候刷新多标签
        if(WebActivity.pages.get(id).active)
            WebActivity.webIcon.setImageBitmap(icon);//顶部栏的图标
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {//用于接收网页title
        super.onReceivedTitle(view, title);

        int id = (int)view.getTag();
        WebActivity.pages.get(id).title = title;
        if(WebActivity.pages.get(id).active)
            WebActivity.textUrl.setText(title);
    }
}
