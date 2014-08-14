
package com.leaf.voiceassistant.action;

import android.content.Context;
import com.leaf.voiceassistant.ISpeaker;
import com.leaf.voiceassistant.IView;

import java.util.ArrayList;
import java.util.List;

public class VAAction {
    private ISpeaker speaker;
    private Context context;
    private IView view;
    private IActionListener actionListener;

    private List<BaseAction> actions;

    public VAAction(Context c, ISpeaker s, IView v, IActionListener l) {
        this.context = c;
        this.speaker = s;
        this.view = v;
        this.actionListener = l;
        actions = new ArrayList<BaseAction>();
        actions.add(new CallAction(context, speaker, view, actionListener));
        actions.add(new OpenAppAction(context, speaker, view, actionListener));
        actions.add(new PlayAction(context, speaker, view, actionListener));
        actions.add(new SearchAction(context, speaker, view, actionListener));
    }

    public void stop() {
        for (BaseAction action : actions) {
            action.stop();
        }
    }
    
    
    public boolean act(String what) {
    	if(what == null)return true;
    	what = what.trim();
    	if(what.equals("取消")){
            for (BaseAction action : actions) {
                action.cancel();
            }
            return true;
    	}
        for (BaseAction action : actions) {
        	if(action.isWaiting()){
        		action.append(what);
        		if(action.waiting && actionListener != null)actionListener.onActContinue(action);
        		return true;
        	}
            if (what.length()>0 && action.act(what)){
            	if(action.waiting && actionListener != null)actionListener.onActContinue(action);
            	return true;
            }
        }
        return false;
    }
}
