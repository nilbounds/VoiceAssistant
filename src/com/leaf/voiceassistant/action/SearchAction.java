/*
 *  http://www.guet.edu.cn/
 *  by hmg25 20111212
 *  Just For Learning
 */

package com.leaf.voiceassistant.action;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.leaf.voiceassistant.ISpeaker;
import com.leaf.voiceassistant.IView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchAction extends BaseAction {
    private Pattern pattern;
    private String engine = "";
    public SearchAction(Context c, ISpeaker speaker, IView view, IActionListener l) {
        super(c, speaker, view, l);
        pattern = Pattern.compile("(.*)(搜索|查询)(.*)");
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean act(String str) {
    	Matcher matcher = pattern.matcher(filter(str));
        if (!matcher.matches()) return false;
        engine = matcher.group(1);
        if(engine == null)engine = "";
        String keyword = matcher.group(3).trim();
        if (keyword.length() == 0) {
        	tooltip("我需要一个关键词:");
            waiting = true;
        }else{
        	search(engine, keyword);
        }
        return true;
    }

    private void search(String engine, String keyword) {
        Intent intent = new Intent();
        if (engine.contains("百度")) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://m.baidu.com/s?word=" + keyword));
        } else {
            intent.setAction("android.intent.action.WEB_SEARCH");
            intent.putExtra("query", keyword);
        }
        engine = "";
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://m.baidu.com/s?word=" + keyword));
            context.startActivity(intent);
        }
        if(listener != null)listener.onActDone(this);
    }

	@Override
	public boolean done() {
		return true;
	}

	@Override
	public void append(String str) {
    	if(str.length() > 0){
    		search(engine, str);
    		waiting = false;
    	}else{
    		tooltip("请说出搜索关键词");
    	}
	}
}
