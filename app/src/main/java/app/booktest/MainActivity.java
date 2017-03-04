package app.booktest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import HttpUtil.HttpUtil;

import static java.lang.Integer.parseInt;

/**
 * 登录界面+联网检测
 */
public class MainActivity extends MyActivity implements OnClickListener {
    private EditText etName, etPass;
    private Button login;
    private TextView back;
    private Intent mIntent;
    //int code = 0;
    SharedPreferences preferences;
    String shareName;
    String sharePass;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_main);
        findViewById();
        login.setOnClickListener(this);
        back.setOnClickListener(this);
        preferences = getSharedPreferences("login", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
        shareName = preferences.getString("username", "");
        sharePass = preferences.getString("password", "");
        editor = preferences.edit();
        etName.setText(shareName);
        etPass.setText(sharePass);
        //联网检查 需要权限
        ConnectivityManager manger = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manger.getActiveNetworkInfo();
        if(info!=null&&info.isConnected())
        {
        }
        else
        {
            Toast.makeText(MainActivity.this, "联网失败", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("请检查网络连接");
            dialog.setNegativeButton("确定",new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(android.os.Build.VERSION.SDK_INT > 10 ){
                        //3.0以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
                        Log.d("ok1", "success");
                        startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    } else {
                        Log.d("ok2", "success");
                        startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                    }
                }
            });
            dialog.show();
        }
    }

    protected void findViewById() {
        etName = ((EditText) findViewById(R.id.loginaccount));
        etPass = ((EditText) findViewById(R.id.loginpassword));
        login = ((Button) findViewById(R.id.login));
        back = ((TextView) findViewById(R.id.back));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                login();
                break;
            case R.id.back:
                back();
                break;
            default:
        }
    }

    //用户登录
    private void login() {
        String username = etName.getText().toString().trim();
        String password = etPass.getText().toString().trim();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit();
        if (username.equals("")) {
            Toast.makeText(MainActivity.this, "用户名不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.equals("")) {
            Toast.makeText(MainActivity.this, "密码不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject jsonObject = null;
            try {
                jsonObject = query(username, password);
            } catch (Exception e) {
                e.printStackTrace();
            }
        if(jsonObject!=null) {
            try {
                if (jsonObject.get("code").toString().equals("")) {

                } else if (parseInt(jsonObject.get("code").toString()) == 200) {
                    Bundle data = new Bundle();
                    data.putString("ID", jsonObject.getString("userId"));
                    System.out.println("lalala" + data);
                    if (jsonObject.getString("roleId").equals("1")) {
                        mIntent = new Intent(MainActivity.this, Discount.class);
                    }
                    //传ID
                    mIntent.putExtras(data);
                    startActivity(mIntent);
                    this.finish();
                } else if (parseInt(jsonObject.get("code").toString()) == 401) {
                    Toast.makeText(MainActivity.this, "账户密码不能为空", Toast.LENGTH_SHORT).show();
                } else if (parseInt(jsonObject.get("code").toString()) == 400) {
                    Toast.makeText(MainActivity.this, "账户或密码错误", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(MainActivity.this, "无网络！~", Toast.LENGTH_SHORT).show();
        }
    }


    //返回上一层界面
    private void back() {
        finish();
    }

    //验证用户名和密码
    private JSONObject query(String username, String password) throws Exception {
        String queryString = "userName=" + username + "&password=" + password;
        String url = "http://www.91shushu.com/user/appLogin?" + queryString;
        System.out.println("lalala" + url);
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", username);
        map.put("password", password);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(HttpUtil.postRequest(url, map));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}