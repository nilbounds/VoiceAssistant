
package com.leaf.voiceassistant.tts;

import android.content.Context;

public abstract class TTS {
    protected boolean enable = false;;
    protected Context context;
    protected ITtsListener listener;

    public TTS(Context c, ITtsListener l) {
        this.context = c;
        this.listener = l;
    }

    public abstract void speak(String str);
    public abstract void close();
    public abstract void stop();
    
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
