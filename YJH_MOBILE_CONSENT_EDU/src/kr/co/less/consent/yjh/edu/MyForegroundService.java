package kr.co.less.consent.yjh.edu;

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

/**
 * 포어그라운드 서비스 관련 클래스 - 앱 상태가 백그라운드로 전환시, 포어그라운드 알림 표시 - 동의서 앱이 백그라운드 전환시, 안드로이드
 * OS에게 kill 당하는 문제 해결 위해 로직 작성
 * 
 * @author gkwns
 *
 */
public class MyForegroundService extends Service {

	// about notification
	NotificationChannel notificationChannel;
	final String CHANNEL_ID = "Foreground Service ID";
	final int FOREGROUND_NOTI_ID = 888;

	// about thread
	final static String CUSTOM_THREAD_NAME = "MyCustomThread";
	Thread thread;
	int count = 0;

	// about log
	final String LOG_TAG = "MyForegroundService";

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		Log.d(LOG_TAG, "서비스(백그라운드) 인스턴스 생성");

		// init thread
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Log.d(LOG_TAG, "서비스-스레드 run start");
				while (true) {
					Log.e("Service", "서비스가 실행 중입니다...");
					Log.e("Service", "" + count);
					try {
						// 백그라운드 작업 및 포어그라운드 알림 업데이트
						Thread.sleep(2000);
						count = count >= Integer.MAX_VALUE ? 0 : count + 2;
						updateNotification(count);
						// EFromViewer.writeLog("[hajun] 카운트: " + Integer.toString(count) + ", 스레드 아이디:
						// " + Long.toString(Thread.currentThread().getId())); // will be deleted
					}
					// 스레드 중단 요청시 발생하는 예외
					catch (InterruptedException e) {
						Log.d(LOG_TAG, "서비스-스레드 인스턴스 발생 및 중단");
						break;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}, CUSTOM_THREAD_NAME);

		// init notification chanel(android_api >= 26)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			notificationChannel = new NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
					NotificationManager.IMPORTANCE_LOW);
			getSystemService(NotificationManager.class).createNotificationChannel(notificationChannel);
		}
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {

    	Notification notification = createNotification(0);
        
        startForeground(FOREGROUND_NOTI_ID, notification);
        this.thread.start();

        return super.onStartCommand(intent, flags, startId);
    }
    
    @Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	
    	this.thread.interrupt();	// 서비스 인스턴스가 종료되면 스레드(백그라운드작업) 중단
    	Log.d(LOG_TAG, "서비스(백그라운드) 인스턴스 소멸");
    }
    
    /**
     *  hajun :: 2024.12.30
     *  알림 생성 메소드
     * @param time
     * @return Notification
     */
    private Notification createNotification(int time) {
    	
    	Notification notification;
    	
    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 오레오(API 26) 이상일 때           
            Notification.Builder notification_builder = new Notification.Builder(this, CHANNEL_ID)
                    .setContentText(String.format("서비스가 실행중입니다.(%ds)", time))
                    .setContentTitle("전자동의서 서비스")
                    .setSmallIcon(android.R.drawable.ic_menu_info_details)
            		.setOngoing(true);

            
            
            notification = notification_builder.build();
        }
    	else { // 오레오 미만 버전에서는 NotificationCompat 사용
            NotificationCompat.Builder notification_builder = new NotificationCompat.Builder(this)
                    .setContentText(String.format("서비스가 실행중입니다.(%ds)", time))
                    .setContentTitle("전자동의서 서비스")
                    .setSmallIcon(android.R.drawable.ic_menu_info_details)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_LOW);

            notification = notification_builder.build();
        }
    	
    	return notification;
    }
    
    /**
     * hajun :: 2024.12.30
     *  알림 업데이트 메소드
     * @param time: 백그라운드 전환 후 경과 시간
     */
    private void updateNotification(int time) {
    	
    	Notification notification = createNotification(time);  	
    	
    	NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(FOREGROUND_NOTI_ID, notification); // 동일한 ID로 알림 갱신
        }
    }
}
