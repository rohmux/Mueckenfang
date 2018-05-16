package de.androidnewcomer.textviewtricks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // bitmap texture
        Bitmap bitmap = BitmapFactory.decodeResource(
                getResources(), R.drawable.my_bitmap_texture);
        Shader shader = new BitmapShader(bitmap,
                Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        ((TextView)findViewById(R.id.textView1)).getPaint().setShader(shader);

        // outline
        findViewById(R.id.textView2).setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ((TextView)findViewById(R.id.textView2)).getPaint().setStyle(Paint.Style.STROKE);

        // Emboss filter
        EmbossMaskFilter filter = new EmbossMaskFilter(
                new float[] { 0f, 1f, 0.8f }, 0.6f, 3f, 2f);
        findViewById(R.id.textView3).setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ((TextView)findViewById(R.id.textView3)).getPaint().setMaskFilter(filter);

        // Blur filter
        TextView textView = (TextView) findViewById(R.id.textView4);
        textView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        BlurMaskFilter blur = new BlurMaskFilter(textView.getTextSize() / 10, BlurMaskFilter.Blur.NORMAL);
        textView.getPaint().setMaskFilter(blur);



    }
}
