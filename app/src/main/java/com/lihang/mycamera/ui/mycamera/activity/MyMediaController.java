package com.lihang.mycamera.ui.mycamera.activity;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.MediaController;

/**
 * Created by leo
 * on 2020/3/18.
 */
public class MyMediaController extends MediaController {
    public MyMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyMediaController(Context context, boolean useFastForward) {
        super(context, useFastForward);
    }

    public MyMediaController(Context context) {
        super(context);
    }
}
