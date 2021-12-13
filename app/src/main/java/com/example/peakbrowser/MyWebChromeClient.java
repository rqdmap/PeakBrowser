package com.example.peakbrowser;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class MyWebChromeClient extends WebChromeClient {
    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        super.onReceivedIcon(view, icon);

        int id = (int)view.getTag();
        WebActivity.pages.get(id).icon.setImageBitmap(icon);
        WebActivity.pages.get(id).iconBitmap = icon;
        if(WebActivity.pages.get(id).active)
            WebActivity.webIcon.setImageBitmap(icon);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);

        int id = (int)view.getTag();
        WebActivity.pages.get(id).title = title;
        if(WebActivity.pages.get(id).active)
            WebActivity.textUrl.setText(title);
    }
}
