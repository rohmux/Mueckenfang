package de.androidnewcomer.notificationdemo;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),0);

        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender()
                .setHintHideIcon(true)
                .setBackground(((BitmapDrawable) getResources().getDrawable(R.drawable.background)).getBitmap());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVibrate(new long[] {400,700,500})
            .setContentTitle(getText(R.string.app_name))
            .setContentText(getText(R.string.hello_world))
            .setWhen(System.currentTimeMillis()+2000)
            .extend(wearableExtender)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(pendingIntent);

        Notification notification = builder.build();
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(1,notification);
    }
}
