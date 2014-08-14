
package com.leaf.voiceassistant.action;

import android.content.Context;

import com.leaf.voiceassistant.ISpeaker;
import com.leaf.voiceassistant.IView;
import com.leaf.voiceassistant.VAListItem;

public abstract class BaseAction {
    private static final String[] ignorePhrase = { "请", "麻烦", "给", "用" };
    protected Context context;
    protected ISpeaker speaker;
    protected IView view;
    protected IActionListener listener;
    protected boolean waiting = false;

    public BaseAction(Context c, ISpeaker speaker, IView view, IActionListener l) {
        this.context = c;
        this.speaker = speaker;
        this.view = view;
        this.listener = l;
    }

    protected void alert(String str) {
        if (speaker != null) speaker.speak(str);
    }
    
    protected void tooltip(String str){
    	if(view != null)view.talk(new VAListItem(str, true));
    }

    public abstract boolean act(String str);

    public abstract void append(String str);
    
    public  abstract void stop();
    
    public boolean isWaiting(){
    	return waiting;
    }
    
    public void cancel(){
    	waiting = false;
    }
    
    public boolean done(){
    	return !waiting;
    };

    protected String filter(String str) {
        String result = str;
        for (String s : ignorePhrase) {
            result = result.replace(s, "");
        }
        return result;
    }
}
