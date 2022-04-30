package com.example.test12;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class School extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school);
        Button button1 = (Button) findViewById(R.id.button);
        WebView webView = (WebView) findViewById(R.id.web);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebViewClient(new WebViewClient(){
                @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
                });
                webView.loadUrl("http://www.gdpu.edu.cn");
                }
            });
            }
        }