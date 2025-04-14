package com.example.newsnow;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class NewsDetailActivity extends AppCompatActivity {
    
    public static final String EXTRA_NEWS_URL = "news_url";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        String newsUrl = getIntent().getStringExtra(EXTRA_NEWS_URL);
        webView = findViewById(R.id.web_view);
        webView.setWebViewClient(new WebViewClient()); // This ensures links open in the WebView
        webView.getSettings().setJavaScriptEnabled(true);
        
        if (newsUrl != null) {
            webView.loadUrl(newsUrl);
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
