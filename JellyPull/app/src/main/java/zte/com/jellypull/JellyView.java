package zte.com.jellypull;


import android.animation.FloatEvaluator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;


/**
 * Created by chyl411 on 2017/12/19.
 */

public class JellyView extends View {
    private Paint mPaint;
    private int minHeight;
    private int mLastX;
    private int mLastY;
    private float controlX;
    private float controlY;

    public JellyView(Context context) {
        this(context, null);
    }

    public JellyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JellyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init()
    {
        minHeight = 0;

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.rgb(20, 200, 20));
        mPaint.setAlpha(80);

//        setWillNotDraw(false);
//        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        controlX = w / 2;
        controlY = 0;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        int width = getWidth();

        Path path = new Path();
        path.moveTo(-50, 0);
        //path.lineTo(-50, minHeight);
        path.quadTo(controlX, controlY, width + 50, minHeight);
        //path.lineTo(width + 50, 0);
        path.close();

        canvas.drawPath(path, mPaint);
    }

    public void refreshJellyView(int cx, int cy)
    {
        controlX = cx;
        controlY = (int)(2 * minHeight + cy * 0.5);
        invalidate();
    }

    public void refreshJellyViewByControlPoint(float cx, float cy)
    {
        controlX = cx;
        controlY = cy;
        invalidate();
    }

    public void restore()
    {
        //1.调用ofInt(int...values)方法创建ValueAnimator对象
        ValueAnimator mAnimator = ValueAnimator.ofFloat(controlY, 0f);
        //2.为目标对象的属性变化设置监听器
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 3.为目标对象的属性设置计算好的属性值
                Float animatorValue = (Float)animation.getAnimatedValue();
                refreshJellyViewByControlPoint(controlX, animatorValue);

                if(animatorValue == 0)
                {
                    JellyView.this.setVisibility(View.GONE);
                    JellyView.this.invalidate();
                }
            }
        });
        //4.设置动画的持续时间、是否重复及重复次数等属性
        mAnimator.setDuration(1000);
        mAnimator.setInterpolator(new BounceInterpolator());
        mAnimator.start();
    }

//    public class DampingEvaluator implements TypeEvaluator<Float> {
//        private float dampingFactor = 30f;
//        private float velocityFactor = 20;
//
//        @Override
//        public Float evaluate(float fraction, Float startValue, Float endValue) {
//
//            float diff = endValue - startValue;
//
//            return endValue + (float) (diff * Math.pow(Math.E, -1 * dampingFactor * fraction) * Math.cos(velocityFactor * fraction));
//        }
//    }
}
