package com.flurgle.camerakit;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.Surface;

public abstract class DisplayOrientationDetector {

    private final OrientationEventListener mOrientationEventListener;

    static final SparseIntArray DISPLAY_ORIENTATIONS = new SparseIntArray();
    static {
        DISPLAY_ORIENTATIONS.put(Surface.ROTATION_0, 0);
        DISPLAY_ORIENTATIONS.put(Surface.ROTATION_90, 90);
        DISPLAY_ORIENTATIONS.put(Surface.ROTATION_180, 180);
        DISPLAY_ORIENTATIONS.put(Surface.ROTATION_270, 270);
    }

    private Display mDisplay;

    private int mLastKnownDisplayOrientation = 0;

    public DisplayOrientationDetector(Context context) {
        mOrientationEventListener = new OrientationEventListener(context) {

            private int mLastKnownRotation = -1;

            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN || mDisplay == null) {
                    return;
                }

                int rotation = mDisplay.getRotation();
                if (orientation >= 330 || orientation < 30) {
                    rotation = Surface.ROTATION_0;
                } else if (orientation >= 60 && orientation < 120) {
                    rotation = Surface.ROTATION_90;
                } else if (orientation >= 150 && orientation < 210) {
                    rotation = Surface.ROTATION_180;
                } else if (orientation >= 240 && orientation < 300) {
                    rotation = Surface.ROTATION_270;
                }
                if (mLastKnownRotation != rotation) {
                    mLastKnownRotation = rotation;
                    dispatchOnDisplayOrientationChanged(DISPLAY_ORIENTATIONS.get(rotation));
                }
            }

        };
    }

    public void enable(Display display) {
        mDisplay = display;
        mOrientationEventListener.enable();
        dispatchOnDisplayOrientationChanged(DISPLAY_ORIENTATIONS.get(display.getRotation()));
    }

    public void disable() {
        mOrientationEventListener.disable();
        mDisplay = null;
    }

    public int getLastKnownDisplayOrientation() {
        return mLastKnownDisplayOrientation;
    }

    void dispatchOnDisplayOrientationChanged(int displayOrientation) {
        mLastKnownDisplayOrientation = displayOrientation;
        onDisplayOrientationChanged(displayOrientation);
    }

    public abstract void onDisplayOrientationChanged(int displayOrientation);

}