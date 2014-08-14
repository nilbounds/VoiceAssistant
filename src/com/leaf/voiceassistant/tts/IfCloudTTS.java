
package com.leaf.voiceassistant.tts;

import android.content.Context;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.iflytek.cloud.speech.SpeechUser;
import com.iflytek.cloud.speech.SynthesizerListener;

public class IfCloudTTS extends TTS implements SynthesizerListener {
    private SpeechSynthesizer player;

    public IfCloudTTS(final Context c, ITtsListener l) {
    	super(c, l);
    	 SpeechUser.getUser().login(context,null,null, "appid=4f0aad6f",null);
    	player=SpeechSynthesizer.createSynthesizer(c);
    	//设置发音人
    	player.setParameter(SpeechConstant.VOICE_NAME,"xiaoyan");
    	//设置语速
    	player.setParameter(SpeechConstant.SPEED, "50");
    	this.enable = true;
    }

    @Override
    public void speak(String str) {
        if (player == null) return;
        player.cancel();
        player.startSpeaking(str, this);
    }


    @Override
    public void close() {
        if (player == null) return;
        player.cancel();
        player.destroy();
        player = null;
    }

    @Override
    public void stop() {
        if (player != null) player.cancel();
    }

	@Override
	public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCompleted(SpeechError arg0) {
		if (listener != null) listener.onTtsResult();
	}

	@Override
	public void onSpeakBegin() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSpeakPaused() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSpeakProgress(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSpeakResumed() {
		// TODO Auto-generated method stub
		
	}
}
