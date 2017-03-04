package photopickersample;

import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;

/**
 * Created by lidong on 2016/1/28.
 */
public class FileUploadManager {

    private static final String ENDPOINT = "http://121.42.187.95/web";

    public interface FileUploadService {
        /**
         * 上传一张图片
         * @param description
         * @param imgs
         * @return
         */
        @Multipart
        @POST("/upload")
        Call<String> uploadImage(@Part("fileName") String description,
                                 @Part("file\"; filename=\"image.png\"") RequestBody imgs);


        /**
         * 上传6张图片
          * @param description
         * @param imgs1
         * @param imgs2
         * @param imgs3
         * @param imgs4
         * @param imgs5
         * @param imgs6
         * @return
         */
        @Multipart
        @POST("/upload")
        Call<String> uploadImage(@Part("description") String description,
                                 @Part("file\"; filename=\"image.png\"") RequestBody imgs1,
                                 @Part("file\"; filename=\"image.png\"") RequestBody imgs2,
                                 @Part("file\"; filename=\"image.png\"") RequestBody imgs3,
                                 @Part("file\"; filename=\"image.png\"") RequestBody imgs4,
                                 @Part("file\"; filename=\"image.png\"") RequestBody imgs5,
                                 @Part("file\"; filename=\"image.png\"") RequestBody imgs6);
    }

    private static final Retrofit sRetrofit = new Retrofit .Builder()
            .baseUrl(ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
//            .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // 使用RxJava作为回调适配器
            .build();


    private static final FileUploadService apiManager = sRetrofit.create(FileUploadService.class);


    /**
     * 发说说
     * @param paths
     * @param desp
     */
    public static void upload(ArrayList<String> paths,String desp){
        RequestBody[] requestBody= new RequestBody[6];
        if (paths.size()>0) {
            for (int i=0;i<paths.size();i++) {
                requestBody[i] =
                        RequestBody.create(MediaType.parse("multipart/form-data"), new File(paths.get(i)));
                System.out.println("lalala2"+requestBody[i]);
            }
        }
        Call<String> call = apiManager.uploadImage( desp,requestBody[0],requestBody[1],requestBody[2],requestBody[3],requestBody[4],requestBody[5]);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                Log.v("Upload", response.message());
                Log.v("Upload", "success");
                System.out.println("lalala成功");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Upload", t.toString());
                System.out.println("lalala" + sRetrofit);
                System.out.println("lalala发送失败");
            }
        });

    }
}
