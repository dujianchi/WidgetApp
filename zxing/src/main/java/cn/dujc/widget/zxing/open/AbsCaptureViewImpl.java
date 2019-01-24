package cn.dujc.widget.zxing.open;

import android.os.Build;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.google.zxing.BarcodeFormat;

import java.io.IOException;
import java.util.Vector;

import cn.dujc.widget.zxing.R;
import cn.dujc.widget.zxing.camera.CameraManager;
import cn.dujc.widget.zxing.decoding.CaptureActivityHandler;
import cn.dujc.widget.zxing.decoding.InactivityTimer;
import cn.dujc.widget.zxing.view.ViewfinderView;

/**
 * Initial the camera
 *
 * @author dujc
 */
public abstract class AbsCaptureViewImpl implements Callback, ICaptureView {

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    @Override
    public Handler getHandler() {
        return handler;
    }

    @Override
    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    @Override
    public int getViewId(){
        return R.layout.activity_capture;
    }

    @Override
    public void _onCreateAfterSetupView() {
        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(getActivity());
    }

    @Override
    public void _onDestroyBefore() {
        inactivityTimer.shutdown();
    }

    @Override
    public void _onResume() {
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            }
        }
        decodeFormats = null;
        characterSet = null;

//		playBeep = true;
//		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
//		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
//			playBeep = false;
//		}
//		initBeepSound();
//		vibrate = true;

        //quit the scan view
//		cancelScanButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				AbsCaptureViewImpl.this.finish();
//			}
//		});
    }

    @Override
    public void _onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

//	private void initBeepSound() {
//		if (playBeep && mediaPlayer == null) {
//			// The volume on STREAM_SYSTEM is not adjustable, and users found it
//			// too loud,
//			// so we now play on the music stream.
//			setVolumeControlStream(AudioManager.STREAM_MUSIC);
//			mediaPlayer = new MediaPlayer();
//			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//			mediaPlayer.setOnCompletionListener(beepListener);
//
//			AssetFileDescriptor file = getResources().openRawResourceFd(
//					R.raw.beep);
//			try {
//				mediaPlayer.setDataSource(file.getFileDescriptor(),
//						file.getStartOffset(), file.getLength());
//				file.close();
//				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
//				mediaPlayer.prepare();
//			} catch (IOException e) {
//				mediaPlayer = null;
//			}
//		}
//	}

//	private static final long VIBRATE_DURATION = 200L;

//	private void playBeepSoundAndVibrate() {
//		if (playBeep && mediaPlayer != null) {
//			mediaPlayer.start();
//		}
//		if (vibrate) {
//			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
//			vibrator.vibrate(VIBRATE_DURATION);
//		}
//	}
//
//    /**
//     * When the beep has finished playing, rewind to queue up another one.
//     */
//    private final OnCompletionListener beepListener = new OnCompletionListener() {
//        public void onCompletion(MediaPlayer mediaPlayer) {
//            mediaPlayer.seekTo(0);
//        }
//    };

}