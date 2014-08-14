
package com.leaf.voiceassistant.action;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import com.leaf.voiceassistant.ISpeaker;
import com.leaf.voiceassistant.IView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CallAction extends BaseAction {
    private String number;
    private Pattern pattern;

    public CallAction(Context c, ISpeaker speaker,IView view,IActionListener l) {
        super(c, speaker,view,l);
        pattern = Pattern.compile("(.*)(拨打|呼叫|call|打电话)(.*)");
    }

    @Override
    public void cancel() {
    	if(waiting){
    		alert("呼叫已取消");
    	}
    	super.cancel();
    }
    
    @Override
    public boolean act(String str) {
        Matcher matcher = pattern.matcher(filter(str));
        if (!matcher.matches()) return false;
        String person = matcher.group(3).trim();
        if (person == null ||person.length() == 0) {
        	tooltip("我需要一个名字");
            waiting = true;
            return true;
        }
        if (!makeCall(person)) alert("没有找到名字是" + person + "的联系人");
        return true;
    }

    public boolean makeCall(String person) {
        if ((person == null) || (person.equals(""))) return false;
        person = person.trim();
        number = null;
        
        if (isPhoneNumber(person)) number = person;
        else number = getNumberByName(person);

        if (number != null) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
            context.startActivity(intent);
            if(listener != null)listener.onActDone(this);
            return true;
        } else {
        	if(listener != null)listener.onActDone(this);
            return false;
        }
    }

    private boolean isPhoneNumber(String num) {
        return num.matches("^\\d+\\D?$");
    }

    private String getNumberByName(String name) {
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, name);
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[] { ContactsContract.Contacts._ID }, null, null, null);
        if ((cursor != null) && (cursor.moveToFirst())) {
            int idCoulmn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            long id = cursor.getLong(idCoulmn);
            cursor.close();
            cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[] { "data1" }, "contact_id = ?", new String[] { Long.toString(id) }, null);
            if ((cursor != null) && (cursor.moveToFirst())) {
                int m = cursor.getColumnIndex("data1");
                String num = cursor.getString(m);
                cursor.close();
                return num;
            }
        }
        return null;
    }

	@Override
	public void stop() {
		
	}

	@Override
	public void append(String str) {
		if(str.length() == 0)tooltip("请说出一个名字或者号码");
		if (!makeCall(str)) alert("没有找到名字是" + str + "的联系人");	
		waiting = false;
	}

}
