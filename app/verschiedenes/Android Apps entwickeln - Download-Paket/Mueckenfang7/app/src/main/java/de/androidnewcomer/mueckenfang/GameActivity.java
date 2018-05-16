package de.androidnewcomer.mueckenfang;

import android.app.Activity;
import android.app.Dialog;
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
public class GameActivity extends Activity implements View.OnClickListener, Runnable {

    private static final long HOECHSTALTER_MS = 2000;
    public static final int DELAY_MILLIS = 100;
    public static final int ZEITSCHEIBEN = 600;
    private static final int MUECKEN_BILDER[][] = {
            {R.drawable.muecke_nw, R.drawable.muecke_n, R.drawable.muecke_no},
            {R.drawable.muecke_w,  R.drawable.muecke,   R.drawable.muecke_o},
            {R.drawable.muecke_sw, R.drawable.muecke_s, R.drawable.muecke_so}};
    private static final String HIMMELSRICHTUNGEN[][] = {
            {"nw", "n", "no"},
            {"w",  "", "o"},
            {"sw", "s", "so"}};
    private int punkte;
    private int runde;
    private int gefangeneMuecken;
    private int zeit;
    private float massstab;
    private int muecken;
    private Random zufallsgenerator = new Random();
    private ViewGroup spielbereich;
    private boolean spielLaeuft;
    private Handler handler = new Handler();
    private MediaPlayer mp;
    private int schwierigkeitsgrad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        massstab = getResources().getDisplayMetrics().density;
        spielbereich = (ViewGroup) findViewById(R.id.spielbereich);
        mp = MediaPlayer.create(this,R.raw.summen);
        schwierigkeitsgrad = getIntent().getIntExtra("schwierigkeitsgrad",0);
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
        mueckenBewegen();
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

    private void mueckenBewegen() {
        int nummer=0;
        while(nummer < spielbereich.getChildCount()) {
            ImageView muecke = (ImageView) spielbereich.getChildAt(nummer);
            int vx = (Integer) muecke.getTag(R.id.vx);
            int vy = (Integer) muecke.getTag(R.id.vy);
            // und nun bewegen...
            FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) muecke.getLayoutParams();
            params.leftMargin += vx*runde;
            params.topMargin += vy*runde;
            muecke.setLayoutParams(params);
            nummer++;
        }
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

        // Bewegungsvektor erzeugen
        int vx;
        int vy;
        do {
            vx = zufallsgenerator.nextInt(3)-1;
            vy = zufallsgenerator.nextInt(3)-1;
        } while(vx==0 && vy==0);

        muecke.setTag(R.id.vx, new Integer(vx));
        muecke.setTag(R.id.vy, new Integer(vy));

        setzeBild(muecke, vx, vy);

        // Geschwindigkeitskorrektur für schräge Mücken
        double faktor = 1.0;
        if(vx != 0 && vy != 0) {
            faktor = 0.70710678;
        }

        vx = (int) Math.round(massstab*faktor*vx);
        vy = (int) Math.round(massstab*faktor*vy);

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

    private void setzeBild(ImageView muecke, int vx, int vy) {
        // setzen des richtigen Bildes mit dem zweidimensionalen Array
        muecke.setImageResource(MUECKEN_BILDER[vy+1][vx+1]);

        // alternativ: setzen über Himmelsrichtungen und Bildname
        //muecke.setImageResource(getResources().getIdentifier("muecke_"+HIMMELSRICHTUNGEN[vy+1][vx+1], "drawable", getPackageName()));
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
        super.onPause();
        handler.removeCallbacks(this);
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
