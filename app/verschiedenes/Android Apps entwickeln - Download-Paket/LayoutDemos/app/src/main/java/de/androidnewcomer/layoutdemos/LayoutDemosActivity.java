package de.androidnewcomer.layoutdemos;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LayoutDemosActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        buttonsVerdrahten();
    }

	private void buttonsVerdrahten() {
		((Button)findViewById(R.id.relativelayout)).setOnClickListener(this);
        ((Button)findViewById(R.id.gewichteteslayout)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.relativelayout) {
			setContentView(R.layout.relativelayout);
			((Button)findViewById(R.id.zurueck)).setOnClickListener(this);
		}
		if(v.getId()==R.id.gewichteteslayout) {
			setContentView(R.layout.gewichteteslayout);
			((Button)findViewById(R.id.zurueck)).setOnClickListener(this);
		}
		if(v.getId()==R.id.zurueck) {
			setContentView(R.layout.main);
			buttonsVerdrahten();
		}
		
	}
}