package cameratest.tokbox.com.cameraswap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cameratest.tokbox.com.cameraswap.cameratest.tokbox.util.Range;
import cameratest.tokbox.com.cameraswap.cameratest.tokbox.util.Size;

/**
 * Created by ardy on 3/2/16.
 */
public class CameraAdapter implements Camera.PreviewCallback {

    private Camera mCamera;
    private SurfaceHolder mSfcHldr;
    private Camera.Size mCamSize;
    private Range mCamFps;
    private static final boolean PREVIEW_FRAME_ENABLED = false;
    public static final int TEXTURE_ID = 43;
    public static final Size LOW  = new Size(352, 288);
    public static final Size MED  = new Size(640, 480);
    public static final Size HIGH = new Size(1280, 720);
    public static final int FPS_01 = 1;
    public static final int FPS_07 = 7;
    public static final int FPS_15 = 15;
    public static final int FPS_30 = 30;

    public CameraAdapter(SurfaceHolder surfaceHolder) {
        mSfcHldr = surfaceHolder;
    }

    public void initCamera(int CamId, int fps, Size size) {

        mCamera = Camera.open(CamId);
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            mCamSize = _findCamSize(size, parameters);
            mCamFps = _findCamFps(fps * 1000, parameters);
            parameters.setPreviewSize(mCamSize.width, mCamSize.height);
            parameters.setPreviewFpsRange(mCamFps.getLowerBound(), mCamFps.getmUpperBound());
            if (PREVIEW_FRAME_ENABLED) {
                parameters.setPreviewFormat(ImageFormat.NV21);
                // Create capture buffers
                PixelFormat pixelFormat = new PixelFormat();
                PixelFormat.getPixelFormatInfo(ImageFormat.NV21, pixelFormat);
                int bufSize = mCamSize.width * mCamSize.height * pixelFormat.bitsPerPixel / 8;
                byte[] buffer = null;
                for (int i = 0; i < 3; i++) {
                    buffer = new byte[bufSize];
                    mCamera.addCallbackBuffer(buffer);
                }
                mCamera.setPreviewTexture(new SurfaceTexture(TEXTURE_ID));
                mCamera.setPreviewCallbackWithBuffer(this);
            } else {
                mCamera.setPreviewDisplay(mSfcHldr);
            }
            mCamera.setParameters(parameters);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
            mCamera.release();
        }
    }

    public void destroyCamera() {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    public Range getCamFps() {
        return mCamFps;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Log.d("[CameraAdapter]", "onPreviewFrame");
        camera.addCallbackBuffer(data);
    }

    private Camera.Size _findCamSize(final Size cameraSize, Camera.Parameters params) {
        List<Camera.Size> sizeLst = params.getSupportedPreviewSizes();
        /* sort list by error from desired size */
        return Collections.min(sizeLst, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                int lXerror = Math.abs(lhs.width - cameraSize.getWidth());
                int lYerror = Math.abs(lhs.height - cameraSize.getHeight());
                int rXerror = Math.abs(rhs.width - cameraSize.getWidth());
                int rYerror = Math.abs(rhs.height - cameraSize.getHeight());
                return (lXerror + lYerror) - (rXerror + rYerror);
            }
        });

    }

    private Range _findCamFps(final int fps, Camera.Parameters params) {
        List<int[]> supportedFps = params.getSupportedPreviewFpsRange();
        int[] fpsRange = Collections.min(supportedFps, new Comparator<int[]> () {
            @Override
            public int compare(int lhs[], int rhs[]) {
                return _calcError(lhs) - _calcError(rhs);
            }
            private int _calcError(int range[]) {
                return  Math.abs(range[0] - fps) + Math.abs(range[1] - fps);
            }
        });
        return new Range(fpsRange[0], fpsRange[1]);
    }
}
