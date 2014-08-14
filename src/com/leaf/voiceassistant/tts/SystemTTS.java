
package com.leaf.voiceassistant.tts;

import android.content.Context;
import android.speech.tts.TextToSpeech;

public class SystemTTS extends TTS {
    private TextToSpeech tts;

    public SystemTTS(Context c, ITtsListener l) {
        super(c, l);
        tts = new TextToSpeech(c, null);
        this.enable = true;
    }

    @Override
    public void speak(String str) {
        tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void close() {
        tts.shutdown();
    }

    @Override
    public void stop() {
        tts.stop();
    }

}
