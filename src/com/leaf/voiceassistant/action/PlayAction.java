/*
 *  http://www.guet.edu.cn/
 *  by hmg25 20111212
 *  Just For Learning
 */

package com.leaf.voiceassistant.action;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;

import com.leaf.voiceassistant.DialogParams;
import com.leaf.voiceassistant.ISpeaker;
import com.leaf.voiceassistant.IView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayAction extends BaseAction implements OnCompletionListener, OnCancelListener {

    private Pattern pattern;
    private MediaPlayer mediaPlayer;
    private String name;
    private String path;
    private ProgressDialog progressDialog;
    private HttpClient client;
    private Pattern urlPattern;
    private String who = "";

    public PlayAction(Context c, ISpeaker speaker, IView view, IActionListener l) {
        super(c, speaker, view, l);
        pattern = Pattern.compile("(.*)(播放|play|播一首|放一首|来一首|来首|放首|播首)(.*?唱?的)?(.*)");
        urlPattern = Pattern.compile(".*?<!\\[CDATA\\[(http[^\\]]*?)[^/\\]]*\\]\\].*?<!\\[CDATA\\[([^\\]]*\\.mp3[^\\]]*).*");
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void stop() {
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
        if (mediaPlayer.isPlaying()) mediaPlayer.stop();
    };

    @Override
    public boolean act(String str) {
        Matcher matcher = pattern.matcher(filter(str));
        if (!matcher.matches()) return false;
        String name = matcher.group(4);
        if (name == null) name = "";
        else name = name.trim();
        who = matcher.group(3);
        if (who == null) who = "";
        else who = who.trim();
        if(name.length() == 0){
        	tooltip("请说出歌曲或者视频的名字");
        	waiting = true;
        }else{
        	 play(name);
        }
        return true;
    }

    private void play(String name) {
        if (name.length() == 0 && who.length() == 0) {
            return;
        }
        if (name.length() != 0) {
            if (!checkSDCardMount()) {
                alert("没有发现SD卡");
                return;
            }
            Integer id = getMusicByName(name);
            if (id != null) {
                playMusic();
                // playMusicById(id);
                return;
            }
            id = getVideoByName(name);
            if (id != null) {
                playVideoById(id);
                return;
            }
        }
        view.showProgress(new DialogParams("正在联网查找歌曲,请稍后...", false, null, null));
        new UrlThread(name, who).start();
        who  = "";
    }

    private class UrlThread extends Thread {
        private String name;
        private String who;

        public UrlThread(String name, String who) {
            this.name = name;
            this.who = who;
        }

        @Override
        public void run() {
            String url = getMusicUrl(who, name);
            if (url != null && url.trim().length() != 0) {
                playUrl(this.name, url.trim());
            }else{
            	alert("没有找到名字是" + name + "的歌曲或者视频");
            }
            if(listener != null)listener.onActDone(PlayAction.this);
        }
    }

    private void playUrl(String name, String url) {
        boolean success = false;
        if (mediaPlayer.isPlaying()) mediaPlayer.stop();
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
            view.showProgress(new DialogParams("正在播放:" + name, true, this, null));
            success = true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!success) {
            view.hideDialog();
            alert("播放失败");
        }
    }

    private void playMusic() {
        if (path != null && path.length() != 0) {
            view.showProgress(new DialogParams("正在播放:" + name, true, this, null));
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * private boolean playMusicById(int id) {
     * Uri uri = Uri.withAppendedPath(Media.EXTERNAL_CONTENT_URI,
     * String.valueOf(id));
     * Intent intent = new Intent("android.intent.action.VIEW", uri);
     * context.startActivity(intent);
     * return true;
     * }
     */
    private boolean playVideoById(int id) {
        Uri uri = Uri.withAppendedPath(Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
        Intent intent = new Intent("android.intent.action.VIEW", uri);
        context.startActivity(intent);
        return true;
    }

    private Integer getMusicByName(String n) {
        Cursor cursor = null;
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[3];
        projection[0] = Media._ID;
        projection[1] = Media.TITLE;
        projection[2] = Media.DATA;
        String Where = Media.TITLE + " LIKE" + " '%" + n + "%'";
        cursor = contentResolver.query(uri, projection, Where, null, null);
        if ((cursor != null) && (cursor.moveToFirst())) {
            int column = cursor.getColumnIndex(Media._ID);
            int id = cursor.getInt(column);
            column = cursor.getColumnIndex(Media.TITLE);
            name = cursor.getString(column);
            Log.d("music title", name);
            column = cursor.getColumnIndex(Media.DATA);
            path = cursor.getString(column);
            Log.d("music path", path);
            cursor.close();
            return id;
        } else {
            if (!cursor.isClosed()) cursor.close();
            return null;
        }
    }

    private Integer getVideoByName(String n) {
        Cursor cursor = null;
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[3];
        projection[0] = Media._ID;
        projection[1] = Media.TITLE;
        projection[2] = Media.DATA;
        String Where = Media.TITLE + " LIKE" + " '%" + n + "%'";
        cursor = contentResolver.query(uri, projection, Where, null, null);
        if ((cursor != null) && (cursor.moveToFirst())) {
            int column = cursor.getColumnIndex(Media._ID);
            int id = cursor.getInt(column);
            column = cursor.getColumnIndex(Media.TITLE);
            name = cursor.getString(column);
            column = cursor.getColumnIndex(Media.DATA);
            path = cursor.getString(column);
            cursor.close();
            return id;
        } else {
            if (!cursor.isClosed()) cursor.close();
            return null;
        }
    }

    private boolean checkSDCardMount() {
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // we can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // we can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // something else is wrong.
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }

        if (mExternalStorageAvailable && mExternalStorageWriteable) {
            return true;
        } else {
            return false;
        }
    }

    public String getMusicUrl(String who, String n) {
        if (client == null) client = new DefaultHttpClient();
        HttpGet get = new HttpGet("http://box.zhangmen.baidu.com/x?op=12&count=1&title=" + n + "$$" + who + "$$$$");
        try {
            HttpResponse response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                String content = EntityUtils.toString(response.getEntity());
                if (content == null) return null;
                content = content.trim();
                if (content.length() == 0) return null;
                Matcher matcher = urlPattern.matcher(content);
                if (matcher.matches()) return matcher.group(1) + matcher.group(2);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        view.hideDialog();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (mediaPlayer.isPlaying()) mediaPlayer.stop();
    }
    
    @Override
    public void cancel() {
    	if(waiting)alert("播放已取消");
    	super.cancel();
    }

	@Override
	public void append(String str) {
		 if(str.length() > 0){
			play(str.trim());
		 	waiting = false;
		 }else{
			 tooltip("请说出要播放的歌曲名称");
		 }
	}
}
