package com.lihang.mycamera.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class ImageUtil {
    public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotateDegree);
        return Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
    }


    public static Bitmap getRotateBitmap_front(Bitmap b, float rotateDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotateDegree);
        //postScale 解决前置 左右相反的问题
        matrix.postScale(-1, 1);
        return Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
    }


}
