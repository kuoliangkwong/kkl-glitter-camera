package com.test.kkl.testing01.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;

import java.util.ArrayList;

/**
 * Created by Kuo Liang on 27-Dec-17.
 */

public class ImageUtils {

    public static Point[] getWhitePoints(Bitmap bitmap) {
        ArrayList<Point> points = new ArrayList<>();
        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                int color = bitmap.getPixel(x, y);
                if (Color.red(color) >= 255) {
                    points.add(new Point(x, y));
                }
            }
        }
        Point[] result = new Point[points.size()];
        points.toArray(result);
        return result;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(),true);
        return Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);
    }
}
