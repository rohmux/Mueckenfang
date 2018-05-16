package de.androidnewcomer.kompass;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;


public class KompassActivity extends Activity implements SensorEventListener{

    private SensorManager sensorManager;
    private Sensor magnetfeldSensor;
    private KompassnadelView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new KompassnadelView(this);
        setContentView(view);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magnetfeldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    }


    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, magnetfeldSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(view!=null) {
            view.setWinkel(-event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this);
        super.onPause();
    }
}
