package dg.shenm233.wechatmod.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class CircleImageView extends ImageView {
    private static Bitmap mBitmap;
    private static Canvas mCanvas = new Canvas();
    private final static Paint mBitmapPaint = new Paint();
    private static BitmapShader mBitmapShader;
    private static float radius;

    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onDraw(Canvas imageViewCanvas) {
        //Hack for wechat!
        try {
            final int mMeasuredWidth = getMeasuredWidth();
            final int mMeasuredHeight = getMeasuredHeight();

            if (mMeasuredWidth <= 0 || mMeasuredHeight <= 0) {
                Log.d("WechatMOD", "getMeasuredWidth() or getMeasuredHeight() <= 0  stop drawing!");
                return;
            }

            if (mBitmap == null) {
                mBitmap = Bitmap.createBitmap(mMeasuredWidth, mMeasuredHeight, Bitmap.Config.ARGB_8888);
                Log.d("WechatMOD", "created bitmap! width=" + mMeasuredWidth + " height=" + mMeasuredHeight);
                mCanvas.setBitmap(mBitmap);
                radius = Math.min(mMeasuredWidth, mMeasuredHeight) / 2;
                mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                mBitmapPaint.setAntiAlias(true);
                mBitmapPaint.setShader(mBitmapShader);
            }

            //draw bitmap on canvas for getting another bitmap
            super.onDraw(mCanvas);

            imageViewCanvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, mBitmapPaint);

            Log.d("WechatMOD", "draw Circle!");
        } catch (OutOfMemoryError e) {
            super.onDraw(imageViewCanvas);
            Log.d("WechatMOD", "[OutOfMemoryError] draw original!");
        }
    }
}
