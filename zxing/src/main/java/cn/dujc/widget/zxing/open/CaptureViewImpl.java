package cn.dujc.widget.zxing.open;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;

public class CaptureViewImpl extends AbsCaptureViewImpl {

    @NonNull
    private final IVew mIVew;
    @NonNull
    private final ICaptureResult mCaptureResult;

    public CaptureViewImpl(@NonNull Activity activity, @NonNull ICaptureResult captureResult) {
        this(new ActivityImpl(activity), captureResult);
    }

    public CaptureViewImpl(@NonNull IVew iVew, @NonNull ICaptureResult captureResult) {
        mIVew = iVew;
        mCaptureResult = captureResult;
    }

    @Override
    public boolean _setResult(int resultCode, Intent data) {
        if (mIVew.getActivity() != null) {
            mIVew.getActivity().setResult(resultCode, data);
        }
        return false;
    }

    @Override
    public void finish() {
        if (mIVew.getActivity() != null) {
            mIVew.getActivity().finish();
        }
    }

    @Override
    public Application getApplication() {
        if (mIVew.getActivity() != null) {
            return mIVew.getActivity().getApplication();
        }
        return null;
    }

    @Override
    public Activity getActivity() {
        return mIVew.getActivity();
    }

    @Override
    public <T extends View> T findViewById(@IdRes int resId) {
        return mIVew.findViewById(resId);
    }

    @Override
    public boolean handleDecode(String result) {
        return mCaptureResult.handleDecode(result);
    }
}
