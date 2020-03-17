package com.lihang.mycamera.ui.mycamera.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.lihang.mycamera.R;
import com.lihang.mycamera.base.BaseActivity;
import com.lihang.mycamera.databinding.ActivityResultBinding;

/**
 * Created by leo
 * on 2020/3/16.
 */
public class ResultActivity extends BaseActivity<ActivityResultBinding> {

    private String path;

    public static void startActivity(Context context, String path) {
        Intent intent = new Intent(context, ResultActivity.class);
        intent.putExtra("path", path);
        context.startActivity(intent);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_result;
    }

    @Override
    protected void processLogic() {
        path = getIntent().getStringExtra("path");
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        binding.image.setImageBitmap(bitmap);
    }

    @Override
    protected void setListener() {
        binding.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_yes:
                finish();
                overridePendingTransition(R.anim.scale_activity_go, R.anim.scale_activity_come);
                break;

            case R.id.image_no:
                finish();
                overridePendingTransition(R.anim.scale_activity_go, R.anim.scale_activity_come);
                break;
        }
    }
}
