package cn.dujc.widget.zxing.open;

import android.os.Handler;

import cn.dujc.widget.zxing.view.ViewfinderView;

public interface ICaptureHandler extends ICaptureResult {
    ViewfinderView getViewfinderView();

    void drawViewfinder();

    Handler getHandler();
}
