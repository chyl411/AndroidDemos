package zte.com.jellypull;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by chyl411 on 2017/12/19.
 */

public class JellyScrollView extends FrameLayout {
    private LinearLayout jv;
    private JellyView jView;
    private Context context;
    private int downX;
    private int downY;

    public JellyScrollView(Context context) {
        this(context, null);
    }

    public JellyScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JellyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        //init();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        jv = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.jelly_view_layout, null, false);

        this.addView(jv);
        jv.setVisibility(View.GONE);
        jView = jv.findViewById(R.id.jv_id);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        boolean intercept = false;
        int action = event.getAction();
        int x = (int)event.getX();
        int y = (int)event.getY();

        if(action == MotionEvent.ACTION_DOWN)
        {
            downX = x;
            downY = y;
            return super.onInterceptTouchEvent(event);
        }
        else if(action == MotionEvent.ACTION_MOVE)
        {
            int deltaY = y - downY;
            //设置一个阀值，避免手指抖动误判
            if(deltaY > 0 && getFirstChildScrollY() == 0)
            {
                intercept = true;
                jv.setVisibility(View.VISIBLE);
                return true;
            }
        }

        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:{

                break;
            }
            case MotionEvent.ACTION_MOVE:{
                int deltaY = y - downY;

                if(deltaY > 0)
                {
                    int controlX = x;
                    int controlY = deltaY;

                    jv.setVisibility(View.VISIBLE);
                    jv.invalidate();
                    jView.refreshJellyView(controlX, controlY);
                }
                break;
            }
            case MotionEvent.ACTION_UP:{
                jView.restore();
                break;
            }
            default:
                break;

        }

        return true;
    }

    public int getFirstChildScrollY()
    {
        return this.getChildAt(0).getScrollY();
    }
}
