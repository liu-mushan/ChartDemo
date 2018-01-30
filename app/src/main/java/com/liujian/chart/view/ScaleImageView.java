package com.liujian.chart.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * 支持手势缩放，移动，双击放大的ImageView的demo
 *
 * @author : liujian
 * @since : 2018/1/1
 */

@SuppressLint("AppCompatCustomView")
public class ScaleImageView extends ImageView implements View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener {
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private final Matrix matrix = new Matrix();
    private final float[] matrixValues = new float[9];
    private static final float SCALE_MAX = 4.0f;

    private float initScale = 1.0f;
    private boolean once = true;

    private float mLastX;
    private float mLastY;
    private int mLastPointerCount;

    private int mTouchSlop;

    private boolean isCheckTopAndBottom;
    private boolean isCheckLeftAndRight;


    public ScaleImageView(Context context) {
        this(context, null);
    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setScaleType(ScaleType.MATRIX);
        //缩放手势监听
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleTapGestureListener());
        //双击手势监听
        gestureDetector = new GestureDetector(context, new DoubleTapGestureListener());
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //缩放
        scaleGestureDetector.onTouchEvent(event);
        //双击放大
        gestureDetector.onTouchEvent(event);
        //图片的移动操作
        if (getDrawable() == null) {
            return true;
        }

        float x = 0, y = 0;
        //多点触控，取平均值
        int pointerCount = event.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x = x / pointerCount;
        y = y / pointerCount;

