package hk.xhy.android.commom.ui;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import hk.xhy.android.commom.R;

import java.util.Map;

/**
 * Created by xuhaoyang on 2/24/16.
 */
public abstract class WebViewActivity extends BaseActivity {
    private ProgressBar mProgress;
    private WebView mWebView;

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mProgress = (ProgressBar) findViewById(android.R.id.progress);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(mWebChromeClient);
    }

    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        mWebView.loadUrl(url, additionalHttpHeaders);
    }

    public void loadUrl(String url) {
        mWebView.loadUrl(url);
    }

    public abstract void onPageStarted(WebView view, String url, Bitmap favicon);


    public abstract void onPageFinished(WebView view, String url);

    public void onReceivedError(WebView view, int errorCode,
                                String description, String failingUrl) {
    }

    public void onProgressChanged(WebView view, int newProgress) {
        if (mProgress != null) {
            mProgress.setMax(100);
            mProgress.setProgress(newProgress);
            // 如果进度大于或者等于100，则隐藏进度条
            if (newProgress >= 100) {
                mProgress.setVisibility(View.GONE);
            }
        }
    }

    public void onReceivedTitle(WebView view, String title) {
    }

    public WebView getWebView() {
        return mWebView;
    }

    public WebSettings getSettings() {
        return mWebView.getSettings();
    }

    public WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            WebViewActivity.this.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            WebViewActivity.this.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            WebViewActivity.this.onReceivedError(view, errorCode, description, failingUrl);
        }
    };

    public WebChromeClient mWebChromeClient = new WebChromeClient() {
        public void onProgressChanged(WebView view, int newProgress) {
            WebViewActivity.this.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            WebViewActivity.this.onReceivedTitle(view, title);
        }


    };
}