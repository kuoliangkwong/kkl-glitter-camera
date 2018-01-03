package com.xiaopo.flying.sticker;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

/**
 * @author wupanjie
 */
public class DrawableSticker extends Sticker {

  private Drawable drawable;
  private Rect realBounds;
  private Paint paint;

  public DrawableSticker(Drawable drawable) {
    this.drawable = drawable;
    realBounds = new Rect(0, 0, getWidth(), getHeight());
    paint = new Paint();
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
  }

  @NonNull @Override public Drawable getDrawable() {
    return drawable;
  }

  @Override public DrawableSticker setDrawable(@NonNull Drawable drawable) {
    this.drawable = drawable;
    return this;
  }

  @Override public void draw(@NonNull Canvas canvas) {
    canvas.save();
//    int count = canvas.saveLayer(new RectF(realBounds), paint);
    canvas.concat(getMatrix());
    drawable.setBounds(realBounds);
    drawable.draw(canvas);
//    canvas.restoreToCount(count);
    canvas.restore();
  }

  @NonNull @Override public DrawableSticker setAlpha(@IntRange(from = 0, to = 255) int alpha) {
    drawable.setAlpha(alpha);
    return this;
  }

  @Override public int getWidth() {
    return drawable.getIntrinsicWidth();
  }

  @Override public int getHeight() {
    return drawable.getIntrinsicHeight();
  }

  @Override public void release() {
    super.release();
    if (drawable != null) {
      drawable = null;
    }
  }
}
