package com.lihang.mycamera.ui.mycamera.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.VideoView;

import com.lihang.mycamera.ui.mycamera.OnVideoPauseListener;

/**
 * Created by leo
 * on 2020/3/18.
 * 这个是为了监听VideoView的暂停和开始状态。
 */
public class MyVideoView extends VideoView {
    public OnVideoPauseListener onVideoPauseListener;

    public void setOnVideoPauseListener(OnVideoPauseListener onVideoPauseListener) {
        this.onVideoPauseListener = onVideoPauseListener;
    }

    public MyVideoView(Context context) {
        this(context, null);
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void pause() {
        super.pause();
        if (onVideoPauseListener != null) {
            onVideoPauseListener.videoPause(true);
        }
    }

    @Override
    public void start() {
        super.start();
        if (onVideoPauseListener != null) {
            onVideoPauseListener.videoPause(false);
        }
    }



}
