package com.lihang.mycamera.ui.mycamera.customview;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import com.lihang.mycamera.ui.mycamera.OnCaptureData;
import com.lihang.mycamera.utils.UIUtil;

/**
 * Created by leo
 * on 2020/3/12.
 */
public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder mSurfaceHolder;
    //是否是前置摄像头
    private boolean isFront;

    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        CameraInterface.getInstance(getContext()).doOpenCamera(isFront);

        //当前得到的最适当的尺寸
        int rightWith = CameraInterface.getInstance(getContext()).getRightHeight();
        int rightHeight = CameraInterface.getInstance(getContext()).getRightWith();

        //当前屏幕尺寸
        int phoneWith = UIUtil.getWidth(getContext());
        int phoneHeight = UIUtil.getHeight(getContext());
        int trueHeight = phoneWith * rightHeight / rightWith;

        //为了适配全面屏不变形做的处理
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
        layoutParams.width = phoneHeight;
        layoutParams.height = trueHeight;
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        setLayoutParams(layoutParams);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        CameraInterface.getInstance(getContext()).doStartPreview(holder, width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        CameraInterface.getInstance(getContext()).doStopCamera();
    }


    //开始拍照
    public void takePicture(OnCaptureData callback) {
        CameraInterface.getInstance(getContext()).takePicture(callback);
    }

    //重新打开摄像头
    public void restartPreview() {
        CameraInterface.getInstance(getContext()).restartPreview();
    }

    //切换摄像头
    public void switchCamera() {
        isFront = !isFront;
        CameraInterface.getInstance(getContext()).doStopCamera();
        CameraInterface.getInstance(getContext()).doOpenCamera(isFront);
        CameraInterface.getInstance(getContext()).doStartPreview(mSurfaceHolder, this.getMeasuredWidth(), this.getMeasuredHeight());
    }

    public boolean isFront() {
        return isFront;
    }


    //切换闪光灯模式
    public void switchFlash(int flash) {
        CameraInterface.getInstance(getContext()).switchFlash(flash);
    }


}
