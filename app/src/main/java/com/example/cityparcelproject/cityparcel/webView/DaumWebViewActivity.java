package com.example.cityparcelproject.cityparcel.webView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cityparcelproject.R;


public class DaumWebViewActivity extends AppCompatActivity {

    private WebView webView;
    private TextView txt_address, txt_postalCode;
    private Handler handler;
    private Button daumWebViewDone;
    private String url = "http://thecityparcel.com/searchaddress.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daum_web_view);

        txt_postalCode = findViewById(R.id.textview_daumPostalCodeResult);
        txt_address = findViewById(R.id.textview_daumResult);
        webView = (WebView) findViewById(R.id.webView_daum);
        daumWebViewDone = (Button) findViewById(R.id.button_daumWebViewDone);

        // WebView 초기화
        init_webView();

        // 핸들러를 통한 JavaScript 이벤트 반응
        handler = new Handler();

        daumWebViewDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProcessData processData = new ProcessData();
                processData.processData(txt_address.getText().toString(), txt_postalCode.getText().toString());
            }
        });
    }
    public void init_webView() {

        // JavaScript 허용
        webView.getSettings().setJavaScriptEnabled(true);

        // JavaScript의 window.open 허용
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        webView.addJavascriptInterface(new AndroidBridge(), "CityParcelProject");

        // web client 를 chrome 으로 설정
        webView.setWebChromeClient(new WebViewHelper());
        webView.setWebViewClient(new WebViewClient());

        // webview url load. php 파일 주소
        webView.loadUrl("http://thecityparcel.com/searchaddress.php");

    }

    private class WebViewHelper extends WebChromeClient {
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView newWebView = new WebView(DaumWebViewActivity.this);
            WebSettings webSettings = newWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);

            final Dialog dialog = new Dialog(DaumWebViewActivity.this);
            dialog.setContentView(newWebView);

            ViewGroup.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
            dialog.show();
            newWebView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onCloseWindow(WebView window) {
                    dialog.dismiss();
                }
            });

            // WebView Popup에서 내용이 안보이고 빈 화면만 보여 아래 코드 추가
            newWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return false;
                }
            });

            ((WebView.WebViewTransport)resultMsg.obj).setWebView(newWebView);
            resultMsg.sendToTarget();
            return true;

        }
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    txt_postalCode.setText(arg1);
                    txt_address.setText(String.format("%s %s", arg2, arg3));
                    webView.destroy();
                }
            });
        }
    }

    public class ProcessData {
        public void processData(String address, String postalCode) {
            Bundle extra = new Bundle();
            Intent intent = new Intent();
            extra.putString("address", address);
            extra.putString("postalCode", postalCode);
            intent.putExtras(extra);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
