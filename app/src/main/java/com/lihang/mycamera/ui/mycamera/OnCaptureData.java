package com.lihang.mycamera.ui.mycamera;

public interface OnCaptureData {
    //1、是否拍照成功，
    //2、拍照存储的文件路，
    public void onCapture(boolean success, String path);
}