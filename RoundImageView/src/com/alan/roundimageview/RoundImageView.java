package com.alan.roundimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alan.myimageview.R;


public class RoundImageView extends ImageView
{
    
    private Context mContext;
    private int defaultColor=0xFFFFFFFF;// 边框默认色值
    private int mBorderThickness=0;// 边框厚度
    private int mBorderOutsideColor=0;// 外边框色值
    private int mBorderInsideColor=0;// 内边框色值
    private int defaultWidth=0;// 控件宽度
    private int defaultHeight=0;// 控件高度
    Paint borderPaint=new Paint();
    
    public RoundImageView(Context context){
        super(context);
        mContext=context;
        init(null);
    }
    
    public RoundImageView(Context context, AttributeSet attrs){
        super(context, attrs);
        mContext=context;
        init(attrs);
    }
    
    public RoundImageView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        mContext=context;
        init(attrs);
    }
    
    private void init(AttributeSet attrs)
    {
        if(attrs != null)
        {
            TypedArray a=mContext.obtainStyledAttributes(attrs, R.styleable.roundedimageview);
            mBorderThickness=a.getDimensionPixelSize(R.styleable.roundedimageview_border_thickness, 0);
            mBorderOutsideColor=a.getColor(R.styleable.roundedimageview_border_outside_color, defaultColor);
            mBorderInsideColor=a.getColor(R.styleable.roundedimageview_border_inside_color, defaultColor);
            a.recycle();
        }
        borderPaint.setAntiAlias(true);
        borderPaint.setFilterBitmap(true);
        borderPaint.setDither(true);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(mBorderThickness);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable=getDrawable();
        if(drawable == null || drawable.getClass() == NinePatchDrawable.class || getMeasuredWidth() == 0 || getMeasuredHeight() == 0){
            return;
        }
        Bitmap b=((BitmapDrawable)drawable).getBitmap();
        Bitmap bitmap=b.copy(Bitmap.Config.ARGB_8888, true);
        if(defaultWidth == 0){
            defaultWidth=getMeasuredWidth();
        }
        if(defaultHeight == 0){
            defaultHeight=getMeasuredHeight();
        }
        // 保证重新读取图片后不会因为图片大小而改变控件宽、高的大小（针对宽、高为wrap_content布局的imageview，但会导致margin无效）
        // if(defaultWidth != 0 && defaultHeight != 0){
        // ViewGroup.LayoutParams params=new
        // ViewGroup.LayoutParams(defaultWidth, defaultHeight);
        // setLayoutParams(params);
        // }
        int radius=0;
        if(mBorderInsideColor != defaultColor && mBorderOutsideColor != defaultColor)
        {
            radius=(defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - 2 * mBorderThickness;
            drawCircleBorder(canvas, radius + mBorderThickness / 2, mBorderInsideColor);
            drawCircleBorder(canvas, radius + mBorderThickness + mBorderThickness / 2, mBorderOutsideColor);
        }
        else if(mBorderInsideColor != defaultColor && mBorderOutsideColor == defaultColor)
        {// 定义画一个边框
            radius=(defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - mBorderThickness;
            drawCircleBorder(canvas, radius + mBorderThickness / 2, mBorderInsideColor);
        }
        else if(mBorderInsideColor == defaultColor && mBorderOutsideColor != defaultColor)
        {// 定义画一个边框
            radius=(defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - mBorderThickness;
            drawCircleBorder(canvas, radius + mBorderThickness / 2, mBorderOutsideColor);
        }
        else{// 没有边框
            radius=(defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2;
        }
        Bitmap roundBitmap=getCroppedRoundBitmap(bitmap, radius);
        canvas.drawBitmap(roundBitmap, defaultWidth / 2 - radius, defaultHeight / 2 - radius, null);
    }
    
    public Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
        Bitmap scaledSrcBmp;
        int diameter=radius * 2;
        // 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
        int bmpWidth=bmp.getWidth();
        int bmpHeight=bmp.getHeight();
        int squareWidth=0, squareHeight=0;
        int x=0, y=0;
        Bitmap squareBitmap;
        if(bmpHeight > bmpWidth){// 高大于宽
            squareWidth=squareHeight=bmpWidth;
            x=0;
            y=(bmpHeight - bmpWidth) / 2;
            squareBitmap=Bitmap.createBitmap(bmp, x, y, squareWidth, squareHeight);
        }
        else if(bmpHeight < bmpWidth){// 宽大于高
            squareWidth=squareHeight=bmpHeight;
            x=(bmpWidth - bmpHeight) / 2;
            y=0;
            squareBitmap=Bitmap.createBitmap(bmp, x, y, squareWidth, squareHeight);
        }
        else{
            squareBitmap=bmp;
        }
        if(squareBitmap.getWidth() != diameter || squareBitmap.getHeight() != diameter){
            scaledSrcBmp=Bitmap.createScaledBitmap(squareBitmap, diameter, diameter, true);
        }
        else{
            scaledSrcBmp=squareBitmap;
        }
        
        
        
        Bitmap output=Bitmap.createBitmap(scaledSrcBmp.getWidth(), scaledSrcBmp.getHeight(), Config.ARGB_8888);
        Canvas canvas=new Canvas(output);
        Rect rect=new Rect(0, 0, scaledSrcBmp.getWidth(), scaledSrcBmp.getHeight());
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2, scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2, paint);
        // 取两层绘制交集，显示上层
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
        // bitmap回收(recycle导致在布局文件XML看不到效果)
        bmp.recycle();
        squareBitmap.recycle();
        scaledSrcBmp.recycle();
        bmp=null;
        squareBitmap=null;
        scaledSrcBmp=null;
        return output;
    }
    
    private void drawCircleBorder(Canvas canvas, int radius, int color) {
        borderPaint.setColor(color);
        canvas.drawCircle(defaultWidth / 2, defaultHeight / 2, radius, borderPaint);
    }
}