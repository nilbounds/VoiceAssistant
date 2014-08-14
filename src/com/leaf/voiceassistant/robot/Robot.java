
package com.leaf.voiceassistant.robot;

import android.content.Context;

import com.leaf.voiceassistant.DialogParams;
import com.leaf.voiceassistant.IView;

public abstract class Robot {
    protected boolean enable = false;
    protected IRobotListener listener;
    protected Context context;
    protected IView view;

    public Robot(Context c, IRobotListener listener, IView v) {
        context = c;
        this.listener = listener;
        this.view = v;
    }

    public void ask(String question) {
        view.showProgress(new DialogParams("我正在思考,请稍后", false, null, null));
        new ThreadProcess(question).start();
    }

    public abstract String getAnswer(String question);

    public abstract void close();

    class ThreadProcess extends Thread {
        String question = null;

        public ThreadProcess(String quest) {
            question = quest;
        }

        public void run() {
            String answer = getAnswer(question);
            view.hideDialog();
            if (listener != null) listener.onRobotResult(answer);
        }
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
