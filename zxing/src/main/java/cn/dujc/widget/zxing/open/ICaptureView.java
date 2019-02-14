package cn.dujc.widget.zxing.open;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.LayoutRes;

public interface ICaptureView extends ICaptureHandler, IVew {

    @LayoutRes
    int _getViewId();

    void _onPause();

    void _onResume();

    void _onDestroyBefore();

    void _onCreateAfterSetupView();

    Application getApplication();

    /**
     * 处理扫码结果，返回true则需要关闭扫码页
     */
    boolean _setResult(int resultCode, Intent data);

    /**
     * 当{@link #_setResult(int, Intent)}返回false，则调用这个方法，这个方法需要自己处理接收结果后的操作
     */
    void onCustomResult();

    void finish();

//    void startActivity(Intent intent);

}
