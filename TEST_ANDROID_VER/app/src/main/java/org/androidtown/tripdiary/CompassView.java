package org.androidtown.tripdiary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by kms0080 on 2017-06-06.
 */

public class CompassView extends View {
    private Drawable mCompass;
    private float mAzimuth = 0;
    private int PADDING = 2;

    public CompassView(Context ctx) {
        super(ctx);

        this.mCompass = ctx.getResources().getDrawable(R.drawable.arrow_n);
    }

    protected void onDraw(Canvas canvas) {
        canvas.save();

        canvas.rotate(360 - mAzimuth, PADDING + mCompass.getMinimumWidth()
                / 2, PADDING + mCompass.getMinimumHeight() / 2);
        mCompass.setBounds(PADDING, PADDING, PADDING
                + mCompass.getMinimumWidth(), PADDING
                + mCompass.getMinimumHeight());

        mCompass.draw(canvas);
        canvas.restore();

        super.onDraw(canvas);
    }

    public void setAzimuth(float aAzimuth) {
        mAzimuth = aAzimuth;
    }

}
