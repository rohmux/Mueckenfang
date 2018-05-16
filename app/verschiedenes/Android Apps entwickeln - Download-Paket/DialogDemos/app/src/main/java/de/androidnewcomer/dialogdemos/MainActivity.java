package de.androidnewcomer.dialogdemos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends Activity implements View.OnClickListener {


    private TextView ergebnis;
    private Handler handler = new Handler();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ergebnis = (TextView) findViewById(R.id.ergebnis);
        findViewById(R.id.demo_alert).setOnClickListener(this);
        findViewById(R.id.demo_progress).setOnClickListener(this);
        findViewById(R.id.demo_datepicker).setOnClickListener(this);
        findViewById(R.id.demo_timepicker).setOnClickListener(this);
        findViewById(R.id.demo_toast).setOnClickListener(this);
        findViewById(R.id.demo_eigener).setOnClickListener(this);
        findViewById(R.id.demo_eigener_toast).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.demo_alert) {

            DialogInterface.OnClickListener positivListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ergebnis.setText("Hinweis akzeptiert");
                    dialog.dismiss();
                }
            };

            DialogInterface.OnClickListener negativListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ergebnis.setText("Hinweis abgelehnt");
                    dialog.dismiss();
                }
            };

            (new AlertDialog.Builder(this)).setMessage("Ich bin ein Hinweis.")
                    .setPositiveButton("akzeptiert", positivListener )
                    .setNegativeButton("nein", negativListener)
                    .show();

        }
        if(v.getId()==R.id.demo_progress) {
            final ProgressDialog dialog = ProgressDialog.show(this, "Bitte warten",  "Nur ein paar Sekunden...", true);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ergebnis.setText("genug gewartet");
                    dialog.dismiss();
                }
            }, 3000);
        }
        if(v.getId()==R.id.demo_datepicker) {
            final Calendar heute = Calendar.getInstance();
            int jahr = heute.get(Calendar.YEAR);
            int monat = heute.get(Calendar.MONTH); // Januar = 0, Dezember = 11
            int tag = heute.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    ergebnis.setText("Datum: " + dayOfMonth + "." + (monthOfYear+1) + "." + year);
                }
            };
            DatePickerDialog dialog = new DatePickerDialog(this, listener , jahr, monat, tag);
            dialog.show();
        }
        if(v.getId()==R.id.demo_timepicker) {
            final Calendar heute = Calendar.getInstance();
            int stunde = heute.get(Calendar.HOUR_OF_DAY);
            int minute = heute.get(Calendar.MINUTE);
            TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    ergebnis.setText("Uhrzeit: " + hourOfDay + ":" + minute);
                }
            };
            TimePickerDialog dialog = new TimePickerDialog(this,listener,stunde,minute,true);
            dialog.show();
        }
        if(v.getId()==R.id.demo_eigener) {
            final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            dialog.setContentView(R.layout.eigenerdialog);
            TextView inhalt = (TextView) dialog.findViewById(R.id.inhalt);
            inhalt.setText("Eigene Dialoge unterliegen keinen Layout-Einschränkungen.");
            Button button = (Button) dialog.findViewById(R.id.ok);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
        if(v.getId()==R.id.demo_toast) {
            Toast.makeText(this, "Na dann Prost!", Toast.LENGTH_LONG).show();
        }
        if(v.getId()==R.id.demo_eigener_toast) {
            View layout = getLayoutInflater().inflate(R.layout.eigenertoast, null);
            Toast toast = new Toast(this);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }

    }
}
