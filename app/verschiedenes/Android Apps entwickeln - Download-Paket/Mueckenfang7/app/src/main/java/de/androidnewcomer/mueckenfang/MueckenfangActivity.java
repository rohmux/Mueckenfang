package de.androidnewcomer.mueckenfang;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class MueckenfangActivity extends Activity implements View.OnClickListener, Html.ImageGetter {

    private Animation animationEinblenden;
    private Animation animationWackeln;
    private Button startButton;
    private Handler handler = new Handler();
    private Runnable wackelnRunnable = new WackleButton();
    private View namenseingabe;
    private Button speichern;
    private static final String HIGHSCORE_SERVER_BASE_URL = "http://myhighscoreserver.appspot.com/highscoreserver";
    private static final String HIGHSCORESERVER_GAME_ID = "mueckenfang";
    private ListView listView;
    private ToplistAdapter adapter;
    private List<String> highscoreList = new ArrayList<String>();
    private Spinner schwierigkeitsgrad;
    private ArrayAdapter<String> schwierigkeitsgradAdapter;

    class ToplistAdapter extends ArrayAdapter<String> {

        public ToplistAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public int getCount() {
            return highscoreList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null) {
                convertView = getLayoutInflater().inflate(R.layout.toplist_element,null);
            }
            TextView tvPlatz = (TextView) convertView.findViewById(R.id.platz);
            tvPlatz.setText(Integer.toString(position + 1) + ".");
            TextUtils.SimpleStringSplitter sss = new TextUtils.SimpleStringSplitter(',');
            sss.setString(highscoreList.get(position));
            TextView tvName = (TextView) convertView.findViewById(R.id.name);
            tvName.setText(sss.next());
            TextView tvPunkte = (TextView) convertView.findViewById(R.id.punkte);
            tvPunkte.setText(sss.next());


            return  convertView;
        }
    }

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
        namenseingabe.setVisibility(View.GONE);
        speichern = (Button) findViewById(R.id.speichern);
        speichern.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new ToplistAdapter(this,0);
        listView.setAdapter(adapter);
        schwierigkeitsgrad = (Spinner) findViewById(R.id.schwierigkeitsgrad);
        schwierigkeitsgradAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[] {"leicht","mittel","schwer"});
        schwierigkeitsgradAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schwierigkeitsgrad.setAdapter(schwierigkeitsgradAdapter);

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

        internetHighscores("",0);
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
            int s = schwierigkeitsgrad.getSelectedItemPosition();
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("schwierigkeitsgrad", s);
            startActivityForResult(intent,1);
        } else if(view.getId() == R.id.speichern) {
            schreibeHighscoreName();
            highscoreAnzeigen();
            namenseingabe.setVisibility(View.GONE);
            internetHighscores(leseHighscoreName(), leseHighscore());
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



    @Override
    public Drawable getDrawable(String name) {
        int id = getResources().getIdentifier(name, "drawable", this.getPackageName());
        Drawable d = getResources().getDrawable(id);
        d.setBounds(0, 0, 30, 30);
        return d;
    }

    private void internetHighscores(final String name, final int points) {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(HIGHSCORE_SERVER_BASE_URL
                            + "?game=" + HIGHSCORESERVER_GAME_ID
                            + "&name=" + URLEncoder.encode(name, "utf-8")
                            + "&points=" + Integer.toString(points)
                            + "&max=100");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    InputStreamReader input = new InputStreamReader(conn.getInputStream(), "utf-8");

                    BufferedReader reader = new BufferedReader(input,2000);
                    highscoreList.clear();
                    String line = reader.readLine();
                    while (line != null) {
                        highscoreList.add(line);
                        line = reader.readLine();
                    }

                } catch(IOException e) {
                    ;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetInvalidated();
                    }
                });
            }
        })).start();

    }

}
