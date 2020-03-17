package com.lihang.mycamera.ui.mycamera.customview;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;

import com.lihang.mycamera.ui.mycamera.OnCaptureData;
import com.lihang.mycamera.utils.ImageUtil;
import com.lihang.mycamera.utils.TimeUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by leo
 * on 2020/3/13.
 */
public class CameraInterface {

    private Camera mCamera;
    private Camera.Parameters parameters;
    private Context context;
    private static CameraInterface mCameraInterface;
    private int cameraWith;//当前camera所占宽度
    private int cameraHeight;//当前camera所占高度

    private int rightWith;//预览和图片的最佳尺寸
    private int rightHeight;//预览和图片的最佳尺寸

    private int cameraId;//照相机

    private CameraInterface(Context context) {
        this.context = context;
    }

    public static synchronized CameraInterface getInstance(Context context) {
        if (mCameraInterface == null) {
            mCameraInterface = new CameraInterface(context);
        }
        return mCameraInterface;
    }

    public void restartPreview() {
        mCamera.startPreview();
    }


    //1、创建相机,参数是否是前置摄像头
    public void doOpenCamera(boolean isFront) {
        try {
            if (isFront) {
                mCamera = Camera.open(1);
                cameraId = 1;
            } else {
                mCamera = Camera.open();
                cameraId = 0;
            }
            //获得相机参数
            parameters = mCamera.getParameters();
            getRightSize();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("如果未打开权限会走这", "相机权限未打开!");
        }
    }


    // 2、开始预览
    public void doStartPreview(SurfaceHolder surfaceHolder, int with, int height) {
        if (mCamera != null) {
            try {
                cameraWith = with;
                cameraHeight = height;
                //设置相机参数
                setParameters();
                //这里要注意系统默认的横屏的。我们要将其转换成竖屏，旋转90°
                mCamera.setDisplayOrientation(90);
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //3、关闭相机
    public void doStopCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }


    public void setParameters() {
        parameters.setPreviewSize(rightWith, rightHeight);
        parameters.setPictureSize(rightWith, rightHeight);
        //设置jpg格式
        parameters.setPictureFormat(ImageFormat.JPEG);
        //设置自动聚焦
        List<String> focusModes = parameters.getSupportedFocusModes();
//        Camera.Parameters.FOCUS_MODE_AUTO
        if (focusModes.contains("continuous-video")) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }

        //设备支持闪光灯，且为后置摄像头才设置闪光模式
        if (isSupportFlash() && cameraId == 0) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        }
        mCamera.setParameters(parameters);
    }

    //切换闪光灯模式
    public void switchFlash(int flashMode) {
        if (!isSupportFlash() || cameraId == 1) {
            //设备不支持闪光灯模式，或者是前置摄像头
            return;
        }
        if (flashMode % 3 == 0) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        } else if (flashMode % 3 == 1) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        } else if (flashMode % 3 == 2) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }


    /**
     * 照相
     */
    public void takePicture(final OnCaptureData callback) {
        if (mCamera == null) {
            return;
        }
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                boolean success = false;
                if (data != null && data.length > 0) {
                    success = true;
                }
                //点击拍照，停止预览
                mCamera.stopPreview();
                //这个是图片截图用的。
                String path = savePicture(data);
//                Bitmap endBit = Bitmap.createScaledBitmap(savePicture(data), 1080, 1794, true);
                callback.onCapture(success, path);
            }
        });
    }


    //这是获取预览图的接口。什么意思呢？
    //比如上面的takePicture,即使不做任何处理，点击拍照也会停留顿一下。
    //说个小需求，如果加上相机人脸识别，当相机里出现人脸的画，需要取几张人脸的图，用拍照就不好实现了。得用下面的实现。
    public void takeOneShotPreview(){
        mCamera.setOneShotPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {

            }
        });
    }






    private String savePicture(byte[] data) {

        File imgFileDir = getImageDir();
        if (!imgFileDir.exists() && !imgFileDir.mkdirs()) {
            return null;
        }
        //文件路径路径
        String imgFilePath = context.getFilesDir().getAbsolutePath().toString() + "/" + TimeUtils.getDateToStringLeo(System.currentTimeMillis() + "") + "_atmancarm.jpg";
        Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length);


        Bitmap rotatedBitmap = null;
        if (null != b) {
            if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                rotatedBitmap = ImageUtil.getRotateBitmap(b, 90.0f);
            } else if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                rotatedBitmap = ImageUtil.getRotateBitmap_front(b, -90.0f);
            }
        }

        int truePicHeight;
        //注意rightWith 和rightHeight原手机横屏的尺寸。也就是说rightHeight是我们真实的预览和图片尺寸的宽度
        truePicHeight = rightHeight * cameraHeight / cameraWith;
        if (truePicHeight > rightWith) {
            truePicHeight = rightWith;
        }

        rotatedBitmap = Bitmap.createScaledBitmap(rotatedBitmap, rightHeight, truePicHeight, true);

        File imgFile = new File(imgFilePath);
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            fos = new FileOutputStream(imgFile);
            bos = new BufferedOutputStream(fos);
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception error) {
            return null;
        } finally {
            try {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
                if (bos != null) {
                    bos.flush();
                    bos.close();
                }
            } catch (IOException e) {
            }

        }

        return imgFilePath;
    }


    private File getImageDir() {
        String path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }


    private CameraSizeComparator sizeComparator = new CameraSizeComparator();

    public void getRightSize() {
        if (rightWith != 0 && rightHeight != 0) {
            return;
        }
        //获取camera api 当前设备支持的预览尺寸
        List<Camera.Size> listPreview = parameters.getSupportedPreviewSizes();
        Collections.sort(listPreview, sizeComparator);
        ArrayList<String> flagComparator = new ArrayList<>();

        for (int i = 0; i < listPreview.size(); i++) {
            Camera.Size size = listPreview.get(i);
            int flag = size.width + size.height;
            flagComparator.add(flag + "");
        }
        //获取camera api 当前设备支持的相机尺寸
        List<Camera.Size> listPicture = parameters.getSupportedPictureSizes();
        Collections.sort(listPicture, sizeComparator);

        for (int i = 0; i < listPicture.size(); i++) {
            Camera.Size size = listPicture.get(i);
            int flag = size.width + size.height;
            if (flagComparator.contains(flag + "")) {
                rightWith = size.width;
                rightHeight = size.height;
                return;
            }
        }
    }


    public int getRightWith() {
        return rightWith;
    }

    public int getRightHeight() {
        return rightHeight;
    }

    public int getCameraId() {
        return cameraId;
    }


    //降序的排序规则
    public class CameraSizeComparator implements Comparator<Camera.Size> {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if (lhs.width == rhs.width) {
                return 0;
            } else if (lhs.width > rhs.width) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    /**
     * 判断设备是否支持闪光灯
     */
    private boolean isSupportFlash() {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            return false;
        } else {
            return true;
        }
    }

}
