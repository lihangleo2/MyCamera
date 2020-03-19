package com.lihang.mycamera.ui.systemcamera;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.lihang.mycamera.R;
import com.lihang.mycamera.base.BaseActivity;
import com.lihang.mycamera.databinding.ActivitySystemCameraBinding;
import com.lihang.mycamera.ui.MainActivity;
import com.lihang.mycamera.ui.mycamera.activity.VideoPlayActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileStore;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

/**
 * Created by leo
 * on 2020/3/12.
 * 7.0以上调用系统相机拍照关于Uri的。https://www.jianshu.com/p/55eae30d133c
 */
public class SystemCameraActivity extends BaseActivity<ActivitySystemCameraBinding> {
    private static int CAMERA_RESULT = 1;
    private static int CAMERA_PATH = 2;
    private static int CAMERA_VIDEO_PATH = 3;
    File newFile;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_system_camera;
    }

    @Override
    protected void processLogic() {
        newFile = new File(getExternalCacheDir(), "output_image.jpg");
    }

    @Override
    protected void setListener() {
        binding.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonPanel:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_RESULT);
                break;

            case R.id.buttonPane2:
                Intent intent_2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri photoUri = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    photoUri = FileProvider.getUriForFile(this, "com.lihang.mycamera.fileprovider", newFile);
                    // 给目标应用一个临时授权
                    intent_2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    photoUri = Uri.fromFile(newFile);
                }
                intent_2.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent_2, CAMERA_PATH);
                break;

            case R.id.buttonPane3:
                Intent intentVideo = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(intentVideo, CAMERA_VIDEO_PATH);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_RESULT) {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");
                binding.image.setImageBitmap(bitmap);
            } else if (requestCode == CAMERA_PATH) {
                Bitmap bitmap = BitmapFactory.decodeFile(newFile.getPath());
                binding.imageSrc.setImageBitmap(bitmap);
            } else if (requestCode == CAMERA_VIDEO_PATH) {
                String path = data.getData().toString();
                VideoPlayActivity.startActivity(SystemCameraActivity.this, path);
            }
        }
    }
}
