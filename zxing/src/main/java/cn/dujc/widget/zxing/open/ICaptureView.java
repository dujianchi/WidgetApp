package cn.dujc.widget.zxing.open;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.View;

public interface ICaptureView extends ICaptureHandler {

    @LayoutRes
    int getViewId();

    void _onPause();

    void _onResume();

    void _onDestroyBefore();

    void _onCreateAfterSetupView();

    Application getApplication();

    Activity getActivity();

    <T extends View> T findViewById(@IdRes int resId);

    /**
     * 处理扫码结果，返回true则需要关闭扫码页
     */
    boolean setResult(int resultCode, Intent data);

    void finish();

//    void startActivity(Intent intent);

}
