package myapplication.mynewsapp.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by ttslso on 2016/3/29.
 */
//自定义点击事件动画
public class RevealBackgroundView extends View {

    public static final int STATE_NOT_STARTED = 0;//未开始
    public static final int STATE_FILL_STARTED = 1;//开始
    public static final int STATE_FINISHED = 2;//结束

    private static final Interpolator INTERPOLATOR = new AccelerateInterpolator();
    private static final int FILL_TIME = 400;

    private int state = STATE_NOT_STARTED;

    private Paint fillPaint;
    private int currentRadius;
    ObjectAnimator revealAnimator;

    private int startLocationX;
    private int startLocationY;

    private OnStateChangeListener onStateChangeListener;

    public RevealBackgroundView(Context context){
        super(context);
        init();
    }

    public RevealBackgroundView(Context context,AttributeSet attrs){
        super(context,attrs);
        init();
    }

    public RevealBackgroundView(Context context,AttributeSet attrs,int defStyleRes){
        super(context,attrs,defStyleRes);
        init();
    }

    public void init(){
        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.BLACK);
    }

    public void startFromLocation(int[] tapLocationOnScreen){
        changeState(STATE_FILL_STARTED);
        startLocationX = tapLocationOnScreen[0];
        startLocationY = tapLocationOnScreen[1];
        revealAnimator = ObjectAnimator.ofInt(this,"currentRadius",0,getWidth()+getHeight())
                .setDuration(FILL_TIME);
        revealAnimator.setInterpolator(INTERPOLATOR);
        revealAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                changeState(STATE_FINISHED);
            }
        });
        revealAnimator.start();
    }

    public void setToFinishedFrame(){
        changeState(STATE_FINISHED);
        invalidate();
    }

    @Override
    //绘制指定半径和圆心的圆
    //在动画结束之后绘制View的矩阵
    protected void onDraw(Canvas canvas){
        if (state == STATE_FINISHED){
            canvas.drawRect(0,0,getWidth(),getHeight(),fillPaint);
        }else{
            canvas.drawCircle(startLocationX,startLocationY,currentRadius,fillPaint);
        }
    }

    private void changeState(int state){
        if(this.state == state){
            return;
        }
        this.state = state;
        if (onStateChangeListener !=null){
            onStateChangeListener.onStateChange(state);
        }
    }

    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener){
        this.onStateChangeListener = onStateChangeListener;
    }

    public void setCurrentRadius(int radius){
        this.currentRadius = radius;
        invalidate();
    }

    public static interface OnStateChangeListener{
        void onStateChange(int state);
    }


}
