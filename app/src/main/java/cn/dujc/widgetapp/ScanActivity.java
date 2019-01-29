package cn.dujc.widgetapp;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import cn.dujc.widget.zxing.camera.CameraManager;
import cn.dujc.widget.zxing.open.CaptureViewImpl;
import cn.dujc.widget.zxing.open.ICaptureHandler;
import cn.dujc.widget.zxing.open.ICaptureView;
import cn.dujc.widget.zxing.view.ViewfinderView;

public class ScanActivity extends AppCompatActivity implements ICaptureHandler {

    private ICaptureView mCaptureView;
    private boolean on = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCaptureView = new CaptureViewImpl(this, this);
        setContentView(mCaptureView.getViewId());
        mCaptureView._onCreateAfterSetupView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("flash light");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        CameraManager.get().flashLight(on = !on);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public ViewfinderView getViewfinderView() {
        return mCaptureView.getViewfinderView();
    }

    @Override
    public void drawViewfinder() {
        mCaptureView.drawViewfinder();
    }

    @Override
    public Handler getHandler() {
        return mCaptureView.getHandler();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCaptureView._onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCaptureView._onResume();
    }

    @Override
    protected void onDestroy() {
        mCaptureView._onDestroyBefore();
        super.onDestroy();
    }

    @Override
    public boolean handleDecode(String result) {
        Toast.makeText(this, "result = " + result, Toast.LENGTH_SHORT).show();
        return false;
    }
}
