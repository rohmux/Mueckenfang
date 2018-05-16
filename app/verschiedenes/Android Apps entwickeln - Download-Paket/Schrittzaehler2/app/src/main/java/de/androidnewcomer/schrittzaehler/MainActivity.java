package de.androidnewcomer.schrittzaehler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity implements View.OnClickListener {

    private TextView textView;
    private EreignisHandler ereignisHandler = new EreignisHandler();

    private class EreignisHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            textView.setText(Integer.toString(msg.what));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        textView = (TextView)findViewById(R.id.schritte);
        findViewById(R.id.zuruecksetzen).setOnClickListener(this);
        findViewById(R.id.start).setOnClickListener(this);
        findViewById(R.id.stop).setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        SchrittzaehlerService.ereignisHandler = ereignisHandler;
        textView.setText(Integer.toString(SchrittzaehlerService.schritte));
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.start) {
            startService(new Intent(this, SchrittzaehlerService.class));
            SchrittzaehlerService.ereignisHandler = ereignisHandler;
        }
        if(view.getId()==R.id.stop) {
            stopService(new Intent(this, SchrittzaehlerService.class));
        }
        if(view.getId()==R.id.zuruecksetzen) {
            SchrittzaehlerService.schritte=0;
            textView.setText(Integer.toString(0));
        }

    }
}
