package com.chengbiao.calculator.reWrite;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * 项目名称：Calculator20180415
 * Created by Long on 2018/4/25.
 * 修改时间：2018/4/25 12:57
 */
public class MyHorizontalScrollView extends HorizontalScrollView {
    private static final String TAG ="MyHorizontalScrollView" ;
    private float mOffsetX,mOffsetY;
    private float mLastPosX,mLastPosY;

    public MyHorizontalScrollView(Context context) {
        this(context,null);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result=false;
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mOffsetX=0.0F;
                mOffsetY=0.0F;
                mLastPosX=ev.getX();
                mLastPosY=ev.getY();
                result= super.onInterceptTouchEvent(ev);//false手势传递给子控件
                break;
                default:
                    float thisPosX=ev.getX();
                    float thisPosY=ev.getY();
                    Log.i(TAG, "onInterceptTouchEvent: thisposx,y：("+thisPosX+","+thisPosY+"）");
                    Log.i(TAG, "onInterceptTouchEvent: 初始mOffsetX，Y：("+mOffsetX+","+mOffsetY+"）");
                    mOffsetX+=Math.abs(thisPosX-mLastPosX);//x偏移
                    mOffsetY+=Math.abs(thisPosY-mLastPosY);//y轴偏移
                    Log.i(TAG, "onInterceptTouchEvent: 偏移后 mOffsetX，Y：("+mOffsetX+","+mOffsetY+"）");
                    Log.i(TAG, "onInterceptTouchEvent: 初始mLastPosX，Y：("+mLastPosX+","+mLastPosY+"）");

                    mLastPosY=thisPosY;
                    mLastPosX=thisPosX;
                    Log.i(TAG, "onInterceptTouchEvent: 之后mLastPosX，Y：("+mLastPosX+","+mLastPosY+"）");
                    if(mOffsetX<3&&mOffsetY<3)
                        result=false;//传给子控件
                    else if(mOffsetY<mOffsetX)
                        result =true;//不传给子控件，自己水平滑动
                    else
                        result=false;//传给子控件
                    break;
        }
        Log.i(TAG, "111111111111onInterceptTouchEvent: result:"+result);
        return result;
    }

}
