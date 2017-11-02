package com.xhy.weibo.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;


import com.xhy.weibo.R;
import com.xhy.weibo.utils.Constants;

import hk.xhy.android.common.ui.WebViewActivity;

/**
 * @author Jianying Li
 */
public class BrowserActivity extends WebViewActivity {

    private String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
//        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_nav_close);
        String url = getIntent().getStringExtra(Constants.EXTRA_URL);
        title = getIntent().getStringExtra(Constants.EXTRA_TITLE);
        this.setTitle(title);
        loadUrl(url);
    }

    @Override
    public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
        if (s.startsWith("mmxueche://update_password/done")) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {

    }

    public void onReceivedTitle(WebView view, String title) {
        if (TextUtils.isEmpty(title)) {
            this.setTitle(title);
        }
    }

    @Override
    public void onBackPressed() {
        if (getWebView().canGoBack()) {
            getWebView().goBack();
            return;
        }
        super.onBackPressed();
    }
}
