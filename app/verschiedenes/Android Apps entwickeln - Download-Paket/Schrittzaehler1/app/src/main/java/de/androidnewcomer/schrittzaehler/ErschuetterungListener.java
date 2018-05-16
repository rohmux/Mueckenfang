package de.androidnewcomer.schrittzaehler;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;

/**
 * Created by uwe on 26.05.15.
 */
public class ErschuetterungListener implements SensorEventListener {

    private static final float SCHWELLWERT = 1.5f;
    private Handler handler;
    private boolean schrittBegonnen;
    private float schwellwert = SCHWELLWERT;

    public ErschuetterungListener(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float y = event.values[1];
        //Log.d("sensor", " " + event.values[0] + " " + event.values[1] + " " + event.values[2]);
        if(schrittBegonnen) {
            if(y>schwellwert) {
                schrittBegonnen = false;
                handler.sendEmptyMessage(1);
            }
        } else {
            if(y< -schwellwert) {
                schrittBegonnen= true;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void setSchwellwert(float schwellwert) {
        this.schwellwert = schwellwert;
    }
}
