package com.test.kkl.testing01;

import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.flurgle.camerakit.CameraListener;
import com.test.kkl.testing01.databinding.ActivityMain2Binding;
import com.test.kkl.testing01.models.Thresholding;
import com.test.kkl.testing01.utils.ImageUtils;
import com.test.kkl.testing01.utils.MathUtils;
import com.xiaopo.flying.sticker.DrawableSticker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

public class MainActivity2 extends AppCompatActivity {

    ActivityMain2Binding mBinding;
    final Matrix.ScaleToFit BITMAP_SCALE_TO_FIT = Matrix.ScaleToFit.FILL;
    final int[] DRAWABLES = new int[] {
            R.drawable.data,
            R.drawable.test1,
            R.drawable.s8_day_1
    };
    int selectedIndex;
    RenderThread renderThread;
    Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_2);
        mBinding.stickerView.setLocked(true);
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
        mBinding.stickerView.setLayerType(View.LAYER_TYPE_HARDWARE, paint);
        mBinding.toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mBinding.stickerView.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
        });

        mBinding.next.setOnClickListener(v -> {
            setupBitmap(loadBitmap(nextResDrawable()));
        });

        selectedIndex = -1;
//        setupBitmap(loadBitmap(nextResDrawable()));

        setupCamera();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBinding.camera.start();
        startThread();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopThread();
        mBinding.camera.stop();
    }

    protected void setupTouch() {
        mBinding.image.setOnTouchListener((v, e) -> {
            final Bitmap bitmap = ((BitmapDrawable)mBinding.image.getDrawable()).getBitmap();
            Matrix invertMat = new Matrix();
            mBinding.image.getImageMatrix().invert(invertMat);

            float[] touchPoint = new float[] {e.getX(), e.getY()};
            invertMat.mapPoints(touchPoint);
            int xCoord = Integer.valueOf((int)touchPoint[0]);
            int yCoord = Integer.valueOf((int)touchPoint[1]);
            boolean noColor = false;
            if (xCoord >= bitmap.getWidth()) {
                xCoord = bitmap.getWidth() - 1;
                noColor = true;
            }
            if (yCoord >= bitmap.getHeight()) {
                yCoord = bitmap.getHeight() - 1;
                noColor = true;
            }
            if (yCoord < 0) {
                yCoord = 0;
                noColor = true;
            }
            if (xCoord < 0) {
                xCoord = 0;
                noColor = true;
            }
//            drawableSticker.getMatrix().setScale(5,5);
//            drawableSticker.getMatrix().postTranslate(e.getX(), e.getY());

            int color = bitmap.getPixel(xCoord, yCoord);
            if (noColor) {
                color = ContextCompat.getColor(getBaseContext(), android.R.color.transparent);
            }
            mBinding.indicator.setBackgroundColor(color);

            mBinding.stickerView.invalidate();
            Log.v("kkl", "x: " + xCoord + ", y: " + yCoord + ", rawX: " + e.getX() + ", rawY: " + e.getY());
            Log.v("kkl", getMappedPoints((int)convertDpToPixel(10), (int)convertDpToPixel(10)).toString());
            return true;
        });
    }

    protected void setupCamera() {
        mBinding.camera.setCameraListener(new CameraListener() {
            @Override
            public void onCameraOpened() {
                mBinding.camera.setPreviewCallback((data, camera) -> {
                    renderThread.camera = camera;
                    renderThread.data = data;
                });
            }
        });
    }

    protected void stopThread() {
        if (renderThread != null) {
            renderThread.stopRender();
            renderThread = null;
        }
    }

    protected void startThread() {
        if (renderThread == null) {
            renderThread = new RenderThread();
            renderThread.start();
        }
    }

    class RenderThread extends Thread {
        public Camera camera;
        public byte[] data;
        public boolean isRunning = true;
        int SLEEP_PERIOD = 10;

        public void stopRender() {
            isRunning = false;
        }

        @Override
        public void run() {
            while (isRunning) {
                if (camera == null || data == null) {
                    try {
                        Thread.sleep(SLEEP_PERIOD);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                Camera.Parameters parameters = camera.getParameters();

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                YuvImage yuvImage = new YuvImage(data, parameters.getPreviewFormat(), parameters.getPreviewSize().width, parameters.getPreviewSize().height, null);
                yuvImage.compressToJpeg(new Rect(0, 0, parameters.getPreviewSize().width, parameters.getPreviewSize().height), 90, out);
                byte[] imageBytes = out.toByteArray();
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                setupBitmap(bitmap);
                try {
                    Thread.sleep(SLEEP_PERIOD);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected int nextResDrawable() {
        selectedIndex++;
        if (selectedIndex >= DRAWABLES.length) {
            selectedIndex = 0;
        }
        return DRAWABLES[selectedIndex];
    }

    protected void setupBitmap(final Bitmap bitmap) {
        Bitmap thresholdBitmap = createThresholdBitmap(bitmap);
        thresholdBitmap = ImageUtils.rotateBitmap(thresholdBitmap, 90);
        Point[] points = ImageUtils.getWhitePoints(thresholdBitmap);
        Matrix matrix = MathUtils.scaleToFit(
                thresholdBitmap.getWidth(),
                thresholdBitmap.getHeight(),
                mBinding.image.getWidth(),
                mBinding.image.getHeight(),
                BITMAP_SCALE_TO_FIT
        );
        addGlitters(matrix, points);
    }

    protected void addGlitters(Matrix matrix, Point[] whitePoints) {
        mBinding.stickerView.post(() -> {
            mBinding.stickerView.removeAllStickers();

            int noOfGlitters = 0;
            for(final Point p: whitePoints) {
                noOfGlitters++;
                if (noOfGlitters >= 200) {
                    break;
                }

                float[] dest = new float[] {
                        p.x,
                        p.y
                };
                matrix.mapPoints(dest);
                addGlitter((int)dest[0], (int)dest[1]);
            }
        });
    }

    protected int getRandom() {
        int randomThreshold = (int)convertDpToPixel(10);
        int finalRand = random.nextInt(randomThreshold + 1 + randomThreshold) - randomThreshold;
        return finalRand;
    }

    protected void addGlitter(final int x, final int y) {
        final DrawableSticker drawableSticker = createDrawableSticker();
        mBinding.stickerView.addSticker(drawableSticker);
        Matrix matrix = drawableSticker.getMatrix();
        matrix.setScale(1,1);
        Point mappedCentrePoint = MathUtils.getMappedCentrePoint(
                x,
                y,
                (int)drawableSticker.getCurrentWidth(),
                (int)drawableSticker.getCurrentHeight()
        );
        matrix.postTranslate(mappedCentrePoint.x + getRandom(), mappedCentrePoint.y + getRandom());
        mBinding.stickerView.postInvalidate();
    }

    protected Bitmap createThresholdBitmap(Bitmap inBitmap) {
        final Bitmap scaledInBitmap = createScaledAspectRatioBitmap(inBitmap, 15);
        final Bitmap thresholdBitmap = copyBitmap(scaledInBitmap);
        Thresholding.process(getBaseContext(), scaledInBitmap, thresholdBitmap, 200);
        return thresholdBitmap;
    }

    protected Bitmap createScaledAspectRatioBitmap(Bitmap bitmap, int targetWidth) {
        float ratio = (float)targetWidth / bitmap.getWidth();
        int targetHeight = (int) (bitmap.getHeight() * ratio);
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false);
    }

    protected DrawableSticker createDrawableSticker() {
        return new DrawableSticker(ContextCompat.getDrawable(this, R.drawable.sparkle2));
    }

    protected Bitmap copyBitmap(Bitmap bitmap) {
        return bitmap.copy(bitmap.getConfig(), true);
    }

    protected Point getMappedPoints(int x, int y) {
        final Bitmap bm = ((BitmapDrawable)mBinding.image.getDrawable()).getBitmap();
        float ratioX = mBinding.image.getWidth()
                / (float)bm.getWidth();
        float ratioY = mBinding.image.getHeight()
                / (float)bm.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(ratioX, ratioY);
        float[] dest = new float[2];
        matrix.mapPoints(dest, new float[]{x, y});
        return new Point((int)(dest[0]), (int)(dest[1]));
    }

    public static float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    private Bitmap loadBitmap(int resource) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeResource(getResources(), resource, options);
    }
}