        //没有使用ACTION_DOWN是因为第二个手指按下不会触发ACTION_DOWN
        if (pointerCount != mLastPointerCount) {
            mLastX = x;
            mLastY = y;
        }
        mLastPointerCount = pointerCount;

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mLastX;
                float dy = y - mLastY;
                if (isCanDrag(dx, dy)) {
                    isCheckLeftAndRight = isCheckTopAndBottom = true;
                    RectF matrixRectF = getMatrixRectF();
                    //如果宽度小于屏幕宽度，禁止左右移动
                    if (matrixRectF.width() < getWidth()) {
                        dx = 0;
                        isCheckLeftAndRight = false;
                    }
                    // 如果高度小于屏幕高度，则禁止上下移动
                    if (matrixRectF.height() < getHeight()) {
                        dy = 0;
                        isCheckTopAndBottom = false;
                    }
                    matrix.postTranslate(dx, dy);
                    checkMatrixBoundsWhenMove();
                    setImageMatrix(matrix);
                }
                mLastY = y;
                mLastX = x;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastPointerCount = 0;
                break;
        }
        return true;
    }

    private boolean isCanDrag(float dx, float dy) {

        return Math.sqrt(dx * dx + dy * dy) > mTouchSlop;
    }


    /**
     * 移动时，进行边界判断，主要判断宽或高大于屏幕的
     */
    private void checkMatrixBoundsWhenMove() {
        RectF rect = getMatrixRectF();

        float deltaX = 0, deltaY = 0;
        final float viewWidth = getWidth();
        final float viewHeight = getHeight();
        // 判断移动或缩放后，图片显示是否超出屏幕边界
        if (rect.top > 0 && isCheckTopAndBottom) {
            deltaY = -rect.top;
        }
        if (rect.bottom < viewHeight && isCheckTopAndBottom) {
            deltaY = viewHeight - rect.bottom;
        }
        if (rect.left > 0 && isCheckLeftAndRight) {
            deltaX = -rect.left;
        }
        if (rect.right < viewWidth && isCheckLeftAndRight) {
            deltaX = viewWidth - rect.right;
        }
        matrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 在缩放时，进行图片显示范围的控制
     */
    private void checkBorderAndCenterWhenScale() {
        //当前Drawable宽高组成的矩形
        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        // 如果宽或高大于屏幕，则控制范围
        if (rect.width() >= width) {
            if (rect.left > 0) {
                deltaX = -rect.left;
            }
            if (rect.right < width) {
                deltaX = width - rect.right;
            }
        }
        if (rect.height() >= height) {
            if (rect.top > 0) {
                deltaY = -rect.top;
            }
            if (rect.bottom < height) {
                deltaY = height - rect.bottom;
            }
        }
        // 如果宽或高小于屏幕，则让其居中
        if (rect.width() < width) {
            deltaX = width * 0.5f - rect.right + 0.5f * rect.width();
        }
        if (rect.height() < height) {
            deltaY = height * 0.5f - rect.bottom + 0.5f * rect.height();
        }
        Log.e("TAG", "deltaX = " + deltaX + " , deltaY = " + deltaY);
        matrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 根据当前图片的Matrix获得图片的范围
     *
     * @return RectF
     */
    private RectF getMatrixRectF() {
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (null != d) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    /**
     * 获得当前的缩放比例
     *
     * @return 缩放比例
     */
    public final float getScale() {
        matrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    @Override
    public void onGlobalLayout() {
        if (once) {
            once = false;
            Drawable drawable = getDrawable();
            if (drawable == null) {
                return;
            }
            int width = getWidth();
            int height = getHeight();
            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();
            float scale = 1.0f;
            if (drawableWidth > width && drawableHeight <= height) {
                scale = width * 1.0f / drawableWidth;
            }
            if (drawableHeight > height && drawableWidth <= width) {
                scale = height * 1.0f / drawableHeight;
            }
            if (drawableHeight > height && drawableWidth > width) {
                scale = Math.min(width * 1.0f / drawableWidth, height * 1.0f / drawableHeight);
            }
            initScale = scale;
            //drawable平移到屏幕中心
            matrix.postTranslate((width - drawableWidth) / 2, (height - drawableHeight) / 2);
            //缩放，根据屏幕中心为中点
            matrix.postScale(scale, scale, width / 2, height / 2);
            setImageMatrix(matrix);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    private class ScaleTapGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scale = getScale();
            float scaleFactor = detector.getScaleFactor();
            Log.i("TAG", "onScale: " + scale);
            Log.i("TAG", "scaleFactor: " + scaleFactor);
            Log.i("TAG", "initScale: " + initScale);

            if ((scale < SCALE_MAX && scaleFactor > 1.0f)
                    || (scale > initScale && scaleFactor < 1.0f)) {
                //以缩放两点的中点为缩放中点缩放
                matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(),
                        detector.getFocusY());
                checkBorderAndCenterWhenScale();
                setImageMatrix(matrix);
            }
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            //一定要返回true，不然onScale方法不会执行
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }
    }

    private class DoubleTapGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            final float x = e.getX();
            final float y = e.getY();

            ScaleImageView.this.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (getScale() >= SCALE_MAX) {
                        matrix.postScale(initScale / getScale(), initScale / getScale(), x, y);
                    } else {
                        matrix.postScale(2f, 2f, x, y);
                    }
                }
            }, 50);
            checkBorderAndCenterWhenScale();
            setImageMatrix(matrix);
            return true;
        }
    }

    /**
     * 慢慢的缩放，postDelay的一个Runnable，可以用属性动画实现
     */
    private class AutoScaleRunnable implements Runnable {
        static final float BIGGER = 1.07f;//一次放大的比例
        static final float SMALLER = 0.93f;//一次缩小的比例
        private float mTargetScale;
        private float tmpScale;

        /**
         * 缩放的中心
         */
        private float x;
        private float y;

        /**
         * 传入目标缩放值，根据目标值与当前值，判断应该放大还是缩小
         *
         * @param targetScale
         */
        public AutoScaleRunnable(float targetScale, float x, float y) {
            this.mTargetScale = targetScale;
            this.x = x;
            this.y = y;
            if (getScale() < mTargetScale) {
                tmpScale = BIGGER;
            } else {
                tmpScale = SMALLER;
            }
        }

        @Override
        public void run() {
            // 进行缩放
            matrix.postScale(tmpScale, tmpScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(matrix);

            final float currentScale = getScale();
            //如果值在合法范围内，继续缩放
            if (((tmpScale > 1f) && (currentScale < mTargetScale))
                    || ((tmpScale < 1f) && (mTargetScale < currentScale))) {
                //循环递归的缩放，属性动画替换
                ScaleImageView.this.postDelayed(this, 16);
            } else//设置为目标的缩放比例
            {
                final float deltaScale = mTargetScale / currentScale;
                matrix.postScale(deltaScale, deltaScale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(matrix);
            }

        }
    }
}
