package cn.dujc.widget.zxing.open;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;

public interface IVew {

    @NonNull
    Activity getActivity();

    <T extends View> T findViewById(@IdRes int resId);

    public static class ActivityImpl implements IVew {
        @NonNull
        private final Activity mActivity;

        public ActivityImpl(@NonNull Activity activity) {
            mActivity = activity;
        }

        @NonNull
        @Override
        public Activity getActivity() {
            return mActivity;
        }

        @Override
        public <T extends View> T findViewById(int resId) {
            return mActivity.findViewById(resId);
        }
    }
}
