package com.test.kkl.testing01.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;

import com.example.android.rs.hellocompute.ScriptC_thresholding;
import com.test.kkl.testing01.R;

public class Thresholding {

	public static void process(
			Context context,
			Bitmap inBitmap,
			Bitmap outBitmap,
			int threshold) {

		RenderScript rs = RenderScript.create(context);

		Allocation inAllocation = Allocation.createFromBitmap(
				rs,
				inBitmap,
				Allocation.MipmapControl.MIPMAP_NONE,
				Allocation.USAGE_SCRIPT
		);
		Allocation outAllocation = Allocation.createTyped(
				rs,
				inAllocation.getType()
		);

		ScriptC_thresholding script = new ScriptC_thresholding(
				rs,
				context.getResources(),
				R.raw.thresholding
		);

		script.set_gIn(inAllocation);
		script.set_gOut(outAllocation);
		script.set_gScript(script);

		script.set_threshold(threshold/255.0f);
		script.invoke_filter();
		outAllocation.copyTo(outBitmap);
	}
}
