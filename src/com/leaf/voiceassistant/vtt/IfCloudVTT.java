
package com.leaf.voiceassistant.vtt;

import android.content.Context;
import android.util.Log;

import com.iflytek.cloud.speech.RecognizerListener;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechRecognizer;
import com.iflytek.cloud.speech.SpeechUser;
import com.leaf.voiceassistant.IView;
import com.iflytek.cloud.speech.RecognizerResult;

public class IfCloudVTT extends VTT implements RecognizerListener {
    private SpeechRecognizer recognizer = null;
    private String text = "";

    public IfCloudVTT(Context c, IVttListener listener, IView v) {
        super(c, listener, v);
        SpeechUser.getUser().login(context,null,null, "appid=4f0aad6f",null);
        recognizer = SpeechRecognizer.createRecognizer(c);
        recognizer. setParameter(SpeechConstant.DOMAIN,"iat");
        recognizer. setParameter(SpeechConstant.LANGUAGE,"zh_cn");
        recognizer.setParameter(SpeechConstant.ASR_PTT, "0");
        recognizer.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "1");
        recognizer.setParameter(SpeechConstant.KEY_SPEECH_TIMEOUT, "2000");
        recognizer.setParameter(SpeechConstant.NET_TIMEOUT,"5000");
        recognizer.setParameter(SpeechConstant.RESULT_TYPE, "plain");
        
        //语言区域可选：mandarin(普通话，默认)，cantonese(粤语)
        //recognizer. setParameter(SpeechConstant.ACCENT,"mandarin");
        
        setEnable(true);
    }

    @Override
    public void listen() {
        if (recognizer == null)return;
        recognizer.startListening(this);
    }

    @Override
    public void close() {
        if (recognizer != null) {
        	recognizer.destroy();
            recognizer = null;
        }
    }

	@Override
	public void onBeginOfSpeech() {
	}

	@Override
	public void onEndOfSpeech() {
	}

	@Override
	public void onError(com.iflytek.cloud.speech.SpeechError error) {
		Log.e("goc", error.getErrorDescription());
	}

	@Override
	public void onEvent(int eventType, int arg1, int arg2, String msg) {
		
	}

	@Override
	public void onResult(RecognizerResult result,boolean isLast) {
		text += result.getResultString().trim();
		if(isLast){
			if (listener != null) listener.onVttResult(text);
			text = "";
		}
	}

	@Override
	public void onVolumeChanged(int arg0) {
		
	}

	@Override
	public void stopListen() {
		if(recognizer != null)recognizer.stopListening();
	}

}
