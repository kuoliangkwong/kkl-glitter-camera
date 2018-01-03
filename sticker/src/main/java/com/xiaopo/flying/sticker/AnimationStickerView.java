package com.xiaopo.flying.sticker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by Kuo Liang on 27-Sep-17.
 */

public class AnimationStickerView extends StickerView {

    private long animInterval = 100;
    private AnimationTask renderTask;
    private final List<Sticker> gifStickers = new ArrayList<>();

    public AnimationStickerView(Context context) {
        super(context);
    }

    public AnimationStickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimationStickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void addStickerImmediately(@NonNull Sticker sticker, int position) {
        if (sticker.getDrawable() instanceof GifDrawable) {
            gifStickers.add(sticker);
        }
        super.addStickerImmediately(sticker, position);
    }

    @Override
    public boolean remove(@Nullable Sticker sticker) {
        gifStickers.remove(sticker);
        return super.remove(sticker);
    }

    @Override
    public void removeAllStickers() {
        gifStickers.clear();
        super.removeAllStickers();
    }

    protected void playStickerInternal(Sticker sticker) {
        Drawable drawable = sticker.getDrawable();
        if (drawable instanceof GifDrawable) {
            stopAllStickers();
            GifDrawable gifDrawable = (GifDrawable) drawable;
            gifDrawable.start();
        }
    }

    protected void playAllStickersInternal() {
        for (Sticker sticker: gifStickers) {
            GifDrawable gifDrawable = (GifDrawable) sticker.getDrawable();
            gifDrawable.start();
        }
    }

    protected void stopAllStickersInternal() {
        for (Sticker sticker: gifStickers) {
            GifDrawable gifDrawable = (GifDrawable) sticker.getDrawable();
            gifDrawable.stop();
        }
    }

    public void playSticker(final Sticker sticker) {
        if (ViewCompat.isLaidOut(this)) {
            playStickerInternal(sticker);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    playStickerInternal(sticker);
                }
            });
        }
        play();
    }

    public void playAllStickers() {
        if (ViewCompat.isLaidOut(this)) {
            playAllStickersInternal();
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    playAllStickersInternal();
                }
            });
        }
        play();
    }

    public void stopAllStickers() {
        if (ViewCompat.isLaidOut(this)) {
            stopAllStickersInternal();
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    stopAllStickersInternal();
                }
            });
        }
        stop();
    }

    public void play() {
        stop();
        renderTask = new AnimationTask();
        renderTask.execute();
    }

    public void stop() {
        if (renderTask == null) {
            return;
        }
        renderTask.cancel(true);
        renderTask = null;
    }

    public class AnimationTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onProgressUpdate(Void... values) {
            if (isCancelled()) {
                return;
            }
            AnimationStickerView.this.postInvalidateOnAnimation();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            while(!isCancelled()) {
                try {
                    Thread.sleep(animInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                publishProgress();
            }
            return null;
        }
    }
}
