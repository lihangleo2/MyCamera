package com.lihang.mycamera.ui.mycamera.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaController2;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Toast;

import com.lihang.mycamera.R;
import com.lihang.mycamera.base.BaseActivity;
import com.lihang.mycamera.databinding.ActivityVideoPlayBinding;
import com.lihang.mycamera.ui.mycamera.OnVideoPauseListener;
import com.lihang.mycamera.ui.systemcamera.SystemCameraActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by leo
 * on 2020/3/18.
 * 预览视频页面
 */
public class VideoPlayActivity extends BaseActivity<ActivityVideoPlayBinding> implements OnVideoPauseListener {
    int currentPosition;
    ScheduledExecutorService service = Executors.newScheduledThreadPool(3);
    ScheduledFuture<?> future;


    public static void startActivity(Context context, String path) {
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra("path", path);
        context.startActivity(intent);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_video_play;
    }


    @Override
    protected void onResume() {
        super.onResume();
        binding.videoView.seekTo(currentPosition);
        binding.videoView.start();
    }

    @Override
    protected void processLogic() {
//        String path = "/storage/emulated/0/DCIM/Camera/VID_20200318_151507.mp4";
//        String path = "/storage/emulated/0/DCIM/Camera/VID_20200318_154208.mp4";
        String path = getIntent().getStringExtra("path");
        Log.e("我去啊", path);
        binding.videoView.setVideoPath(path);
        binding.videoView.setOnVideoPauseListener(this);

        //自己定制化一个MediaController样式
//        MediaController mediaController = new MediaController(this, false);
//        binding.videoView.setMediaController(mediaController);

    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.videoView.pause();
        currentPosition = binding.videoView.getCurrentPosition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.videoView.suspend();
    }

    @Override
    protected void setListener() {
        binding.setOnClickListener(this);
        binding.videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(VideoPlayActivity.this)
                        .setMessage("无法播放此视频").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                finish();
                            }
                        });
                builder.create().show();
                //这里return true后，就不弹出系统的无法播放此视频的弹窗
                return true;
            }
        });

        binding.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                binding.seekBar.setMax(binding.videoView.getDuration() * 1000);
                mp.setLooping(true);
            }
        });


        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    binding.videoView.seekTo(progress / 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                binding.videoView.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                binding.videoView.start();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image:
                binding.videoView.start();
                break;

            case R.id.txt_pause:
                if (binding.videoView.isPlaying()) {
                    binding.videoView.pause();
                }
                break;
        }
    }

    @Override
    public void videoPause(boolean isPause) {
        if (isPause) {
            //暂停了
            binding.image.setVisibility(View.VISIBLE);
            future.cancel(true);
        } else {
            binding.image.setVisibility(View.GONE);
            future = service.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    binding.seekBar.setProgress(binding.videoView.getCurrentPosition() * 1000);
                }
            }, 0, 10, TimeUnit.MILLISECONDS);
        }
    }
}
