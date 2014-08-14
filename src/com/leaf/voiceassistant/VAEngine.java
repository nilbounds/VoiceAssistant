
package com.leaf.voiceassistant;

import android.content.Context;

import com.leaf.voiceassistant.action.BaseAction;
import com.leaf.voiceassistant.action.IActionListener;
import com.leaf.voiceassistant.action.VAAction;
import com.leaf.voiceassistant.robot.ChongDong;
import com.leaf.voiceassistant.robot.IRobotListener;
import com.leaf.voiceassistant.robot.Robot;
import com.leaf.voiceassistant.robot.XiaoI;
import com.leaf.voiceassistant.tts.ITtsListener;
import com.leaf.voiceassistant.tts.IfCloudTTS;
import com.leaf.voiceassistant.tts.SystemTTS;
import com.leaf.voiceassistant.tts.TTS;
import com.leaf.voiceassistant.vtt.IVttListener;
import com.leaf.voiceassistant.vtt.IfCloudVTT;
import com.leaf.voiceassistant.vtt.VTT;

public class VAEngine implements IVttListener, ITtsListener, IRobotListener, ISpeaker, IActionListener {
    public static final int TTS_IFCLOUD = 0;
    public static final int TTS_SYSTEM = 1;

    public static final int ROBOT_XIAOI = 0;
    public static final int ROBOT_CHONGDONG = 1;

    public static final int VTT_IFCLOUD = 0;

    private TTS tts = null;
    private VTT vtt = null;
    private Robot robot = null;
    private VAAction action;

    private boolean alwaysListen = false;
    private Context context;
    private IView view;

    public VAEngine(Context c, IView view) {
        this.context = c;
        this.view = view;
        action = new VAAction(context, this, view, this);
        setTTS(TTS_IFCLOUD);
        setVTT(VTT_IFCLOUD);
        setRobot(ROBOT_XIAOI);
        speak("初始化完成,欢迎使用", true);
    }

    public void stop() {
        tts.stop();
        vtt.stopListen();
        action.stop();
    }

    public void setTTS(int type) {
        if (tts != null) {
            tts.close();
            tts = null;
        }
        if (type == TTS_IFCLOUD) {
            tts = new IfCloudTTS(context, this);
        } else if (type == TTS_SYSTEM) {
            tts = new SystemTTS(context, this);
        }
    }

    public void setRobot(int type) {
        if (robot != null) {
            robot.close();
            robot = null;
        }
        if (type == ROBOT_XIAOI) {
            robot = new XiaoI(context, this, view);
        } else if (type == ROBOT_CHONGDONG) {
            robot = new ChongDong(context, this, view);
        }
    }

    public void setVTT(int type) {
        if (vtt != null) {
            vtt.close();
            vtt = null;
        }
        if (type == VTT_IFCLOUD) {
            vtt = new IfCloudVTT(context, this, view);
        }
    }

    public void listen() {
        if (tts != null) tts.stop();
        if (vtt != null && vtt.isEnable()) vtt.listen();
    }

    public void stopListen(){
    	 if (vtt != null && vtt.isEnable()) vtt.stopListen();
    }
    
    public void ask(String question) {
        view.talk(new VAListItem(question, false));
        if (robot != null && robot.isEnable()) robot.ask(question);
    }

    public void speak(String str, boolean show) {
        if (show) view.talk(new VAListItem(str, true));
        if (tts != null && tts.isEnable()) tts.speak(str);
    }

    public void speak(String str) {
        speak(str, true);
    }

    @Override
    public void onRobotResult(String result) {
        speak(result);
    }

    @Override
    public void onTtsResult() {
        if (alwaysListen) listen();
    }

    @Override
    public void onVttResult(String result) {
    	view.listenDone();
        if (result == null) result = "";
        result = result.trim();
        view.talk(new VAListItem(result, false));
        if (action.act(result)) return;
        if (result.length() == 0) return;
        if (robot != null && robot.isEnable()) robot.ask(result);
    }

    public boolean isAlwaysListen() {
        return alwaysListen;
    }

    public void setAlwaysListen(boolean alwaysListen) {
        this.alwaysListen = alwaysListen;
    }

    @Override
    public void onActDone(BaseAction action) {

    }

	@Override
	public void onActContinue(BaseAction action) {
		if(vtt != null)vtt.listen();
	}
}
