package cn.dujc.widget.banner;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

/**
 * 自定义指示器
 */
public interface DuBannerIndicator {

    @NonNull
    ViewGroup getView();

    void updateIndex(int current, int count);
}
