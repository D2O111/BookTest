package app.booktest;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.app.AlertDialog;

import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2016/5/31.
 * 程序主界面，webview实现。
 */
public class welcom extends MyActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private String errorHtml = "";
    public WebView webview;
    public Button button;
    String url = "http://www.91shushu.com/phone/index";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.welcome_layout);
        InputStream inputStream = getResources().openRawResource(R.raw.html1);
        errorHtml = GetString(inputStream);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;    //得到宽度
        int height = dm.heightPixels;  //得到高度
        webview = (WebView) findViewById(R.id.web);
        WebSettings settings = webview.getSettings();
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) webview.getLayoutParams();
        linearParams.height = height - 200;
        webview.setLayoutParams(linearParams);
        button = (Button) findViewById(R.id.button_sale);
        webview.getSettings().setJavaScriptEnabled(true);// 开启Javascript支持
        webview.getSettings().setLoadsImagesAutomatically(true);// 设置可以自动加载图片
        webview.setHorizontalScrollBarEnabled(true);//设置水平滚动条
        webview.setVerticalScrollBarEnabled(true);//设置竖直滚动条
        webview.getSettings().setDefaultTextEncodingName("UTF-8");  //设置默认的显示编码
        settings.setUseWideViewPort(true);//设置加载进来的页面自适应手机屏幕
        settings.setLoadWithOverviewMode(true);//设置加载进来的页面自适应手机屏幕
        webview.loadUrl(url);
        Log.i(TAG, "--onCreate--");
        //设置web视图的客户端
        webview.setWebViewClient(new MyWebViewClient());
        button.setOnClickListener(listener);

        PgyUpdateManager.register(welcom.this,
                new UpdateManagerListener() {
                    @Override
                    public void onUpdateAvailable(final String result) {
                        // 将新版本信息封装到AppBean中
                        final AppBean appBean = getAppBeanFromString(result);
                        new AlertDialog.Builder(welcom.this)
                                .setTitle("更新")
                                .setMessage("应用已经更新，快来体验一下！")
                                .setNegativeButton(
                                        "确定",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                startDownloadTask(welcom.this,appBean.getDownloadURL());
                                            }
                                        }).show();
                    }
                    @Override
                    public void onNoUpdateAvailable() {
                    }
                });
    }

    //网页一级一级返回
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断是否可以返回操作
        if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onResume() {
        super.onResume();
        Log.i(TAG, "--onResume()--");
    }

    private class MyWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i(TAG, "-MyWebViewClient->shouldOverrideUrlLoading()--");
            view.loadUrl(url);
            return true;
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.i(TAG, "-MyWebViewClient->onPageStarted()--");
            super.onPageStarted(view, url, favicon);
        }

        public void onPageFinished(WebView view, String url) {
            Log.i(TAG, "-MyWebViewClient->onPageFinished()--");
            super.onPageFinished(view, url);
        }

        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

            Log.i(TAG, "-MyWebViewClient->onReceivedError()--\n errorCode=" + errorCode + " \ndescription=" + description + " \nfailingUrl=" + failingUrl);
            //这里进行无网络或错误处理，具体可以根据errorCode的值进行判断，做跟详细的处理。
            view.loadDataWithBaseURL(null, errorHtml, "text/html", "UTF-8", null);//使中文不乱码
        }
    }

    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_sale: {
                    scannig();
                    break;
                }
            }
        }
    };

    public void scannig() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //读取网址
    public String GetString(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
