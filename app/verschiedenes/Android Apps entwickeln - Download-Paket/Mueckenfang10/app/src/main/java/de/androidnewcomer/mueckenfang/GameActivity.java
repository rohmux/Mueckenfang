package de.androidnewcomer.mueckenfang;

import android.app.Activity;
import android.app.Dialog;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.Random;

/**
 * Created by uwe on 20.05.15.
 */
public class GameActivity extends Activity implements View.OnClickListener, Runnable, SensorEventListener {

    private static final long HOECHSTALTER_MS = 2000;
    public static final int DELAY_MILLIS = 100;
    public static final int ZEITSCHEIBEN = 600;
    public static final int KAMERABREITE_AZIMUT = 10;
    public static final int KAMERABREITE_POLAR = 15;
    private int punkte;
    private int runde;
    private int gefangeneMuecken;
    private int zeit;
    private float massstab;
    private int muecken;
    private Random zufallsgenerator = new Random();
    private FrameLayout spielbereich;
    private boolean spielLaeuft;
    private Handler handler = new Handler();
    private MediaPlayer mp;
    private int schwierigkeitsgrad;
    private SensorManager sensorManager;
    private Sensor sensor;
    private RadarView radar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        massstab = getResources().getDisplayMetrics().density;
        spielbereich = (FrameLayout) findViewById(R.id.spielbereich);
        radar = (RadarView) findViewById(R.id.radar);
        radar.setContainer(spielbereich);
        mp = MediaPlayer.create(this,R.raw.summen);
        schwierigkeitsgrad = getIntent().getIntExtra("schwierigkeitsgrad",0);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        spielStarten();
    }

    private void spielStarten() {
        spielLaeuft = true;
        runde = 0;
        punkte = 0;
        starteRunde();
    }

    private void bildschirmAktualisieren() {
        TextView tvPunkte = (TextView)findViewById(R.id.points);
        tvPunkte.setText(Integer.toString(punkte));
        TextView tvRunde = (TextView)findViewById(R.id.round);
        tvRunde.setText(Integer.toString(runde));
        TextView tvTreffer = (TextView) findViewById(R.id.hits);
        tvTreffer.setText(Integer.toString(gefangeneMuecken));
        TextView tvZeit = (TextView) findViewById(R.id.time);
        tvZeit.setText(Integer.toString(zeit/(1000/DELAY_MILLIS)));
        FrameLayout flTreffer = (FrameLayout)findViewById(R.id.bar_hits);
        FrameLayout flZeit = (FrameLayout)findViewById(R.id.bar_time);
        ViewGroup.LayoutParams lpTreffer = flTreffer.getLayoutParams();
        lpTreffer.width = Math.round( massstab * 300 *
            Math.min( gefangeneMuecken,muecken)/muecken);
        ViewGroup.LayoutParams lpZeit = flZeit.getLayoutParams();
        lpZeit.width = Math.round(massstab*zeit*300/ ZEITSCHEIBEN);

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void zeitHerunterzaehlen(){
        zeit = zeit-1;
        if(zeit % (1000/DELAY_MILLIS) ==0) {
            float zufallszahl = zufallsgenerator.nextFloat();
            double wahrscheinlichkeit = muecken * 1.5;
            if (wahrscheinlichkeit > 1) {
                eineMueckeAnzeigen();
                if (zufallszahl < wahrscheinlichkeit - 1) {
                    eineMueckeAnzeigen();
                }
            } else {
                if (zufallszahl < wahrscheinlichkeit) {
                    eineMueckeAnzeigen();
                }
            }
        }
        mueckenVerschwinden();
        bildschirmAktualisieren();
        if(!pruefeSpielende()) {
            if(!pruefeRundenende()) {
                handler.postDelayed(this, DELAY_MILLIS);
            }
        }
    }

    private boolean pruefeRundenende() {
        if(gefangeneMuecken >= muecken) {
            starteRunde();
            return true;
        }
        return false;
    }

    private void starteRunde() {
        runde = runde +1;
        muecken = runde * (10+schwierigkeitsgrad*10);
        gefangeneMuecken = 0;
        zeit = ZEITSCHEIBEN;
        bildschirmAktualisieren();
        handler.postDelayed(this, 1000);
    }

    private boolean pruefeSpielende() {
        if(zeit == 0 && gefangeneMuecken < muecken) {
            gameOver();
            return true;
        }
        return false;
    }

    private void gameOver() {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.gameover);
        dialog.show();
        spielLaeuft = false;
        setResult(punkte);
    }


    private void mueckenVerschwinden() {
        int nummer=0;
        while(nummer < spielbereich.getChildCount()) {
            ImageView muecke = (ImageView) spielbereich.getChildAt(nummer);
            Date geburtsdatum = (Date) muecke.getTag(R.id.geburtsdatum);
            long alter = (new Date()).getTime() - geburtsdatum.getTime();
            if(alter > HOECHSTALTER_MS) {
                spielbereich.removeView(muecke);
            } else {
                nummer++;
            }
        }
    }

    private void eineMueckeAnzeigen() {

        int breite = spielbereich.getWidth();
        int hoehe  = spielbereich.getHeight();
        int muecke_breite = (int) Math.round(massstab*50);
        int muecke_hoehe = (int) Math.round(massstab*42);
        int links = zufallsgenerator.nextInt(breite - muecke_breite );
        int oben = zufallsgenerator.nextInt(hoehe - muecke_hoehe);

        // Mücke erzeugen
        ImageView muecke = new ImageView(this);
        muecke.setOnClickListener(this);
        muecke.setTag(R.id.geburtsdatum, new Date());
        muecke.setVisibility(View.INVISIBLE);

        muecke.setImageResource(R.drawable.muecke);

        // Mücke positionieren
        int azimut = zufallsgenerator.nextInt(360);
        int polar  = zufallsgenerator.nextInt(61) - 30;
        muecke.setTag(R.id.azimut, new Integer(azimut));
        muecke.setTag(R.id.polar, new Integer(polar));

        // Mücke anzeigen
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(muecke_breite, muecke_hoehe);
        params.leftMargin = links;
        params.topMargin = oben;
        params.gravity = Gravity.TOP + Gravity.LEFT;
        spielbereich.addView(muecke, params);

        // Summen starten
        mp.seekTo(0);
        mp.start();
    }


    @Override
    public void onClick(View muecke) {
        gefangeneMuecken++;
        punkte += 100 + schwierigkeitsgrad*100;
        bildschirmAktualisieren();
        mp.pause();
        Animation animationTreffer = AnimationUtils.loadAnimation(this,R.anim.treffer);
        muecke.startAnimation(animationTreffer);
        animationTreffer.setAnimationListener(new MueckeAnimationListener(muecke));
        muecke.setOnClickListener(null);
    }

    private void mueckenPositionieren(float azimutKamera, float polarKamera) {
        int nummer=0;
        while(nummer<spielbereich.getChildCount()) {
            ImageView muecke = (ImageView) spielbereich.getChildAt(nummer);
            int azimut = (Integer) muecke.getTag(R.id.azimut);
            int polar  = (Integer) muecke.getTag(R.id.polar);
            float azimutRelativ = azimut - azimutKamera;
            float polarRelativ = polar - polarKamera;
            if(istMueckeInKamera(azimutRelativ, polarRelativ)) {
                muecke.setVisibility(View.VISIBLE);
                FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) muecke.getLayoutParams();
                params.leftMargin = spielbereich.getWidth()/ 2 + Math.round(spielbereich.getWidth()
                        * azimutRelativ / KAMERABREITE_AZIMUT)-muecke.getWidth()/2;
                params.topMargin =spielbereich.getHeight()/2 - Math.round(spielbereich.getHeight()
                        *polarRelativ/KAMERABREITE_POLAR) - muecke.getHeight()/2;
                muecke.setLayoutParams(params);
            } else {
                muecke.setVisibility(View.GONE);
            }
            nummer++;
        }
    }

    private boolean istMueckeInKamera(float azimutRelativ, float polarRelativ) {
        return (Math.abs(azimutRelativ) <= KAMERABREITE_AZIMUT/2) && (Math.abs(polarRelativ)<= KAMERABREITE_POLAR/2);
    }

    @Override
    public void run() {
        zeitHerunterzaehlen();
    }

    @Override
    protected void onDestroy() {
        mp.release();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(this);
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float azimutKamera = sensorEvent.values[0];
        float polarKamera = -90- sensorEvent.values[1];
        mueckenPositionieren(azimutKamera, polarKamera);
        radar.setWinkel(-sensorEvent.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private class MueckeAnimationListener implements Animation.AnimationListener {
        private View muecke;

        public MueckeAnimationListener(View m) {
            muecke = m;
        }
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    spielbereich.removeView(muecke);
                }
            });
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }


}
