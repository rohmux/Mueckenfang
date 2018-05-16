package de.androidnewcomer.schrittzaehler;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

/**
 * Created by uwe on 26.05.15.
 */
public class SchrittzaehlerService  extends Service {

    public static Handler ereignisHandler;
    private SensorManager sensorManager;
    private Sensor sensor;
    public static int schritte=0;
    private ErschuetterungsHandler handler = new ErschuetterungsHandler();
    private ErschuetterungListener listener = new ErschuetterungListener(handler);


    private class ErschuetterungsHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            schritte++;
            if(ereignisHandler!=null)
                ereignisHandler.sendEmptyMessage(schritte);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(listener);
        super.onDestroy();
    }
}
