
package com.thirdlayer.oldperson;

import java.util.ArrayList;

public class FragmentBackStack {
    private ArrayList<String> mFragmentStack;

    public FragmentBackStack() {
        mFragmentStack = new ArrayList<String>();
    }

    void addToStack(String mNewFragment) {
        if (mFragmentStack.contains(mNewFragment)) {
            mFragmentStack.remove(mNewFragment);
        }
        mFragmentStack.add(mNewFragment);
    }

    String onBackPressed() {
        if (mFragmentStack.size() == (1)) {
            return "empty stack";
        }
        mFragmentStack.remove(mFragmentStack.size() - 1);
        String mFragmentToRaise = mFragmentStack.get(mFragmentStack.size() - 1);
        return mFragmentToRaise;
    }
}
