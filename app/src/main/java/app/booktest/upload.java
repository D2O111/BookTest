package app.booktest;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.pgyersdk.crash.PgyCrashManager;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static HttpUtil.HttpUtil.postRequest;
import static app.booktest.R.id.bookCount;
import static app.booktest.seller_begin2.isNumber;

/**
 * Created by 周鹤铭 on 2016/7/1.
 * 拍照上传
 *
 */

public class upload extends MyActivity {
    private static final int PHOTO_CAPTURE = 0x11;
    private static String photoPath = "/sdcard/shushuwang/";
    private static String photoName = photoPath + "shushu.jpg";
    Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.jpg"));//第二个参数是临时文件，在后面将会被修改
    private Button photo, sc_photo, backfromscn, backmain;//拍照、下载、返回
    private ImageView img_photo;//显示图片
    private String newName = "shushu.jpg";
    private String uploadFile = "/sdcard/shushuwang/shushu.jpg";
    private String actionUrl = "http://192.168.191.13:8080/UploadPhoto/UploadServlet";
    String ID = null;
    String discount;
    private EditText isbn, bookname, bookprice, booksellprice, bookcount, bookauthor;
    private Spinner bookType;
    List<String> list = new ArrayList<String>();
    private String btype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
        Intent mIntent = getIntent();
        ID = mIntent.getStringExtra("ID");
        discount = mIntent.getStringExtra("Discount");
        photo = (Button) findViewById(R.id.photo);
        sc_photo = (Button) findViewById(R.id.sc_photo);
        backfromscn = (Button) findViewById(R.id.backfromscn);
        backmain = (Button) findViewById(R.id.backmain);
        img_photo = (ImageView) findViewById(R.id.imt_photo);
        isbn = (EditText) findViewById(R.id.isbn);
        bookname = (EditText) findViewById(R.id.bookName);
        bookprice = (EditText) findViewById(R.id.bookoldPrice);
        booksellprice = (EditText) findViewById(R.id.bookPrice);
        bookcount = (EditText) findViewById(bookCount);
        bookauthor = (EditText) findViewById(R.id.bookauthor);
        bookType = (Spinner) findViewById(R.id.bookType);
        sc_photo.setOnClickListener(new Sc_photo());
        photo.setOnClickListener(new Photo());
        backfromscn.setOnClickListener(new BackScn());
        backmain.setOnClickListener(new BackMain());
        //booksellprice.setOnTouchListener(new GetDis());
        booksellprice.setOnClickListener(new GetDis());
        list.add("教材");
        list.add("外语及考试");
        list.add("小说");
        list.add("人文社科");
        list.add("其它");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        bookType.setAdapter(arrayAdapter);
        bookType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                //拿到被选择项的值
                btype = (String) bookType.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        String sdStatus = Environment.getExternalStorageState();
        switch (requestCode) {
            case PHOTO_CAPTURE:
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                    Log.i("内存卡错误", "请检查您的内存卡");
                } else {
                    BitmapFactory.Options op = new BitmapFactory.Options();
                    // 设置图片的大小
                    Bitmap bitMap = BitmapFactory.decodeFile(photoName);
                    int width = bitMap.getWidth();
                    int height = bitMap.getHeight();
                    // 设置想要的大小
                    int newWidth = 480;
                    int newHeight = 640;
                    // 计算缩放比例
                    float scaleWidth = ((float) newWidth) / width;
                    float scaleHeight = ((float) newHeight) / height;
                    // 取得想要缩放的matrix参数
                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth, scaleHeight);
                    // 得到新的图片
                    bitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height, matrix, true);
                    // 防止内存溢出
                    op.inSampleSize = 4; // 这个数字越大,图片大小越小.
                    Bitmap pic = null;
                    pic = BitmapFactory.decodeFile(photoName, op);
                    //获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转
                    int degree = readPictureDegree(uploadFile);
                    img_photo.setImageBitmap(rotaingImageView(degree, pic)); // 这个ImageView是拍照完成后显示图片
                    FileOutputStream b = null;
                    try {
                        b = new FileOutputStream(photoName);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        PgyCrashManager.reportCaughtException(upload.this, e);
                    }
                    if (pic != null) {
                        pic.compress(Bitmap.CompressFormat.JPEG, 50, b);
                    }
                }
                break;
            default:
                return;
        }
    }

    //上传
    class Sc_photo implements View.OnClickListener {
        @Override
        public void onClick(View arg0) {
            dialog();
        }
    }

    //返回扫描界面
    class BackScn implements View.OnClickListener {
        @Override
        public void onClick(View arg0) {
            Bundle data = new Bundle();
            data.putString("ID", ID);
            data.putString("Discount", discount);
            Intent mIntent = new Intent(upload.this, seller_begin2.class);
            mIntent.putExtras(data);
            startActivity(mIntent);
            finish();
        }
    }

    //返回主界面
    class BackMain implements View.OnClickListener {
        @Override
        public void onClick(View arg0) {
            finish();
        }
    }

    //拍照
    class Photo implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            //"/sdcard/AnBo/";
            File file = new File(photoPath);
            if (!file.exists()) { // 检查图片存放的文件夹是否存在
                file.mkdir(); // 不存在的话 创建文件夹
            }
            File photo = new File(photoName);
            imageUri = Uri.fromFile(photo);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 这样就将文件的存储方式和uri指定到了Camera应用中
            startActivityForResult(intent, PHOTO_CAPTURE);
            isbn.setText(null);
            bookname.setText(null);
            bookauthor.setText(null);
            bookprice.setText(null);
            booksellprice.setText(null);
            bookcount.setText(null);
            bookauthor.setText(null);
        }
    }

    //获得折扣
    class GetDis implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (bookprice.getText().toString().trim().equals("") || !isNumber(bookprice.getText().toString().trim())) {
                Toast.makeText(getApplicationContext(), "请正确输入本次扫描的折扣！", Toast.LENGTH_SHORT).show();
                bookprice.setText("");
                return;
            } else {
                double dis = Double.valueOf(discount);
                double s_price0 = Double.valueOf(bookprice.getText().toString().trim());
                double s_price1 = s_price0 * (dis / 10);
                DecimalFormat df = new DecimalFormat("######0.00");
                booksellprice.setText(df.format(s_price1));
            }
        }
    }

    //确定
    protected void dialog() {
        if (IsNull()) {
            AlertDialog.Builder builder = new Builder(upload.this);
            builder.setMessage("确认上架吗？");
            builder.setTitle("提示");
            builder.setPositiveButton("确认", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
                    uploadPhoto();
                }
//                }).start();
//            }
            });
            builder.setNegativeButton("取消", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }else{
            return;
        }
    }

    //上架
    protected void uploadPhoto() {
        String picBase = GetImageStr(uploadFile).replaceAll(" ", "+");
        if (IsNull()) {
            String queryString = "ISBN=" + isbn.getText().toString().trim() +
                    "&bookPrice=" + booksellprice.getText().toString().trim() +
                    "&userId=" + ID +
                    "&bookCount=" + bookcount.getText().toString().trim() +
                    "&title=" + bookname.getText().toString().trim() +
                    "&price=" + bookprice.getText().toString().trim() +
                    "&categoryName=" + btype.trim() +
                    "&author=" + bookauthor.getText().toString().trim() +
                    "&baseImg=" + picBase;
            String url = "http://www.91shushu.com/product/upProduct?" + queryString;
            Map<String, String> map = new HashMap<String, String>();
            map.put("ISBN", isbn.getText().toString().trim());
            map.put("bookPrice", booksellprice.getText().toString().trim());
            map.put("bookCount", bookcount.getText().toString().trim());
            map.put("userId", ID);
            map.put("title", bookname.getText().toString().trim());
            map.put("price", bookprice.getText().toString().trim());
            map.put("categoryNames", btype.trim());
            map.put("author", bookauthor.getText().toString().trim());
            map.put("baseImg", picBase);
            JSONObject jsonObject = null;
            try {
                System.out.println("lalala上架" + url);
                jsonObject = new JSONObject(postRequest(url, map));
                System.out.println("lalala上架" + jsonObject);
                if (Integer.parseInt(jsonObject.get("code").toString()) == 200) {
                    Toast.makeText(this, "上架成功", Toast.LENGTH_SHORT).show();
                    isbn.setText(null);
                    bookname.setText(null);
                    bookauthor.setText(null);
                    bookprice.setText(null);
                    bookcount.setText(null);
                    booksellprice.setText(null);
                    Picasso.with(this).load("www.baidu.com").into(img_photo);
                }
                if (Integer.parseInt(jsonObject.get("code").toString()) == 401)
                    Toast.makeText(this, "该书已经上架，请勿重复", Toast.LENGTH_SHORT).show();

                if (Integer.parseInt(jsonObject.get("code").toString()) == 402)
                    Toast.makeText(this, "ISBN格式错误", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(this, "上架失败", Toast.LENGTH_SHORT).show();
                System.out.println("lalala上架" + jsonObject);
                e.printStackTrace();
                PgyCrashManager.reportCaughtException(upload.this, e);
            }
        }
        else {
            return;
        }
    }

    //将图片转换为base64
    protected String GetImageStr(String imgFilePath) {
        if (imgFilePath == null || imgFilePath == "") {
            Toast.makeText(this, "图片不存在！", Toast.LENGTH_SHORT).show();
        }
        File file = new File(imgFilePath);
        if (!file.exists()) {
            Toast.makeText(this, "图片不存在！", Toast.LENGTH_SHORT).show();
        }
        byte[] data = null;
        try {
            InputStream in = new FileInputStream(imgFilePath);
            data = getByte(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String imageString = Base64.encodeToString(data, Base64.NO_WRAP);
        return imageString;
    }
    protected static byte[] getByte(InputStream in) {
        if (in == null) {
            return null;
        }
        int sumSize = 0;
        List<byte[]> totalBytes = new ArrayList<byte[]>();
        byte[] buffer = new byte[1024];
        int length = -1;
        try {
            while ((length = in.read(buffer)) != -1) {
                sumSize += length;
                byte[] tmp = new byte[length];
                System.arraycopy(buffer, 0, tmp, 0, length);
                totalBytes.add(tmp);
            }
            byte[] data = new byte[sumSize];
            int start = 0;
            for (byte[] tmp : totalBytes) {
                System.arraycopy(tmp, 0, data, start, tmp.length);
                start += tmp.length;
            }
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //判空
    protected boolean IsNull() {
        String picBase = GetImageStr(uploadFile).replaceAll(" ", "+");
        if (isbn.getText().toString().equals("")) {
            Toast.makeText(this, "请输入ISBN！", Toast.LENGTH_SHORT).show();
            return false;
        } else if (bookname.getText().toString().equals("")) {
            Toast.makeText(this, "请输入书名！", Toast.LENGTH_SHORT).show();
            return false;
        } else if (bookprice.getText().toString().equals("")) {
            Toast.makeText(this, "请输入原价并保留两位以内的小数!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (booksellprice.getText().toString().equals("")) {
            Toast.makeText(this, "请输入售价并保留两位以内的小数!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (bookcount.getText().toString().equals("")) {
            Toast.makeText(this, "请输入数量！", Toast.LENGTH_SHORT).show();
            return false;
        } else if (btype.equals("")) {
            Toast.makeText(this, "请输入书籍类别！", Toast.LENGTH_SHORT).show();
            return false;
        } else if (bookauthor.getText().toString().equals("")) {
            Toast.makeText(this, "请输入作者！", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isNumber(bookprice.getText().toString().trim())) {
            Toast.makeText(this, "请输入正确原价并保留两位以内的小数!", Toast.LENGTH_SHORT).show();
            bookprice.setText("");
            return false;
        } else if (!isNumber(booksellprice.getText().toString().trim())) {
            Toast.makeText(this, "请输入正确售价并保留两位以内的小数!", Toast.LENGTH_SHORT).show();
            booksellprice.setText("");
            return false;
        } else if (picBase.equals("")) {
            Toast.makeText(this, "请上传图片！", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Double oprice = Double.parseDouble(bookprice.getText().toString().trim());
            Double sprice = Double.parseDouble(booksellprice.getText().toString().trim());
            if (sprice > oprice) {
                Toast.makeText(this, "售价不得高于原价！", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    /**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    protected int readPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    //旋转图片
    protected Bitmap rotaingImageView(int angle , Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }
}
