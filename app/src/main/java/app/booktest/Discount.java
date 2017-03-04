package app.booktest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static app.booktest.seller_begin2.isNumber;

/**
 * Created by Administrator on 2016/9/4.
 * 折扣界面
 */

public class Discount extends MyActivity {
    private Button getdis,clean;
    private EditText discount;
    String ID = null;
    String dis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discount);
        Intent mIntent = getIntent();
        ID = mIntent.getStringExtra("ID");
        getdis = (Button) findViewById(R.id.uploaddis);
        clean = (Button) findViewById(R.id.clean);
        discount = (EditText) findViewById(R.id.discount);
        getdis.setOnClickListener(listener);
        clean.setOnClickListener(listener);
    }

    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.uploaddis: {
                    GetDiscount();
                    break;
                }
                case R.id.clean:{
                    Clean();
                    break;
                }
                default:
            }
        }
    };
    //获得折扣并传给扫描界面和拍照界面
    private void GetDiscount() {
         dis = discount.getText().toString().trim();
        if(dis.equals("")||!isNumber(dis)) {
            Toast.makeText(this, "请正确输入本次扫描的折扣！", Toast.LENGTH_SHORT).show();
            discount.setText("");
            return;
        }
        else {
            Bundle data = new Bundle();
            data.putString("Discount",dis);
            data.putString("ID",ID);
            Intent intent = new Intent(this, seller_begin2.class);
            intent.putExtras(data);
            startActivityForResult(intent, 0);
            this.finish();
        }
    }

    //清空折扣
    private void Clean() {
        discount.setText("");
    }
}


