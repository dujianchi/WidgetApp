package cn.dujc.widget.zxing.open;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.LayoutRes;

public interface ICaptureView extends ICaptureHandler, IVew {

    @LayoutRes
    int getViewId();

    void _onPause();

    void _onResume();

    void _onDestroyBefore();

    void _onCreateAfterSetupView();

    Application getApplication();

    /**
     * 处理扫码结果，返回true则需要关闭扫码页
     */
    boolean setResult(int resultCode, Intent data);

    void finish();

//    void startActivity(Intent intent);

}
