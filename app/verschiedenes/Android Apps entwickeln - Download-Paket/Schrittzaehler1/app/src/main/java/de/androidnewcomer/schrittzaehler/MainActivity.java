package de.androidnewcomer.schrittzaehler;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity implements View.OnClickListener {

    private SensorManager sensorManager;
    private Sensor sensor;
    private int schritte=0;
    private TextView textView;
    private ErschuetterungsHandler handler = new ErschuetterungsHandler();
    private ErschuetterungListener listener = new ErschuetterungListener(handler);

    private class ErschuetterungsHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            schritte++;
            aktualisiereAnzeige();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        textView = (TextView)findViewById(R.id.schritte);
        aktualisiereAnzeige();
        findViewById(R.id.button).setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(listener);
        super.onPause();
    }

    private void aktualisiereAnzeige() {
        textView.setText(Integer.toString(schritte));
    }


    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.button) {
            schritte=0;
            aktualisiereAnzeige();
        }
    }
}
