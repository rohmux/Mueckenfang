package de.androidnewcomer.weginsbuero;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.PathOverlay;

import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener{

    private MapView mapView;
    private WegHandler handler = new WegHandler();

    private class WegHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            zeigeWeg();
        }
    }

    private void zeigeWeg() {
        List<Location> weg = WegAufzeichnungService.weg;
        if(!weg.isEmpty()) {
            mapView.getController().setCenter(new GeoPoint(weg.get(weg.size()-1)));
            mapView.getOverlayManager().clear();
            if(weg.size()>1) {
                PathOverlay overlay = new PathOverlay(Color.BLUE);
                for(int i=0; i<weg.size(); i++) {
                    GeoPoint point = new GeoPoint(weg.get(i));
                    overlay.addPoint(point);
                }
                mapView.getOverlayManager().add(overlay);
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViewById(R.id.start).setOnClickListener(this);
        findViewById(R.id.stop).setOnClickListener(this);
        mapView = (MapView) findViewById(R.id.map);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(16);
    }

    @Override
    protected void onResume() {
        super.onResume();
        zeigeWeg();
        WegAufzeichnungService.updateHandler = handler;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.start) {
            startService(new Intent(this, WegAufzeichnungService.class));
            WegAufzeichnungService.updateHandler = handler;
        }
        if(view.getId()==R.id.stop) {
            stopService(new Intent(this, WegAufzeichnungService.class));
        }
    }
}
