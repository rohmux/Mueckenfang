package de.androidnewcomer.mueckenfang;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.w3c.dom.Text;

import java.util.Random;

public class GameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        private float massstab;
        massstab = getResources().getDisplayMetrics().density;
        private Random zufallsgenerator = new Random();
        float zufallszahl = zufallsgenerator.nextFloat();
        spielStarten();
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
    }
}
