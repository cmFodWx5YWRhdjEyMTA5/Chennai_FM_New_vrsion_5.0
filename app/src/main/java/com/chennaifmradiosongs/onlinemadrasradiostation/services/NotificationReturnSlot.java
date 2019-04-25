package com.chennaifmradiosongs.onlinemadrasradiostation.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.chennaifmradiosongs.onlinemadrasradiostation.activities.ActivityMain;

public class NotificationReturnSlot extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    PlayPause();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    NextPlay();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    PrevPlay();
                    break;
            }
        } else {
            switch (intent.getAction()) {
                case RadioStreamingService.ACTION_STOP:
                    PlayPause();
                    break;
                case RadioStreamingService.ACTION_NEXT:
                    NextPlay();
                    break;
                case RadioStreamingService.ACTION_PREV:
                    PrevPlay();
                    break;
                case RadioStreamingService.ACTION_CLOSE:
                    AppClose(context);
                    break;
            }
        }
    }

    private void AppClose(Context context) {
        if (RadioStreamingService.getInstance() != null) {
            ActivityMain.isActive = (false);
            context.stopService(new Intent(context, RadioStreamingService.class));
            RadioStreamingService.getInstance().notificationCancel();
            ActivityMain.fa.finish();
        }
    }

    private void PrevPlay() {
        if (RadioStreamingService.getInstance() != null)
            if (RadioStreamingService.getInstance().isPlaying()) {
                RadioStreamingService.getInstance().StopExoPlayer();
                RadioStreamingService.getInstance().PrevPlay();
            }else{
                RadioStreamingService.getInstance().PrevPlay();
            }
    }

    private void NextPlay() {
        if (RadioStreamingService.getInstance() != null)
            if (RadioStreamingService.getInstance().isPlaying()) {
                RadioStreamingService.getInstance().StopExoPlayer();
                RadioStreamingService.getInstance().NextPlay();
            }else{
                RadioStreamingService.getInstance().NextPlay();
            }
    }

    private void PlayPause() {
        if (RadioStreamingService.getInstance() != null)
            if (RadioStreamingService.getInstance().isPlaying()) {
                ActivityMain.isActive = (false);
                RadioStreamingService.getInstance().StopExoPlayer();
            } else {
                ActivityMain.isActive = (true);
                RadioStreamingService.getInstance().PlayExoPlayer();
            }
    }
}

