package com.seid.fetawa_.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.ContextCompat;

public class Utils {
    public static boolean isPermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int readPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return writePermission == PackageManager.PERMISSION_GRANTED && readPermission == PackageManager.PERMISSION_GRANTED;
        }
    }

    public static MediaPlayer mediaPlayer;

    public static void Player(MediaPlayer mp) {
        mediaPlayer = mp;
        mediaPlayer.setLooping(false);
        mediaPlayer.start();
    }

    public static void stop() {
        if (mediaPlayer != null)
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();
    }
}
