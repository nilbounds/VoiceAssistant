package com.leaf.voiceassistant.vtt;

import android.content.Context;

import com.leaf.voiceassistant.IView;

public abstract class VTT {
    protected boolean enable = false;
    protected Context context;
    protected IVttListener listener;
    protected IView view;

    public VTT(Context c, IVttListener listener, IView v) {
        this.context = c;
        this.listener = listener;
        this.view = v;
    }

    public abstract void listen();

    public abstract void stopListen();
    
    public abstract void close();

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
