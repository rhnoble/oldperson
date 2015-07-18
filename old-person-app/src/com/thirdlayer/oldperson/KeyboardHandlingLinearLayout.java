package com.thirdlayer.oldperson;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class KeyboardHandlingLinearLayout extends LinearLayout {
    ResizeListener resizeListener = null;

    public KeyboardHandlingLinearLayout(Context context) {
        super(context);
    }
    
    public KeyboardHandlingLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public void SetOnResizeListener(ResizeListener resizeListener)
    {
        this.resizeListener = resizeListener;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }
    
    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        if(resizeListener != null)
        {
            resizeListener.onResize(this.getId(), xNew, yNew, xOld, yOld);
        }
        post(new Runnable() {
            public void run() {
                requestLayout();
            }
        });
    }
}
