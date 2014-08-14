/*
 *  http://www.guet.edu.cn/
 *  by hmg25 20111212
 *  Just For Learning
 */

package com.leaf.voiceassistant.action;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.leaf.voiceassistant.ISpeaker;
import com.leaf.voiceassistant.IView;

public class OpenAppAction extends BaseAction {
    private Pattern pattern;

    public OpenAppAction(Context c, ISpeaker speaker,IView view,IActionListener l) {
        super(c, speaker,view,l);
        pattern = Pattern.compile("(.*)(打开|运行|启动)(.*)");
    }

    @Override
    public boolean act(String str) {
        Matcher matcher = pattern.matcher(filter(str));
        if (!matcher.matches()) return false;
        String app = matcher.group(3).trim();
        if(app.length()==0){
        	tooltip("请说出要打开的程序名字");
            waiting = true;
            return true;
        }
        if (!openAppByName(app)) alert("没有找到名字是"+app+"的应用程序");
        return true;
    }

    private boolean openAppByName(String appName) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> installAppList = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo info : installAppList) {
            String name = info.loadLabel(pm).toString();
            if (name.equalsIgnoreCase(appName)) {
            	openApplicaton(info);
                if(listener != null)listener.onActDone(this);
                return true;
            }
        }
        for (ResolveInfo info : installAppList) {
            String name = info.loadLabel(pm).toString();
            if (name.contains(appName)) {
            	openApplicaton(info);
                if(listener != null)listener.onActDone(this);
                return true;
            }
        }
        if(listener != null)listener.onActDone(this);
        return false;
    }
    
    private void openApplicaton(ResolveInfo info){
    	Intent intent;
    	PackageManager pm = context.getPackageManager();
        String pkgname = info.activityInfo.packageName;
        if ("com.android.contacts".equalsIgnoreCase(pkgname)) {
            Uri uri = Uri.parse("content://contacts/people");
            intent = new Intent("android.intent.action.VIEW", uri);
            context.startActivity(intent);
        } else {
            intent = pm.getLaunchIntentForPackage(pkgname);
            intent.addCategory("android.intent.category.LAUNCHER");
            context.startActivity(intent);
        }
    }
    
    @Override
    public void cancel() {
    	if(waiting)alert("打开已取消");
    	super.cancel();
    }
    
	@Override
	public void stop() {
		
	}

	@Override
	public void append(String str) {
		if(str.length()>0){
			waiting = false;
        	if (!openAppByName(str.trim())) alert("没有找到名字是"+str.trim()+"的应用程序");
		}else{
			tooltip("请说出要打开的应用名称");
		}
	}
}
