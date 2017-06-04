package com.example.george.dictionary;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by George on 2017/6/3.
 */

public class AudioService extends Service {
    private MediaPlayer mediaPlayer;
    private String query;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        if (query != null && !query.equals(intent.getStringExtra("query")) && mediaPlayer != null) {
            mediaPlayer.start();
        } else {
            String query = intent.getStringExtra("query");
            String contain = "http://dict.youdao.com/dictvoice?audio=" + query;
            Uri location = Uri.parse(contain);

            mediaPlayer = MediaPlayer.create(this, location);
            mediaPlayer.prepareAsync();
            mediaPlayer.start();


            // 播放音乐时发生错误的事件处理
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // 释放资源
                    try {
                        mp.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
