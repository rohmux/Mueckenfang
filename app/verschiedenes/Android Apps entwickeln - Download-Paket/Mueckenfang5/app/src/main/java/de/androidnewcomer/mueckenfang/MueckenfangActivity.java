package de.androidnewcomer.mueckenfang;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;


public class MueckenfangActivity extends Activity implements View.OnClickListener {

    private Animation animationEinblenden;
    private Animation animationWackeln;
    private Button startButton;
    private Handler handler = new Handler();
    private Runnable wackelnRunnable = new WackleButton();
    private View namenseingabe;
    private Button speichern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
        animationEinblenden = AnimationUtils.loadAnimation(this,R.anim.einblenden);
        animationWackeln = AnimationUtils.loadAnimation(this,R.anim.wackeln);
        startButton = (Button) findViewById(R.id.button);
        namenseingabe = findViewById(R.id.namenseingabe);
        namenseingabe.setVisibility(View.INVISIBLE);
        speichern = (Button) findViewById(R.id.speichern);
        speichern.setOnClickListener(this);
    }


    private class WackleButton implements Runnable {
        @Override
        public void run() {
            startButton.startAnimation(animationWackeln);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        View v = findViewById(R.id.wurzel);
        v.startAnimation(animationEinblenden);
        handler.postDelayed(wackelnRunnable, 1000 * 10);

        highscoreAnzeigen();
    }

    private void highscoreAnzeigen() {
        TextView tv = (TextView) findViewById(R.id.highscore);
        int highscore = leseHighscore();
        if(highscore > 0) {
            tv.setText(Integer.toString(highscore) + " von " + leseHighscoreName());
        } else {
            tv.setText("-");
        }
    }

    private int leseHighscore() {
        SharedPreferences pref = getSharedPreferences("GAME", 0);
        return pref.getInt("HIGHSCORE", 0);
    }

    private void schreibeHighscoreName() {
        TextView tv = (TextView) findViewById(R.id.spielername);
        String name = tv.getText().toString().trim();
        SharedPreferences pref = getSharedPreferences("GAME", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("HIGHSCORE_NAME", name);
        editor.commit();
    }

    private String leseHighscoreName() {
        SharedPreferences pref = getSharedPreferences("GAME", 0);
        return pref.getString("HIGHSCORE_NAME", "");
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(wackelnRunnable);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button) {
            startActivityForResult(new Intent(this, GameActivity.class),1);
        } else if(view.getId() == R.id.speichern) {
            schreibeHighscoreName();
            highscoreAnzeigen();
            namenseingabe.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1) {
            if(resultCode > leseHighscore()) {
                schreibeHighscore(resultCode);
                namenseingabe.setVisibility(View.VISIBLE);
            }
        }
    }

    private void schreibeHighscore(int highscore) {
        SharedPreferences pref = getSharedPreferences("GAME", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("HIGHSCORE", highscore);
        editor.commit();
    }

}
