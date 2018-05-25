package de.androidnewcomer.mueckenfang;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Date;
import org.w3c.dom.Text;

import java.util.Random;

public class GameActivity extends Activity implements View.OnClickListener, Runnable{
    private static final long HOECHSTALTER_MS = 2000;
    private int punkte;
    private int runde;
    private int gefangeneMuecken;
    private int zeit;
    private float massstab;
    private int muecken;
    private ViewGroup spielbereich;
    private boolean spielLaeuft;
    private Random zufallsgenerator = new Random();
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        massstab = getResources().getDisplayMetrics().density;
        float zufallszahl = zufallsgenerator.nextFloat();
        spielStarten();
    }

    @Override
    public void run()
    {
        zeitHerunterzaehlen();
    }

    @Override
    public void onClick(View muecke)
    {
        gefangeneMuecken++;
        punkte += 100;
        bildschirmAktualisieren();
        spielbereich.removeView(muecke);
    }

    private void spielStarten()
    {
        spielLaeuft = true;
        runde = 0;
        punkte = 0;
        starteRunde();
    }

    private void starteRunde()
    {
        runde = runde + 1;
        muecken = runde * 10;
        gefangeneMuecken = 0;
        zeit = 60;
        bildschirmAktualisieren();
        handler.postDelayed(this, 1000);
    }

    private void bildschirmAktualisieren()
    {
        TextView tvPunkte = (TextView) findViewById(R.id.points);
        tvPunkte.setText(Integer.toString(punkte));
        TextView tvRunde = (TextView) findViewById(R.id.round);
        tvRunde.setText(Integer.toString(runde));
        TextView tvTreffer = (TextView) findViewById(R.id.hits);
        tvTreffer.setText(Integer.toString(gefangeneMuecken));
        TextView tvZeit = (TextView) findViewById(R.id.time);
        tvZeit.setText(Integer.toString(zeit));

        FrameLayout flTreffer = (FrameLayout) findViewById(R.id.bar_hits);
        FrameLayout flZeit = (FrameLayout) findViewById(R.id.bar_time);

        ViewGroup.LayoutParams lpTreffer = flTreffer.getLayoutParams();
        lpTreffer.width = Math.round(massstab * 300 * Math.min(gefangeneMuecken, muecken) / muecken );
        ViewGroup.LayoutParams lpZeit = flZeit.getLayoutParams();
        lpZeit.width = Math.round(massstab * zeit * 300 / 60 );
        spielbereich = (ViewGroup) findViewById(R.id.spielBereich);

    }

    private void zeitHerunterzaehlen()
    {
        zeit = zeit - 1;
        float zufallszahl = zufallsgenerator.nextFloat();
        double wahrscheinlichkeit = muecken * 1.5 / 60;

        if (wahrscheinlichkeit > 1)
        {
            eineMueckeAnzeigen();
            if (zufallszahl < wahrscheinlichkeit - 1)
            {
                eineMueckeAnzeigen();
            }
        }
        else
        {
            if (zufallszahl < wahrscheinlichkeit)
            {
                eineMueckeAnzeigen();
            }
        }
        mueckenVerschwinden();
        bildschirmAktualisieren();
        if(!pruefeSpielende())
        {
            pruefeRundenende();
        }
        if(!pruefeSpielende())
        {
            if(!pruefeRundenende())
            {
                handler.postDelayed(this, 1000);
            }
        }
    }

    private boolean pruefeSpielende()
    {
        if ( zeit == 0 && gefangeneMuecken < muecken )
        {
            gameOver();
            return true;
        }
        return false;
    }

    private boolean pruefeRundenende()
    {
        if (gefangeneMuecken >= muecken)
        {
            starteRunde();
            return true;
        }
        return false;
    }

    private void eineMueckeAnzeigen()
    {
        int breite = spielbereich.getWidth();
        int hoehe = spielbereich.getHeight();
        int muecke_breite = Math.round(massstab * 50);
        int muecke_hoehe = Math.round(massstab * 42);
        int links = zufallsgenerator.nextInt(breite - muecke_breite );
        int oben = zufallsgenerator.nextInt(hoehe - muecke_hoehe);
        ImageView muecke = new ImageView(this);
        muecke.setImageResource(R.drawable.muecke);
        muecke.setOnClickListener(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(muecke_breite, muecke_hoehe);
        params.leftMargin = links;
        params.topMargin = oben;
        params.gravity = Gravity.TOP + Gravity.LEFT;
        spielbereich.addView(muecke, params);
        muecke.setTag(R.id.geburtsdatum, new Date());
    }

    private void mueckenVerschwinden()
    {
        int nummer = 0;
        while (nummer < spielbereich.getChildCount())
        {
            ImageView muecke = (ImageView)spielbereich.getChildAt(nummer);
            Date geburtsdatum = (Date)muecke.getTag(R.id.geburtsdatum);
            long alter = (new Date()).getTime() - geburtsdatum.getTime();
            if (alter > HOECHSTALTER_MS)
            {
                spielbereich.removeView(muecke);
            }
            else
                {
                    nummer++;
                }
        }
    }

    private void gameOver()
    {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.gameover);
        dialog.show();
        spielLaeuft = false;
    }

}
