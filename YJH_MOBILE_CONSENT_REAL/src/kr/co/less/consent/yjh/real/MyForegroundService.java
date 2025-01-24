package kr.co.less.consent.yjh.real;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MyForegroundService extends Service {

    int count = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Log.e("Service", "서비스가 실행 중입니다...");
                    Log.e("Service", "" + count);
                    try {
                        Thread.sleep(2000);
                        count++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        final String CHANNELID = "Foreground Service ID";
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 오레오(API 26) 이상일 때
            NotificationChannel channel = new NotificationChannel(
                    CHANNELID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_LOW);

            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            
            Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                    .setContentText("서비스가 실행중입니다.")
                    .setContentTitle("전자동의서 서비스")
                    .setSmallIcon(android.R.drawable.ic_menu_info_details);

            startForeground(888, notification.build());
        } else {
            // 오레오 미만 버전에서는 NotificationCompat 사용
            NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                    .setContentText("서비스가 실행중입니다.")
                    .setContentTitle("전자동의서 서비스")
                    .setSmallIcon(android.R.drawable.ic_menu_info_details)
                    .setPriority(NotificationCompat.PRIORITY_LOW);

            startForeground(888, notification.build());
        }

        return super.onStartCommand(intent, flags, startId);
    }


}
