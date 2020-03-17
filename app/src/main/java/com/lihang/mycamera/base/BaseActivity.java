package com.lihang.mycamera.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

/**
 * Created by leo
 * on 2020/3/12.
 */
public abstract class BaseActivity<VDB extends ViewDataBinding> extends AppCompatActivity implements View.OnClickListener {
    //获取当前activity布局文件
    protected abstract int getContentViewId();

    //处理逻辑业务
    protected abstract void processLogic();

    //所有监听放这里
    protected abstract void setListener();

    protected VDB binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, getContentViewId());
        binding.setLifecycleOwner(this);
        processLogic();
        setListener();
    }
}
