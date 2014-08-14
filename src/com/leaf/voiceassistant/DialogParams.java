
package com.leaf.voiceassistant;

import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;

public class DialogParams {
    public boolean cancelable;
    public String message;
    public OnCancelListener cancellistener;
    public OnClickListener clickListener;

    public DialogParams(String message, boolean cancelable, OnCancelListener cancelListener, OnClickListener clickListener) {
        this.message = message;
        this.cancelable = cancelable;
        this.cancellistener = cancelListener;
        this.clickListener = clickListener;
    }
}
