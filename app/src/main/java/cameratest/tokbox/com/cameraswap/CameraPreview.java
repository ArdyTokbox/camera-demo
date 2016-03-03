package cameratest.tokbox.com.cameraswap;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import cameratest.tokbox.com.cameraswap.cameratest.tokbox.util.Size;


public class CameraPreview extends Activity implements SurfaceHolder.Callback {

    private SurfaceView mCamPreview;
    private CameraAdapter mCamera = null;
    private int mCameraId = 0;
    private int mSizeNdx = 0;
    private int mFpsNdx = 0;
    private static final Size mCameraSizeTbl[] = new Size[] {
            CameraAdapter.LOW, CameraAdapter.MED, CameraAdapter.HIGH
    };
    private static final int mCameraFpsTbl[] = new int[] {
            CameraAdapter.FPS_01,
            CameraAdapter.FPS_07,
            CameraAdapter.FPS_15,
            CameraAdapter.FPS_30,
    };
    private static final String mCameraSizeNameTbl[] = new String[] {
            "LOW", "MED", "HIGH"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);
        mCamPreview = (SurfaceView)findViewById(R.id.camera_preview);
        mCamPreview.getHolder().addCallback(this);
        mCamera = new CameraAdapter(mCamPreview.getHolder());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_camera_preview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBtnPress(View item) {

        switch (item.getId()) {
            case R.id.swap_camera_btn:
                mCameraId ^= 1;
                break;
            case R.id.switch_size_btn:
                mSizeNdx = (mSizeNdx + 1) % mCameraSizeTbl.length;
                break;
            case R.id.switch_fps_btn:
                mFpsNdx = (mFpsNdx + 1) % mCameraFpsTbl.length;
                break;
            default:
                break;
        }
        mCamera.destroyCamera();
        mCamera.initCamera(mCameraId, mCameraFpsTbl[mFpsNdx], mCameraSizeTbl[mSizeNdx]);
        Toast.makeText(
                this,
                "Camera ID:" + mCameraId + " Camera Size: " + mCameraSizeNameTbl[mSizeNdx] +
                        " Camera fps:[" + mCamera.getCamFps().getLowerBound() + ", " +
                        mCamera.getCamFps().getmUpperBound() + "]",
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera.initCamera(mCameraId, mCameraFpsTbl[mFpsNdx], mCameraSizeTbl[mSizeNdx]);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.destroyCamera();
    }

}
