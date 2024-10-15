package com.antorian.legendsbackground;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.HandlerThread;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public class TiledWallpaperService extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new TiledEngine();
    }

    private class TiledEngine extends Engine {
        private Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mml);
        private Paint paint = new Paint();
        private float offsetX = 0;
        private float offsetY = 0;
        private boolean visible = true;
        private Handler handler;
        private final Runnable drawRunnable = new Runnable() {
            @Override
            public void run() {
                drawFrame();
            }
        };
        private long lastFrameTime;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            HandlerThread thread = new HandlerThread("WallpaperThread");
            thread.start();
            handler = new Handler(thread.getLooper());
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                lastFrameTime = System.currentTimeMillis();
                drawFrame();
            } else {
                handler.removeCallbacks(drawRunnable);
            }
        }

        private void drawFrame() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    canvas.drawColor(0xFF000000);  // Clear background

                    // Calculate time elapsed
                    long currentTime = System.currentTimeMillis();
                    float deltaTime = (currentTime - lastFrameTime) / 1000.0f;
                    lastFrameTime = currentTime;

                    // Update offsets
                    float speed = 150.0f;  // Speed in pixels per second
                    offsetX += speed * deltaTime;
                    offsetY += speed * deltaTime;
                    if (offsetX >= width) {
                        offsetX = 0;
                    }
                    if (offsetY >= height) {
                        offsetY = 0;
                    }

                    for (int y = (int) offsetY - height; y < canvas.getHeight(); y += height) {
                        for (int x = (int) offsetX - width; x < canvas.getWidth(); x += width) {
                            canvas.drawBitmap(bitmap, x, y, paint);
                        }
                    }
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
            handler.removeCallbacks(drawRunnable);
            if (visible) {
                handler.postDelayed(drawRunnable, 16);  // Approx. 60 fps
            }
        }
    }
}
