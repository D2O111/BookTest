package app.booktest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pgyersdk.crash.PgyCrashManager;
import com.squareup.picasso.Picasso;
import com.zxing.activity.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import HttpUtil.HttpUtil;

/**
 * 扫描上传
 */
public class seller_begin2 extends MyActivity {
    private Button enter,scanning,gointo,back,Upload;
    private TextView result,bookauthor;
    private EditText bookprice,bookname,bookCount,oldPrice;
    private ImageView img;
    private Spinner bookType;
    String ID = null;
    String read_result = null;
    String imageurl;
    String booktitle = "";
    String discount;
    int count;
    double o_price;
    double s_price;
    String bookUpPrice;
    String arr[] = {"教材"};
    String bookType_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seller_camer2);
        Intent mIntent = getIntent();
        ID = mIntent.getStringExtra("ID");
        discount = mIntent.getStringExtra("Discount");
        scanning = (Button) findViewById(R.id.btn_scanning);
        Upload = (Button) findViewById(R.id.upload);
        back = (Button) findViewById(R.id.backfrom);
        gointo = (Button) findViewById(R.id.gointo);
        bookauthor = (TextView) findViewById(R.id.bookauthor);
        bookname = (EditText) findViewById(R.id.bookName);
        bookprice = (EditText) findViewById(R.id.bookPrice);
        oldPrice = (EditText) findViewById(R.id.bookoldPrice);
        enter = (Button) findViewById(R.id.enter);
        result = (TextView) findViewById(R.id.result);
        bookType = (Spinner) findViewById(R.id.bookType);
        bookCount = (EditText) findViewById(R.id.bookCount);
        img = (ImageView) findViewById(R.id.img);
        scanning.setOnClickListener(listener);
        Upload.setOnClickListener(listener);
        enter.setOnClickListener(listener);
        gointo.setOnClickListener(listener);
        back.setOnClickListener(listener);
        bookType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                //拿到被选择项的值
                bookType_data = (String) bookType.getSelectedItem();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_scanning: {
                    scanning();
                    break;
                }
                case R.id.enter: {
                    try {
                        enter();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case R.id.gointo: {
                    gointo();
                    break;
                }
                case R.id.backfrom: {
                    backfrom();
                    break;
                }
                case R.id.upload: {
                    upload();
                    break;
                }
                default:
            }
        }
    };

    //扫码
    private void scanning() {
        Upload.setVisibility(Upload.GONE);//拍照键消失
        Bundle data = new Bundle();
        data.putString("ID", ID);
        Intent intent = new Intent(this, CaptureActivity.class);
        intent.putExtras(data);
        startActivityForResult(intent, 0);
        bookname.setText(null);
        bookauthor.setText(null);
        bookprice.setText(null);
        bookCount.setText(null);
        oldPrice.setText(null);
        Picasso.with(this).load("www.baidu.com").into(img);
    }

    //确定
    private void enter() throws JSONException {
        //上传URL
        String url = "http://www.91shushu.com/product/getAppImgUrl?" +
                "userId=" + ID +
                "&" +
                "ISBN=" + read_result;
        Map<String, String> map = new HashMap<String, String>();
        map.put("ISBN", ID);
        map.put("ISBN", read_result);
        System.out.println("lalala扫码" + url);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(HttpUtil.postRequest(url, map));

            if (Integer.parseInt(jsonObject.get("code").toString()) == 400) {
                Toast.makeText(this, "ISBN号为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Integer.parseInt(jsonObject.get("code").toString()) == 402) {
                Toast.makeText(this, "ISBN格式错误", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Integer.parseInt(jsonObject.get("code").toString()) == 401) {
                Toast.makeText(this, "该书已经上架，请勿重复", Toast.LENGTH_SHORT).show();
                bookname.setText(null);
                bookauthor.setText(null);
                bookprice.setText(null);
                bookCount.setText(null);
                oldPrice.setText(null);
                return;
            }

            //加载图片
            imageurl = jsonObject.getString("imgUrl");
            //System.out.println("lalalaima" + imageurl);
            Picasso.with(this).load(imageurl).into(img);


            if (jsonObject.getString("title").equals("")) {
                Toast.makeText(this, "该书信息尚未完善,请选择拍照上传", Toast.LENGTH_SHORT).show();
                Picasso.with(this).load(imageurl).into(img);
                Upload.setVisibility(Upload.VISIBLE);//没有信息，选择拍照。
            }

            else {
                bookname.setText(jsonObject.getString("title"));
                booktitle = bookname.getText().toString().trim();
                bookauthor.setText(jsonObject.getString("author"));
                oldPrice.setText(jsonObject.get("price").toString());
                //折扣
                double dis = Double.valueOf(discount);
                double s_price0 = Double.valueOf(jsonObject.get("price").toString());
                double s_price1 = s_price0 * (dis/10);
                DecimalFormat df = new DecimalFormat("######0.00");
                bookprice.setText(df.format(s_price1));
                if (jsonObject.getString("price").equals("")) {
                    o_price = 9999.9999;
                } else {
                    bookUpPrice = jsonObject.getString("price");
                    o_price = Double.parseDouble(jsonObject.get("price").toString());
                }
                arr = jsonObject.getString("categoryNames").split("\",\"|\\[\"|\"\\]");
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr);
                bookType.setAdapter(arrayAdapter);
                count = Integer.parseInt(jsonObject.get("bookCount").toString());
                bookCount.setText(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            PgyCrashManager.reportCaughtException(seller_begin2.this, e);
        }
    }

    //上架
    private void gointo() {
        String bp = bookprice.getText().toString().trim();
        String bc = bookCount.getText().toString().trim();
        String bop = oldPrice.getText().toString().trim();
        if(result.getText().toString().equals(""))
        {
            Toast.makeText(this, "请输入ISBN", Toast.LENGTH_SHORT).show();
            return;
        }
        if (bp.equals("") || (!isNumber(bp))) {
            Toast.makeText(this, "请输入价格并保留两位以内的小数!", Toast.LENGTH_SHORT).show();
            return;
        }
        s_price = Double.parseDouble(bp);
        if (o_price == 9999.9999) {
//            System.out.println("lalala" + o_price);
//            System.out.println("lalala" + bop);
            if (bop.equals("") || bop.equals(null) || (!isNumeric(bop))) {
                Toast.makeText(this, "请正确输入售价！", Toast.LENGTH_SHORT).show();
                return;
            }
            bookUpPrice = oldPrice.getText().toString().trim();
            o_price = Double.parseDouble(oldPrice.getText().toString().trim());
        }
        if (s_price > o_price) {
            Toast.makeText(this, "售价不得高于原价!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (bc.equals("") || (!isNumeric(bc))) {
            Toast.makeText(this, "请输入合理数量!", Toast.LENGTH_SHORT).show();
            return;
        }
        int b = IntWrapper(bookCount.getText().toString(), 0);
        if (b > count) {
            Toast.makeText(this, "单次上传书籍不得超过" + count + "", Toast.LENGTH_SHORT).show();
            return;
        }

        if (booktitle.equals("") || booktitle.equals(null)) {
            if (bookname.getText().toString().trim().equals(null) ||
                    bookname.getText().toString().trim().equals("")) {
                Toast.makeText(this, "请输入书籍名称", Toast.LENGTH_SHORT).show();
                return;
            }
            booktitle = bookname.getText().toString().trim();
        }
        if (bookType_data.equals("")){
            Toast.makeText(this, "请选择书籍类型!", Toast.LENGTH_SHORT).show();
            return;
        }
        String queryString = "ISBN=" + read_result +
                "&bookPrice=" + bookprice.getText().toString().trim() +
                "&userId=" + ID +
                "&bookCount=" +  bookCount.getText().toString().trim() +
                "&title=" + bookname.getText().toString().replaceAll(" ","") +
                "&price=" + bookUpPrice+
                "&categoryName="+bookType_data;

        String url = "http://www.91shushu.com/product/upProduct?" + queryString;
        Map<String, String> map = new HashMap<String, String>();
        map.put("ISBN", read_result);
        map.put("bookPrice", bookprice.getText().toString().trim());
        map.put("bookCount", bookCount.getText().toString().trim());
        map.put("userId", ID);
        map.put("title", booktitle);
        map.put("price", bookUpPrice);
        map.put("categoryNames", bookType_data);
        JSONObject jsonObject = null;
        try {
            System.out.println("lalala上架" + url);
            jsonObject = new JSONObject(HttpUtil.postRequest(url, map));
            if (Integer.parseInt(jsonObject.get("code").toString()) == 200) {
                Toast.makeText(this, "上架成功", Toast.LENGTH_SHORT).show();
                bookname.setText(null);
                bookauthor.setText(null);
                bookprice.setText(null);
                bookCount.setText(null);
                oldPrice.setText(null);
                Picasso.with(this).load("www.baidu.com").into(img);
            }
             if (Integer.parseInt(jsonObject.get("code").toString()) == 401)
                Toast.makeText(this, "该书已经上架，请勿重复", Toast.LENGTH_SHORT).show();

             if (Integer.parseInt(jsonObject.get("code").toString()) == 402)
                Toast.makeText(this, "ISBN格式错误", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "上架失败", Toast.LENGTH_SHORT).show();
            PgyCrashManager.reportCaughtException(seller_begin2.this, e);
        }
    }

    private void backfrom() {
        this.finish();
    }

    private void upload() {
        Bundle data = new Bundle();
        data.putString("ID", ID);
        data.putString("Discount",discount);
        Intent mIntent = new Intent(seller_begin2.this, upload.class);
        mIntent.putExtras(data);
        startActivity(mIntent);
        this.finish();
    }

    //判断是否为数字
    public static boolean isNumber(String str) {
        String string[] = str.split("\\.");
        if (string.length > 2)
            return false;
        if (string.length == 2) {
            if (!isNumeric2(string[1]))
                return false;
            if (string[1].length() > 2)
                return false;
        }
        if (!isNumeric(string[0]))
            return false;
        return true;
    }

    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(0) == '0')
                return false;
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumeric2(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static int IntWrapper(String s, int as) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return as;
        }

    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            final String scanResult = bundle.getString("result");
            result.setText(scanResult);
            read_result = scanResult;
        }
    }
}
