package com.test.kkl.testing01.utils;

import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;

/**
 * Created by Kuo Liang on 27-Dec-17.
 */

public class MathUtils {

    public static Point getMappedPoint(
            int srcWidth, int srcHeight,
            int targetWidth, int targetHeight,
            int x, int y
    ) {
        int convertedX = (targetWidth * x / srcWidth);
        int convertedY = (targetHeight * y / srcHeight);
        return new Point(convertedX, convertedY);
    }

    public static Matrix scaleToFit(int srcWidth, int srcHeight,
                                int targetWidth, int targetHeight, Matrix.ScaleToFit scaleToFit) {
        RectF srcRect = new RectF(0, 0, srcWidth, srcHeight);
        RectF targetRect = new RectF(0, 0, targetWidth, targetHeight);
        Matrix resultMatrix = new Matrix();
        resultMatrix.setRectToRect(srcRect, targetRect, scaleToFit);
        return resultMatrix;
    }

    public static Point getMappedCentrePoint(int x, int y, int width, int height) {
        return new Point(x - (int)(width * 1f / 2), y - (int)(height * 1f / 2));
    }
}
