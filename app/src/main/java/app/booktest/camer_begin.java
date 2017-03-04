package app.booktest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.zxing.activity.CaptureActivity;

import java.net.HttpURLConnection;
import java.net.URL;

import HttpUtil.HttpUtil;

/**
 * 开启相机
 */
public class camer_begin extends MyActivity {

    private Button scanning;
    private TextView result;
    String ID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camer_go);
        Intent mIntent = getIntent();
        ID = mIntent.getStringExtra("ID");
        System.out.println("lalala" + ID);
        scanning = (Button) findViewById(R.id.btn_scanning);
        scanning.setOnClickListener(listener);
    }


    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_scanning: {
                    scanning();
                    break;
                }
            }
        }
    };

    private void scanning() {
        Bundle data = new Bundle();
        data.putString("ID", ID);
        Intent intent = new Intent(this, CaptureActivity.class);
        intent.putExtras(data);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            final String scanResult = bundle.getString("result");
            new Thread() {
                @Override
                public void run() {
                    try {
                        String path = HttpUtil.BASE_URL + "/web/saoma?IBSN=" + scanResult + "&ID=" + ID + "&NUM=" + 1;
                        URL url = new URL(path);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(5000);
                        conn.setRequestMethod("GET");
                        int code = conn.getResponseCode();
                        if (code == 200) {
                            System.out.println("lalala已经成功连接服务器");
                        } else {
                            System.out.println("lalala连接服务器失败");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            scanning();
        }
    }
}
