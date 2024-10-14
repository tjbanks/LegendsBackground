package com.antorian.legendsbackground;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = findViewById(R.id.tiledImageView);
        imageView.setImageDrawable(new TiledImageDrawable());
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageView imageView = findViewById(R.id.tiledImageView);
        TiledImageDrawable drawable = new TiledImageDrawable();
        imageView.setImageDrawable(drawable);
        new Thread(() -> {
            while (true) {
                runOnUiThread(() -> drawable.invalidateSelf());
                try {
                    Thread.sleep(100); // Adjust for frame rate
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private class TiledImageDrawable extends Drawable {
        private Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mml);
        private Paint paint = new Paint();

        private float offsetX = 0;
        private float offsetY = 0;

        @Override
        public void draw(Canvas canvas) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            for (int y = (int) (-height + offsetY); y < canvas.getHeight(); y += height) {
                for (int x = (int) (-width + offsetX); x < canvas.getWidth(); x += width) {
                    canvas.drawBitmap(bitmap, x, y, paint);
                }
            }

            // Update offsets for animation
            offsetX -= 2; // Speed of horizontal movement
            offsetY -= 2; // Speed of vertical movement

            if (offsetX <= -width) {
                offsetX = 0;
            }
            if (offsetY <= -height) {
                offsetY = 0;
            }

            // Redraw
            invalidateSelf();
        }


        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }
    }
}