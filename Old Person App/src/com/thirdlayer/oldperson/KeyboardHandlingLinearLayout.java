package com.thirdlayer.oldperson;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class KeyboardHandlingLinearLayout extends LinearLayout {
    
    private KeyboardListener mCallbackKeyboard;

    public KeyboardHandlingLinearLayout(Context context) {
        super(context);
        try {
            mCallbackKeyboard = (KeyboardListener) getContext();
        } catch (ClassCastException e) {
            throw new ClassCastException(getContext().toString() + " must imlpement KeyboardListener");
        }
        // TODO Auto-generated constructor stub
    }
    
    public KeyboardHandlingLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            mCallbackKeyboard = (KeyboardListener) getContext();
        } catch (ClassCastException e) {
            throw new ClassCastException(getContext().toString() + " must imlpement KeyboardListener");
        }
        // TODO Auto-generated constructor stub
    }
    
    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        if (xNew * yNew < xOld * yOld) {
            mCallbackKeyboard.onKeyboardUp();
        } else {
            mCallbackKeyboard.onKeyboardDown();
        }
    }
    
//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
//        try {
//            mCallbackKeyboard = (KeyboardListener) getContext();
//        } catch (ClassCastException e) {
//            throw new ClassCastException(getContext().toString() + " must imlpement KeyboardListener");
//        }
//    }
    
    public interface KeyboardListener
    {
        public void onKeyboardUp();
        public void onKeyboardDown();
    }

}
