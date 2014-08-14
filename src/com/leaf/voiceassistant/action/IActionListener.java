package com.leaf.voiceassistant.action;

public interface IActionListener {
    public void onActDone(BaseAction action);
    public void onActContinue(BaseAction action);
}
