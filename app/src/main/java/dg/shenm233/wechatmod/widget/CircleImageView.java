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
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private final Paint mBitmapPaint = new Paint();
    private BitmapShader mBitmapShader;
    private float radius;

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
            //draw bitmap on canvas for getting another bitmap
            super.onDraw(mCanvas);

            if (mBitmap == null) {
                mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                mCanvas = new Canvas(mBitmap);
                radius = Math.min(getWidth(), getHeight()) / 2;
            }

            //clear
            mCanvas.drawColor(0xffffff);
            mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mBitmapPaint.setAntiAlias(true);
            mBitmapPaint.setShader(mBitmapShader);

            //real drawing on ImageView
            imageViewCanvas.drawColor(0xffffff);
            imageViewCanvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, mBitmapPaint);

            Log.d("WechatMOD", "draw Circle!");
        } catch (OutOfMemoryError e) {
            super.onDraw(imageViewCanvas);
            Log.d("WechatMOD", "[OutOfMemoryError] draw original!");
        }
    }
}
