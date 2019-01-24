package cn.dujc.widget.zxing.open;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;

public class CaptureViewImpl extends AbsCaptureViewImpl {

    private final Activity mActivity;
    private final Fragment mFragment;
    private final ICaptureResult mCaptureResult;

    public CaptureViewImpl(Activity activity, Fragment fragment, ICaptureResult captureResult) {
        mActivity = activity;
        mFragment = fragment;
        mCaptureResult = captureResult;
    }

    @Override
    public boolean setResult(int resultCode, Intent data) {
        if (mActivity != null) {
            mActivity.setResult(resultCode, data);
        }
        return false;
    }

    @Override
    public void finish() {
        System.out.println("--------------   " + mActivity + " is finishing");
        if (mActivity != null) {
            mActivity.finish();
        }
    }

    @Override
    public Application getApplication() {
        if (mActivity != null) {
            return mActivity.getApplication();
        }
        return null;
    }

    @Override
    public Activity getActivity() {
        return mActivity;
    }

    @Override
    public <T extends View> T findViewById(int resId) {
        if (mFragment != null && mFragment.getView() != null) {
            return mFragment.getView().findViewById(resId);
        } else if (mActivity != null) {
            return mActivity.findViewById(resId);
        }
        return null;
    }

    @Override
    public boolean handleDecode(String result) {
        if (mCaptureResult != null) {
            return mCaptureResult.handleDecode(result);
        }
        return false;
    }
}
