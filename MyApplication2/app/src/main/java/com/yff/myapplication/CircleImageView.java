package com.yff.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class CircleImageView extends AppCompatImageView {


    private int width;
    private int height;
    private float radius;
    private Xfermode xfermode;
    private Paint paint;
    private Path path; // 用来裁剪图片的path


    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        paint = new Paint();
        path = new Path();

        xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        setScaleType(ScaleType.CENTER_CROP);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);
        super.onDraw(canvas);
        path.addCircle(width / 2.0f, height / 2.0f, radius, Path.Direction.CCW);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(xfermode);
        canvas.drawPath(path, paint);
        paint.setXfermode(null);
        canvas.restore();
    }

    private void initSrcRectF() {
        radius = Math.min(width, height) / 2.0f;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        initSrcRectF();
    }

}
