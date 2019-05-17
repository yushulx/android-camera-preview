package com.dynamsoft.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private Camera mCamera;
    private AutoFitTextureView mTextureView;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getSurfaceTexture());
        } else {
            mTextureView.setSurfaceTextureListener(this);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTextureView = new AutoFitTextureView(this);
        mTextureView.setSurfaceTextureListener(this);
        setContentView(mTextureView);
    }

    private void openCamera(SurfaceTexture surface) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
            return;
        }

        mCamera = Camera.open(0);
        try {
            mCamera.setPreviewTexture(surface);
            int rotation = getWindowManager().getDefaultDisplay()
                    .getRotation();
            mCamera.setDisplayOrientation(ORIENTATIONS.get(rotation));
            Camera.Parameters params =  mCamera.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            Camera.Size size = params.getPreviewSize();

            int orientation = getResources().getConfiguration().orientation;
            int previewWidth = size.width;
            int previewHeight = size.height;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mTextureView.setAspectRatio(
                        previewWidth, previewHeight);
            } else {
                mTextureView.setAspectRatio(
                        previewHeight, previewWidth);
            }

            mCamera.setParameters(params);
            mCamera.startPreview();
        } catch (IOException ioe) {
        }
    }
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        openCamera(surface);
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }

        return true;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }
        }
    }
}
