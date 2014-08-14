
package com.leaf.voiceassistant;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements OnItemClickListener, OnClickListener, IView {
    public static final int MSG_TALK = 0;
    public static final int MSG_SHOW_DIALOG = 1;
    public static final int MSG_SHOW_PROGRESS = 2;
    public static final int MSG_HIDE_DIALOG = 3;
    public static final int MSG_LISTEN_DONE = 4;

    private VAEngine vaEngine;
    private ListView listView;
    private List<VAListItem> list;
    private ChatMsgViewAdapter adapter;
    private ImageButton speakButton;
    private Dialog dialog = null;
    private boolean listening = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!Util.CheckNetwork(this)) {
            showDialog(new DialogParams("联网失败", true, new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            }, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }));
            return;
        }
        new Thread() {
            @Override
            public void run() {
                showProgress(new DialogParams("正在加载请稍后", false, null, null));
                vaEngine = new VAEngine(MainActivity.this, MainActivity.this);
                hideDialog();
            };
        }.start();
        list = new ArrayList<VAListItem>();
        adapter = new ChatMsgViewAdapter(this, list);
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setFastScrollEnabled(true);
        speakButton = (ImageButton) findViewById(R.id.voice_input);
        speakButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_exit) {
            vaEngine.stop();
            finish();
        } else if (item.getItemId() == R.id.menu_clear) {
            list.clear();
            adapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        VAListItem item = list.get(position);
        if (item.isVA) {
            vaEngine.speak(item.message, false);
            showDialog(new DialogParams(item.message, true, null, null));
        }

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == MSG_TALK) {
                VAListItem item = (VAListItem) msg.obj;
                addToList(item);
            } else if (msg.what == MSG_SHOW_DIALOG) {
                doShowDialog((DialogParams) msg.obj);
            } else if (msg.what == MSG_SHOW_PROGRESS) {
                doShowProgress((DialogParams) msg.obj);
            } else if (msg.what == MSG_HIDE_DIALOG) {
                doHideDialog();
            }else if(msg.what == MSG_LISTEN_DONE){
            	speakButton.setImageResource(R.drawable.siri_button);
            	listening = false;
            }
        }
    };

    private void doHideDialog() {
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
    }

    private void doShowDialog(DialogParams p) {
        doHideDialog();
        AlertDialog d = new AlertDialog.Builder(MainActivity.this).create();
        d.setTitle("提示");
        d.setMessage(p.message);
        d.setCancelable(p.cancelable);
        d.setButton(DialogInterface.BUTTON_NEGATIVE, "确定", p.clickListener);
        dialog = d;
        d.show();
    }

    private void doShowProgress(DialogParams params) {
        doHideDialog();
        dialog = ProgressDialog.show(MainActivity.this, "提示", params.message, true, params.cancelable, params.cancellistener);
    }

    private void addToList(VAListItem item) {
        list.add(item);
        adapter.notifyDataSetChanged();
        listView.setSelection(list.size() - 1);
    }

    @Override
    public void onClick(View v) {
    	if(listening){
    		vaEngine.stopListen();
    		speakButton.setImageResource(R.drawable.siri_button);
    	}else{
    		vaEngine.listen();
    		speakButton.setImageResource(R.drawable.siri_button_running);
    	}
    	
    	listening = !listening;
    }

    @Override
    public void talk(VAListItem item) {
        handler.sendMessage(handler.obtainMessage(MSG_TALK, item));
    }

    @Override
    public void showDialog(DialogParams params) {
        handler.sendMessageAtFrontOfQueue(handler.obtainMessage(MSG_SHOW_DIALOG, params));
    }

    @Override
    public void showProgress(DialogParams params) {
        handler.sendMessage(handler.obtainMessage(MSG_SHOW_PROGRESS, params));
    }

    public void hideDialog() {
        handler.sendEmptyMessage(MSG_HIDE_DIALOG);
    }

    @Override
    protected void onDestroy() {
        if (vaEngine != null) vaEngine.stop();
        super.onDestroy();
    }

	@Override
	public void listenDone() {
		handler.sendMessage(handler.obtainMessage(MSG_LISTEN_DONE, null));
	}
}
