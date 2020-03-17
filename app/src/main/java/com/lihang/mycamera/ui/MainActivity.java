package com.lihang.mycamera.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Toast;

import com.lihang.mycamera.R;
import com.lihang.mycamera.ui.systemcamera.SystemCameraActivity;
import com.lihang.mycamera.ui.mycamera.activity.MyCameraActivity;
import com.lihang.mycamera.base.BaseActivity;
import com.lihang.mycamera.databinding.ActivityMainBinding;
import com.lihang.mycamera.utils.ActivityUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import androidx.core.app.ActivityCompat;
import io.reactivex.functions.Consumer;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private RxPermissions rxPermissions;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void processLogic() {
        rxPermissions = new RxPermissions(this);
    }

    @Override
    protected void setListener() {
        binding.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonPanel:
                goWhat(0);

                break;

            case R.id.buttonPane2:
                goWhat(1);
                break;
        }
    }


    public void goWhat(final int type) {
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            //这个判断是为了解决，oppo手机保持禁止后，也会走这个方法，android太杂了
                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                                    && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            ) {
                                if (type == 0) {
                                    ActivityUtils.startActivity(MainActivity.this, SystemCameraActivity.class);
                                } else {
                                    ActivityUtils.startActivity(MainActivity.this, MyCameraActivity.class);
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "权限被拒绝", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        } else {
                            Toast.makeText(MainActivity.this, "权限被拒绝", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }

}
