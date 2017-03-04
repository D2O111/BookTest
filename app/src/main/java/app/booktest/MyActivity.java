package app.booktest;

import android.app.Activity;
import android.os.Bundle;

import com.pgyersdk.activity.FeedbackActivity;
import com.pgyersdk.crash.PgyCrashManager;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;

/**
 * Created by Administrator on 2016/10/5.
 * 实现摇一摇上传错误。
 */

public class MyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PgyCrashManager.register(this);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // 自定义摇一摇的灵敏度，默认为950，数值越小灵敏度越高。
        PgyFeedbackShakeManager.setShakingThreshold(1000);
        // 以对话框的形式弹出
        //PgyFeedbackShakeManager.register(MyActivity.this);
        // 以Activity的形式打开，这种情况下必须在AndroidManifest.xml配置FeedbackActivity
        // 打开沉浸式,默认为false
        FeedbackActivity.setBarImmersive(true);
        PgyFeedbackShakeManager.register(MyActivity.this, false);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        PgyFeedbackShakeManager.unregister();
    }
}
