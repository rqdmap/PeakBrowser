package com.example.peakbrowser;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static com.example.peakbrowser.WebActivity.addinfo_history;


public class MyWebViewClient extends WebViewClient {
    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";
    private static final String FTP = "FTP://";
    

    private boolean if_load;// gzp 新增： 防止重定位造成历史记录多次添加进数据库

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // 设置在webView点击打开的新网页在当前界面显示,而不跳转到新的浏览器中
        if (url == null) {
            // 返回true自己处理，返回false不处理
            return true;
        }

        // 正常的内容，打开
        if (url.startsWith(HTTP) || url.startsWith(HTTPS) || url.startsWith(FTP)) {
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
        if_load=false;//gzp新增
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if_load=true;//gzp 新增
        int id = (int)view.getTag();
        WebActivity.pages.get(id).url = url;
        if(favicon != null) {
            WebActivity.pages.get(id).icon.setImageBitmap(favicon);
            WebActivity.pages.get(id).iconBitmap = favicon;
            if (WebActivity.pages.get(id).active)
                WebActivity.webIcon.setImageBitmap(favicon);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        WebActivity.pages.get((int)view.getTag()).url = url;
        if(if_load) {
            addinfo_history(view.copyBackForwardList().getCurrentItem().getTitle(), view.copyBackForwardList().getCurrentItem().getUrl());
            if_load = false;
        }
    }

}
